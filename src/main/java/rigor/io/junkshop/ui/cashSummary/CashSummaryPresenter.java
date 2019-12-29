package rigor.io.junkshop.ui.cashSummary;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.customProperties.CustomProperty;
import rigor.io.junkshop.models.customProperties.CustomPropertyHandler;
import rigor.io.junkshop.models.customProperties.CustomPropertyKeys;
import rigor.io.junkshop.models.expense.Expense;
import rigor.io.junkshop.models.expense.ExpenseFX;
import rigor.io.junkshop.models.expense.ExpenseHandler;
import rigor.io.junkshop.models.junk.JunkCollector;
import rigor.io.junkshop.models.sales.SalesEntity;
import rigor.io.junkshop.models.sales.SalesFX;
import rigor.io.junkshop.models.sales.SalesMan;
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CashSummaryPresenter implements Initializable {

  @FXML
  private TableView<SalesFX> salesTable;
  @FXML
  private Label loadingLabel;
  @FXML
  private TableView<ExpenseFX> expensesTable;
  @FXML
  private JFXDatePicker datePicker;
  @FXML
  private JFXTextField expenseNameTextBox;
  @FXML
  private JFXTextField expenseAmountTextBox;
  @FXML
  private JFXButton addExpenseButton;
  @FXML
  private JFXButton deleteExpenseButton;
  @FXML
  private JFXButton saveButton;
  @FXML
  private JFXTextField capitalTextBox;
  @FXML
  private JFXTextField salesTextBox;
  @FXML
  private JFXTextField purchasesTextBox;
  @FXML
  private JFXTextField expensesTextBox;
  @FXML
  private JFXTextField cashOnHandTextBox;
  private ExpenseHandler expenseHandler;
  private SalesMan salesMan;
  private JunkCollector junkCollector;
  private CustomPropertyHandler customPropertyHandler;
  private static final String EXPENSES_KEY = "expenses";
  private static final String SALES_KEY = "sales";
  private static final String PURCHASES_KEY = "purchases";
  private CustomProperty capitalProperty;

  public CashSummaryPresenter() {
    expenseHandler = new ExpenseHandler();
    salesMan = new SalesMan();
    junkCollector = new JunkCollector();
    customPropertyHandler = new CustomPropertyHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<ExpenseFX, String> expense = new TableColumn<>("Expense");
    expense.setCellValueFactory(e -> e.getValue().getName());

    TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
    amount.setCellValueFactory(e -> e.getValue().getAmount());


    expensesTable.getColumns().addAll(expense,
                                      amount);

    TableColumn<SalesFX, String> span = new TableColumn<>("Span");
    span.setCellValueFactory(e -> e.getValue().getSpan());

    TableColumn<SalesFX, String> sales = new TableColumn<>("Sales");
    sales.setCellValueFactory(e -> e.getValue().getSales());


    salesTable.getColumns().addAll(span,
                                   sales);

    setSalesTable();
    setCapitalTextbox();
    setExpensesTable();
    UITools.numberOnlyTextField(capitalTextBox);
    UITools.numberOnlyTextField(expenseAmountTextBox);
    UITools.numberOnlyTextField(salesTextBox);
    UITools.numberOnlyTextField(purchasesTextBox);
    UITools.numberOnlyTextField(expensesTextBox);
  }

  private void setCapitalTextbox() {
    TaskTool<CustomProperty> tool = new TaskTool<>();
    Task<CustomProperty> task = tool.createTask(() -> customPropertyHandler.getProperty(CustomPropertyKeys.CAPITAL.name()));
    task.setOnSucceeded(e -> {
      capitalProperty = task.getValue();
      if (capitalProperty.getProperty() != null) {
        String capitalValue = task.getValue().getValue();
        capitalTextBox.setText(capitalValue);
      }
    });
    tool.execute(task);
  }

  private void setSalesTable() {
    TaskTool<List<SalesEntity>> tool = new TaskTool<>();
    Task<List<SalesEntity>> task = tool.createTask(() -> salesMan.getSales());
    task.setOnSucceeded(e -> {
      Stream<SalesEntity> salesEntityStream = task.getValue()
          .stream();
      List<SalesFX> sales = salesEntityStream
          .map(SalesFX::new)
          .collect(Collectors.toList());
      salesTable.setItems(FXCollections.observableList(sales));
    });
    tool.execute(task);
  }

  private void setAmounts() {
    TaskTool<Map<String, Double>> tool = new TaskTool<>();
    Task<Map<String, Double>> task = tool.createTask(() -> {
      Map<String, Double> values = new HashMap<>();

      values.put(EXPENSES_KEY, getTotalExpenses());
      values.put(SALES_KEY, getTotalSales());
      values.put(PURCHASES_KEY, getTotalPurchases());
      return values;
    });
    task.setOnSucceeded(e -> {
      Map<String, Double> map = task.getValue();
      expensesTextBox.setText("" + map.get(EXPENSES_KEY));
      salesTextBox.setText("" + map.get(SALES_KEY));
      purchasesTextBox.setText("" + map.get(PURCHASES_KEY));
      saveChanges();
    });
    tool.execute(task);
  }

  @FXML
  public void saveChanges() {
    if (capitalTextBox.getText().length() > 0) {
      loadingLabel.setVisible(true);
      Double capital = Double.valueOf(capitalTextBox.getText());
      Double sales = Double.valueOf(salesTextBox.getText());
      Double purchases = Double.valueOf(purchasesTextBox.getText());
      double totalExpenses = Double.valueOf(expensesTextBox.getText());
      double totalCash = (capital + sales) - (purchases + totalExpenses);
      cashOnHandTextBox.setText("" + totalCash);
      TaskTool<Object> tool = new TaskTool<>();
      Task<Object> task = tool.createTask(() -> {
        CustomProperty customProperty = capitalProperty;
        if (customProperty.getProperty() == null) {
          customProperty = new CustomProperty();
          customProperty.setProperty(CustomPropertyKeys.CAPITAL.name());
        }
        customProperty.setValue("" + capital);
        customPropertyHandler.sendProperty(customProperty);
        loadingLabel.setVisible(false);
        return null;
      });
      tool.execute(task);
    }
  }

  private void setExpensesTable() {
    loadingLabel.setVisible(true);
    TaskTool<List<Expense>> tool = new TaskTool<>();
    Task<List<Expense>> task = tool.createTask(this::getExpenses);
    task.setOnSucceeded(e -> {
      Stream<Expense> expenseStream = task.getValue()
          .stream();
      List<ExpenseFX> expenses = expenseStream
          .map(ExpenseFX::new)
          .collect(Collectors.toList());
      expensesTable.setItems(FXCollections.observableList(expenses));
      loadingLabel.setVisible(false);
      setAmounts();
    });
    tool.execute(task);
  }

  @FXML
  public void changeDate() {
  }

  @FXML
  public void addExpense() {
    String name = expenseNameTextBox.getText();
    String amount = expenseAmountTextBox.getText();
    TaskTool<Object> tool = new TaskTool<>();
    Task<Object> task = tool.createTask(() -> {
      expenseHandler.sendExpense(new Expense(name, amount));
      setExpensesTable();
      return null;
    });
    tool.execute(task);
  }

  @FXML
  public void deleteExpense() {
    ExpenseFX selectedItem = expensesTable.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      Expense expense = new Expense(selectedItem);
      TaskTool<Object> tool = new TaskTool<>();
      Task<Object> task = tool.createTask(() -> {
        expenseHandler.deleteExpense(expense);
        setExpensesTable();
        return null;
      });
      tool.execute(task);
    }
  }

  private double getTotalExpenses() {
    return getExpenses()
        .stream()
        .mapToDouble(value -> Double.valueOf(value.getAmount()))
        .sum();
  }

  private List<Expense> getExpenses() {
    return expenseHandler.getExpenses();
  }

  private double getTotalSales() {
    return salesMan.getSales()
        .stream()
        .mapToDouble(value -> Double.valueOf(value.getSales()))
        .sum();
  }

  private double getTotalPurchases() {
    return junkCollector.getJunk()
        .stream()
        .mapToDouble(value -> Double.valueOf(value.getPrice()) * Double.valueOf(value.getWeight()))
        .sum();
  }
}
