package rigor.io.junkshop.ui.cashSummary;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.cash.Cash;
import rigor.io.junkshop.models.cash.CashFX;
import rigor.io.junkshop.models.cash.CashHandler;
import rigor.io.junkshop.models.customProperties.CustomProperty;
import rigor.io.junkshop.models.customProperties.CustomPropertyHandler;
import rigor.io.junkshop.models.expense.Expense;
import rigor.io.junkshop.models.expense.ExpenseFX;
import rigor.io.junkshop.models.expense.ExpenseHandler;
import rigor.io.junkshop.models.junk.Junk;
import rigor.io.junkshop.models.junk.JunkCollector;
import rigor.io.junkshop.models.junk.JunkFX;
import rigor.io.junkshop.models.sales.SalesEntity;
import rigor.io.junkshop.models.sales.SalesFX;
import rigor.io.junkshop.models.sales.SalesMan;
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CashSummaryPresenter implements Initializable {

  @FXML
  private JFXComboBox<String> dataSelector;
  @FXML
  private JFXTextField noteTextbox;
  @FXML
  private TableView dataTable;
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
  private CustomProperty capitalProperty;
  private CashHandler cashHandler;
  private static final String EXPENSES_KEY = "expenses";
  private static final String SALES_KEY = "sales";
  private static final String PURCHASES_KEY = "purchases";
  private static final String SELECT_SALES = "Sales";
  private static final String SELECT_PURCHASES = "Purchases";
  private static final String SELECT_OVERALL = "Overall";

  public CashSummaryPresenter() {
    expenseHandler = new ExpenseHandler();
    salesMan = new SalesMan();
    junkCollector = new JunkCollector();
    customPropertyHandler = new CustomPropertyHandler();
    cashHandler = new CashHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    fillDataSelector();

    TableColumn<ExpenseFX, String> expense = new TableColumn<>("Expense");
    expense.setCellValueFactory(e -> e.getValue().getName());

    TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
    amount.setCellValueFactory(e -> e.getValue().getAmount());


    expensesTable.getColumns().addAll(expense,
                                      amount);

    initOverallTable();
    setDailies();
    setExpensesTable();
    UITools.numberOnlyTextField(capitalTextBox);
    UITools.numberOnlyTextField(expenseAmountTextBox);
    UITools.numberOnlyTextField(salesTextBox);
    UITools.numberOnlyTextField(purchasesTextBox);
    UITools.numberOnlyTextField(expensesTextBox);
  }

  private void initSalesTable() {

    dataTable.setItems(null);
    dataTable.getColumns().clear();
    TableColumn<SalesFX, String> span = new TableColumn<>("Span");
    span.setCellValueFactory(e -> e.getValue().getSpan());

    TableColumn<SalesFX, String> sales = new TableColumn<>("Sales");
    sales.setCellValueFactory(e -> e.getValue().getSales());


    dataTable.getColumns().addAll(span,
                                  sales);
    TaskTool<List<SalesEntity>> tool = new TaskTool<>();
    Task<List<SalesEntity>> task = tool.createTask(() -> salesMan.getSales());
    task.setOnSucceeded(e -> {
      Stream<SalesEntity> salesEntityStream = task.getValue()
          .stream();
      List<SalesFX> sales1 = salesEntityStream
          .map(SalesFX::new)
          .collect(Collectors.toList());
      dataTable.setItems(FXCollections.observableList(sales1));
    });
    tool.execute(task);
  }

  private void initPurchasesTable() {
    dataTable.setItems(null);
    dataTable.getColumns().clear();
    TableColumn<JunkFX, String> totalPrice = new TableColumn<>("Total Price");
    totalPrice.setCellValueFactory(e -> e.getValue().getTotalPrice());

    TableColumn<JunkFX, String> material = new TableColumn<>("Material");
    material.setCellValueFactory(e -> e.getValue().getMaterial());

    TableColumn<JunkFX, String> date = new TableColumn<>("Date");
    date.setCellValueFactory(e -> e.getValue().getDate());


    dataTable.getColumns().addAll(date,
                                  material,
                                  totalPrice);
    TaskTool<List<Junk>> tool = new TaskTool<>();
    Task<List<Junk>> task = tool.createTask(() -> junkCollector.getJunk());
    task.setOnSucceeded(e -> {
      Stream<Junk> junkStream = task.getValue()
          .stream();
      List<JunkFX> junkFXList = junkStream
          .map(JunkFX::new)
          .collect(Collectors.toList());
      dataTable.setItems(FXCollections.observableList(junkFXList));
    });
    tool.execute(task);
  }

  private void initOverallTable() {
    dataTable.setItems(null);
    dataTable.getColumns().clear();
    TableColumn<CashFX, String> date = new TableColumn<>("Date");
    date.setCellValueFactory(e -> e.getValue().getDate());

    TableColumn<CashFX, String> capital = new TableColumn<>("Capital");
    capital.setCellValueFactory(e -> e.getValue().getCapital());
    TableColumn<CashFX, String> cashOnHand = new TableColumn<>("Cash");
    cashOnHand.setCellValueFactory(e -> e.getValue().getCashOnHand());


    dataTable.getColumns().addAll(date,
                                  capital,
                                  cashOnHand);
    TaskTool<List<Cash>> tool = new TaskTool<>();
    Task<List<Cash>> task = tool.createTask(() -> cashHandler.getCash());
    task.setOnSucceeded(e -> {
      Stream<Cash> cashStream = task.getValue().stream();
      List<CashFX> cash = cashStream.map(CashFX::new).collect(Collectors.toList());
      dataTable.setItems(FXCollections.observableList(cash));
    });
    tool.execute(task);

  }

  private void fillDataSelector() {
    List<String> options = new ArrayList<>();
    options.add(SELECT_SALES);
    options.add(SELECT_PURCHASES);
    options.add(SELECT_OVERALL);
    dataSelector.setItems(FXCollections.observableList(options));
  }

/*
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
*/

  private void setDailies() {
    TaskTool<Cash> tool = new TaskTool<>();
    Task<Cash> task = tool.createTask(() -> cashHandler.today());
    task.setOnSucceeded(e -> {
      Cash cash = task.getValue();
      capitalTextBox.setText(cash.getCapital());
      salesTextBox.setText(cash.getSales());
      purchasesTextBox.setText(cash.getPurchases());
      expensesTextBox.setText(cash.getExpenses());
      cashOnHandTextBox.setText(cash.getCashOnHand());
    });
    tool.execute(task);
  }

/*  private void setAmounts() {
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
  }*/

  @FXML
  public void saveChanges() {
    String capital = capitalTextBox.getText();
    Cash cash = cashHandler.today();
    cash.setCapital(capital);
    TaskTool<Cash> tool = new TaskTool<>();
    Task<Cash> task = tool.createTask(() -> {
      cashHandler.sendCash(cash);
      setDailies();
      return null;
    });
    tool.execute(task);
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
//      setAmounts();
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
      expenseHandler.sendExpense(new Expense(name, noteTextbox.getText(), amount));
      setExpensesTable();
      setDailies();
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
        setDailies();
        return null;
      });
      tool.execute(task);
    }
  }

  @FXML
  public void changeData() {
    String option = dataSelector.getValue();
    switch (option) {
      case SELECT_PURCHASES:
        initPurchasesTable();
        break;
      case SELECT_OVERALL:
        initOverallTable();
        break;
      case SELECT_SALES:
        initSalesTable();
        break;
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
