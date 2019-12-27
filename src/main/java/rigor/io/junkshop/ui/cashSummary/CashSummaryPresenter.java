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
import java.util.List;
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

  public CashSummaryPresenter() {
    expenseHandler = new ExpenseHandler();
    salesMan = new SalesMan();
    junkCollector = new JunkCollector();
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
    setExpensesTable();
    UITools.numberOnlyTextField(capitalTextBox);
    UITools.numberOnlyTextField(expenseAmountTextBox);
    UITools.numberOnlyTextField(salesTextBox);
    UITools.numberOnlyTextField(purchasesTextBox);
    UITools.numberOnlyTextField(expensesTextBox);
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

    TaskTool<Double> t1 = new TaskTool<>();
    Task<Double> taskExpense = t1.createTask(this::getTotalExpenses);
    taskExpense.setOnSucceeded(e -> expensesTextBox.setText("" + taskExpense.getValue()));
    t1.execute(taskExpense);

    TaskTool<Double> t2 = new TaskTool<>();
    Task<Double> taskSales = t2.createTask(this::getTotalSales);
    taskSales.setOnSucceeded(e -> salesTextBox.setText("" + taskSales.getValue()));
    t2.execute(taskSales);

    TaskTool<Double> t3 = new TaskTool<>();
    Task<Double> taskPurchases = t3.createTask(this::getTotalPurchases);
    taskPurchases.setOnSucceeded(e -> {
      purchasesTextBox.setText("" + taskPurchases.getValue());
    });
    t3.execute(taskPurchases);

  }

  private void setExpensesTable() {
    loadingLabel.setVisible(true);
    setAmounts();
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

  @FXML
  public void saveChanges() {
    Double capital = Double.valueOf(capitalTextBox.getText());
    Double sales = Double.valueOf(salesTextBox.getText());
    Double purchases = Double.valueOf(purchasesTextBox.getText());
    double totalExpenses = Double.valueOf(expensesTextBox.getText());
    double totalCash = (capital + sales) - (purchases + totalExpenses);
    cashOnHandTextBox.setText("" + totalCash);
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
