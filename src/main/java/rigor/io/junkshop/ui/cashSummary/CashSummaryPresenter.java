package rigor.io.junkshop.ui.cashSummary;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.models.cash.Cash;
import rigor.io.junkshop.models.cash.CashFX;
import rigor.io.junkshop.models.cash.CashHandler;
import rigor.io.junkshop.models.customProperties.CustomProperty;
import rigor.io.junkshop.models.customProperties.CustomPropertyHandler;
import rigor.io.junkshop.models.customProperties.CustomPropertyKeys;
import rigor.io.junkshop.models.expense.Expense;
import rigor.io.junkshop.models.expense.ExpenseFX;
import rigor.io.junkshop.models.expense.ExpenseHandler;
import rigor.io.junkshop.models.junk.Junk;
import rigor.io.junkshop.models.junk.JunkFX;
import rigor.io.junkshop.models.junk.PurchaseHandler;
import rigor.io.junkshop.models.junk.PurchaseSummaryFX;
import rigor.io.junkshop.models.sale.Sale;
import rigor.io.junkshop.models.sale.SaleFX;
import rigor.io.junkshop.models.sale.SaleHandler;
import rigor.io.junkshop.models.sales.SalesEntity;
import rigor.io.junkshop.models.sales.SalesFX;
import rigor.io.junkshop.models.sales.SalesMan;
import rigor.io.junkshop.printing.PrintUtil;
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CashSummaryPresenter implements Initializable {

  @FXML
  private Hyperlink printLink;
  @FXML
  private JFXComboBox<String> expenseSpan;
  @FXML
  private JFXComboBox<String> spanSelector;
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
  private PurchaseHandler purchaseHandler;
  private CustomPropertyHandler customPropertyHandler;
  private CustomProperty capitalProperty;
  private CashHandler cashHandler;
  private SaleHandler saleHandler;
  private static final String EXPENSES_KEY = "expenses";
  private static final String SALES_KEY = "sales";
  private static final String PURCHASES_KEY = "purchases";
  private static final String SELECT_SALES = "Sales";
  private static final String SELECT_PURCHASES = "Purchases";
  private static final String SELECT_OVERALL = "Overall";
  private static final String DAILY = "Daily";
  private static final String MONTHLY = "Monthly";
  private static final String TODAY = "Today";

  public CashSummaryPresenter() {
    expenseHandler = new ExpenseHandler();
    salesMan = new SalesMan();
    purchaseHandler = new PurchaseHandler();
    customPropertyHandler = new CustomPropertyHandler();
    cashHandler = new CashHandler();
    saleHandler = new SaleHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    fillDataSelector();
    fillSpanSelector();
    dataSelector.setValue(SELECT_OVERALL);
    spanSelector.setValue(MONTHLY);
    expenseSpan.setValue(TODAY);
    expensesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    initOverallTable();
    setDailies();
    setExpensesTable();
    UITools.numberOnlyTextField(capitalTextBox);
    UITools.numberOnlyTextField(expenseAmountTextBox);
    UITools.numberOnlyTextField(salesTextBox);
    UITools.numberOnlyTextField(purchasesTextBox);
    UITools.numberOnlyTextField(expensesTextBox);
  }

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
  public void addExpense() {
    String name = expenseNameTextBox.getText();
    String amount = expenseAmountTextBox.getText();
    TaskTool<Object> tool = new TaskTool<>();
    Task<Object> task = tool.createTask(() -> {
      loadingLabel.setVisible(true);
      expenseHandler.sendExpense(new Expense(name, noteTextbox.getText(), amount));
      clearData();
      return null;
    });
    task.setOnSucceeded(e -> {
      setExpensesTable();
      System.out.println("arE?");
      saveChanges();
    });
    tool.execute(task);
  }

  @FXML
  public void deleteExpense() {
    List<Expense> selectedItem = expensesTable.getSelectionModel().getSelectedItems()
        .stream()
        .map(Expense::new)
        .collect(Collectors.toList());
    if (selectedItem.size() > 0) {
      TaskTool<Object> tool = new TaskTool<>();
      Task<Object> task = tool.createTask(() -> {
        loadingLabel.setVisible(true);
        expenseHandler.deleteExpense(selectedItem);
        return null;
      });
      task.setOnSucceeded(e -> {
        setExpensesTable();
        saveChanges();
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

  private void initSalesTable() {

    dataTable.setItems(null);
    dataTable.getColumns().clear();
    String s = spanSelector.getValue();
    if (s.equals(MONTHLY)) {
      TableColumn<SalesFX, String> span = new TableColumn<>("Span");
      span.setCellValueFactory(e -> e.getValue().getSpan());

      TableColumn<SalesFX, String> sales = new TableColumn<>("Sales");
      sales.setCellValueFactory(e -> e.getValue().getSales());


      dataTable.getColumns().addAll(span,
                                    sales);
      TaskTool<List<SalesEntity>> tool = new TaskTool<>();
      Task<List<SalesEntity>> task = tool.createTask(() -> salesMan.getSales(PublicCache.getAccountId()));
      task.setOnSucceeded(e -> {
        Stream<SalesEntity> salesEntityStream = task.getValue()
            .stream();
        List<SalesFX> sales1 = salesEntityStream
            .map(SalesFX::new)
            .collect(Collectors.toList());
        dataTable.setItems(FXCollections.observableList(sales1));
      });
      tool.execute(task);
    } else {
      TableColumn<SaleFX, String> receiptNumber = new TableColumn<>("Receipt #");
      receiptNumber.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getReceiptNumber().get()));

      TableColumn<SaleFX, String> price = new TableColumn<>("Total Price");
      price.setCellValueFactory(e -> e.getValue().getTotalPrice());

      TableColumn<SaleFX, String> date = new TableColumn<>("Date");
      date.setCellValueFactory(e -> e.getValue().getDate());

      TableColumn<SaleFX, String> items = new TableColumn<>("No. Items");
      items.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getPurchaseItems().size()));

      dataTable.getColumns().addAll(receiptNumber,
                                    items,
                                    price,
                                    date);
      TaskTool<List<Sale>> tool = new TaskTool<>();
      Task<List<Sale>> task = tool.createTask(() -> saleHandler.getSales(null, PublicCache.getAccountId()));
      task.setOnSucceeded(e -> {
        Stream<Sale> salesEntityStream = task.getValue()
            .stream();
        List<SaleFX> sales1 = salesEntityStream
            .map(SaleFX::new)
            .collect(Collectors.toList());
        sales1 = s.equals(DAILY)
            ? sales1
            : sales1.stream()
            .filter(sa -> sa.getDate().get().equals(LocalDate.now().toString()))
            .collect(Collectors.toList());
        dataTable.setItems(FXCollections.observableList(sales1));
      });
      tool.execute(task);

    }

  }

  private void initPurchasesTable() {
    dataTable.setItems(null);
    dataTable.getColumns().clear();
    String span = spanSelector.getValue();
    if (span.equals(MONTHLY)) {
      TableColumn<PurchaseSummaryFX, String> s = new TableColumn<>("Span");
      s.setCellValueFactory(e -> e.getValue().getSpan());

      TableColumn<PurchaseSummaryFX, String> t = new TableColumn<>("Total");
      t.setCellValueFactory(e -> e.getValue().getAmount());


      dataTable.getColumns().addAll(s,
                                    t);
      TaskTool<List<PurchaseSummaryFX>> tool = new TaskTool<>();
      Task<List<PurchaseSummaryFX>> task = tool.createTask(() -> purchaseHandler.getMonthlyPurchaseSummary(PublicCache.getAccountId()));
      task.setOnSucceeded(e -> dataTable.setItems(FXCollections.observableList(task.getValue())));
      tool.execute(task);
    } else {
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
      Task<List<Junk>> task = tool.createTask(() -> purchaseHandler.getJunk(null, PublicCache.getAccountId()));
      task.setOnSucceeded(e -> {
        Stream<Junk> junkStream = task.getValue()
            .stream();
        List<JunkFX> junkFXList = junkStream
            .map(JunkFX::new)
            .collect(Collectors.toList());
        junkFXList = span.equals(DAILY)
            ? junkFXList
            : junkFXList.stream()
            .filter(j -> j.getDate().get().equals(LocalDate.now().toString()))
            .collect(Collectors.toList());
        dataTable.setItems(FXCollections.observableList(junkFXList));
      });
      tool.execute(task);
    }


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

    TableColumn<CashFX, String> expense = new TableColumn<>("Expenses");
    expense.setCellValueFactory(e -> e.getValue().getExpenses());
    TableColumn<CashFX, String> purchase = new TableColumn<>("Purchases");
    purchase.setCellValueFactory(e -> e.getValue().getPurchases());

    TableColumn<CashFX, String> sales = new TableColumn<>("Sales");
    sales.setCellValueFactory(e -> e.getValue().getSales());


    dataTable.getColumns().addAll(date,
                                  capital,
                                  sales,
                                  purchase,
                                  expense,
                                  cashOnHand);
    TaskTool<List<Cash>> tool = new TaskTool<>();
    String span = spanSelector.getValue();
    Task<List<Cash>> task = tool.createTask(() -> span.equals(DAILY)
        ? cashHandler.getCash(PublicCache.getAccountId())
        : span.equals(TODAY)
        ? cashHandler.getCash(PublicCache.getAccountId()).stream().filter(c -> c.getDate().equals(LocalDate.now().toString())).collect(Collectors.toList())
        : cashHandler.getMonthlyCash(PublicCache.getAccountId()));
    task.setOnSucceeded(e -> {
      Stream<Cash> cashStream = task.getValue().stream();
      List<CashFX> cash = cashStream.map(CashFX::new).collect(Collectors.toList());
      dataTable.setItems(FXCollections.observableList(cash));
    });
    tool.execute(task);

  }

  @FXML
  public void addCapital() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Add capital");
    dialog.setHeaderText(null);
    dialog.setContentText("Amount to add:");
    Optional<String> result = dialog.showAndWait();
    result.ifPresent(capital -> {
      Double initCap = Double.valueOf(capitalTextBox.getText());
      String total = "" + (initCap + Double.valueOf(capital));
      capitalTextBox.setText(total);
      saveChanges();
    });
  }

  @FXML
  public void updateCapital() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Update capital");
    dialog.setHeaderText(null);
    dialog.setGraphic(null);
    PasswordField pwd = new PasswordField();
    HBox content = new HBox();
    content.setAlignment(Pos.CENTER_LEFT);
    content.setSpacing(10);
    content.getChildren().addAll(new Label("Enter password:"), pwd);
    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == ButtonType.OK) {
        return pwd.getText();
      }
      return null;
    });
    Optional<String> result = dialog.showAndWait();
    result.ifPresent(pw -> {
      CustomProperty property = customPropertyHandler.getProperty(CustomPropertyKeys.CAPITAL_PASSWORD.name());
      if (property.getValue().equals(pw)) {
        TextInputDialog updater = new TextInputDialog();
        updater.setTitle("Update capital");
        updater.setHeaderText(null);
        updater.setGraphic(null);
        updater.setContentText("Enter new amount:");
        Optional<String> amount = updater.showAndWait();
        amount.ifPresent(capital -> {
          capitalTextBox.setText(capital);
          saveChanges();
        });
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Not allowed");
        alert.setHeaderText("Wrong password");
        alert.setContentText("Wrong password was given");
        alert.showAndWait();
      }
    });
  }

  @FXML
  public void printExpenses() {
    List<String> lines = new ArrayList<>();
    lines.add("Steelman Junkshop\n");
    lines.add("Expenses\n");
    lines.add("Date: " + LocalDate.now().toString() + "\n");
    expensesTable.getItems().forEach(expense -> {
      lines.add(expense.getDate().get() + "\n");
      lines.add(expense.getName().get() + "\n");
      if (expense.getNote() != null)
        lines.add(expense.getNote().get() + "\n");
      lines.add(UITools.PESO + " " + expense.getAmount().get() + "\n");
      lines.add("-------\n");
    });
    lines.add("Total: " + UITools.PESO + " " + expensesTable.getItems().stream()
        .mapToDouble(ex -> Double.valueOf(ex.getAmount().get()))
        .sum());
    PrintUtil.print(lines);
  }

  @FXML
  public void showExpenses() {
    String value = expenseSpan.getValue();
    deleteExpenseButton.setDisable(value.equals(MONTHLY));
    setExpensesTable();
  }

  private void clearData() {
    expenseNameTextBox.clear();
    expenseAmountTextBox.clear();
    noteTextbox.clear();
  }

  private void fillDataSelector() {
    List<String> options = new ArrayList<>();
    options.add(SELECT_SALES);
    options.add(SELECT_PURCHASES);
    options.add(SELECT_OVERALL);
    dataSelector.setItems(FXCollections.observableList(options));
  }

  private void fillSpanSelector() {
    List<String> options = new ArrayList<>();
    options.add(DAILY);
    options.add(MONTHLY);
    spanSelector.setItems(FXCollections.observableList(options));
    options.add(TODAY);
    expenseSpan.setItems(FXCollections.observableList(options));
  }

  private List<Expense> getExpenses() {
    return expenseHandler.getExpenses();
  }

  private void setExpensesTable() {
    String span = expenseSpan.getValue();
    expensesTable.setItems(null);
    expensesTable.getColumns().clear();
    switch (span) {
      case DAILY: {

        TableColumn<ExpenseFX, String> expense = new TableColumn<>("Expense");
        expense.setCellValueFactory(e -> e.getValue().getName());

        TableColumn<ExpenseFX, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(e -> e.getValue().getDate());

        TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(e -> e.getValue().getAmount());


        expensesTable.getColumns().addAll(date,
                                          expense,
                                          amount);
        break;
      }
      case MONTHLY: {

        TableColumn<ExpenseFX, String> date = new TableColumn<>("Month");
        date.setCellValueFactory(e -> e.getValue().getDate());

        TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(e -> e.getValue().getAmount());


        expensesTable.getColumns().addAll(date,
                                          amount);
        break;
      }
      case TODAY: {
        TableColumn<ExpenseFX, String> expense = new TableColumn<>("Expense");
        expense.setCellValueFactory(e -> e.getValue().getName());

        TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(e -> e.getValue().getAmount());

        expensesTable.getColumns().addAll(expense,
                                          amount);
        break;
      }
    }
    TaskTool<List<Expense>> tool = new TaskTool<>();
    Task<List<Expense>> task = tool.createTask(() -> {
      loadingLabel.setVisible(true);
      return span.equals(DAILY)
          ? getExpenses()
          : span.equals(MONTHLY)
          ? expenseHandler.getMonthlyExpenses()
          : getExpenses().stream()
          .filter(e -> e.getDate().equals(LocalDate.now().toString()))
          .collect(Collectors.toList());
    });
    task.setOnSucceeded(e -> {
      Stream<Expense> expenseStream = task.getValue()
          .stream();
      List<ExpenseFX> expenses = expenseStream
          .map(ExpenseFX::new)
          .collect(Collectors.toList());
      expensesTable.setItems(FXCollections.observableList(expenses));
      setDailies();
      loadingLabel.setVisible(false);
//      setAmounts();
    });
    tool.execute(task);
  }

  private void setDailies() {
    TaskTool<Cash> tool = new TaskTool<>();
    Task<Cash> task = tool.createTask(() -> {
      loadingLabel.setVisible(true);
      return cashHandler.today();
    });
    task.setOnSucceeded(e -> {
      Cash cash = task.getValue();
      capitalTextBox.setText(cash.getCapital());
      salesTextBox.setText(cash.getSales());
      purchasesTextBox.setText(cash.getPurchases());
      expensesTextBox.setText(cash.getExpenses());
      cashOnHandTextBox.setText(cash.getCashOnHand());
      loadingLabel.setVisible(false);
    });
    tool.execute(task);
  }
}
