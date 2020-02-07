package rigor.io.junkshop.ui.cashSummary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import rigor.io.junkshop.models.junk.PurchaseHandler;
import rigor.io.junkshop.models.junk.PurchaseSummaryFX;
import rigor.io.junkshop.models.junk.junklist.JunkList;
import rigor.io.junkshop.models.junk.junklist.PurchaseFX;
import rigor.io.junkshop.models.sale.Sale;
import rigor.io.junkshop.models.sale.SaleFX;
import rigor.io.junkshop.models.sale.SaleHandler;
import rigor.io.junkshop.models.sales.SalesEntity;
import rigor.io.junkshop.models.sales.SalesFX;
import rigor.io.junkshop.models.sales.SalesMan;
import rigor.io.junkshop.printing.PrintUtil;
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CashSummaryPresenter implements Initializable {

  public JFXDatePicker customDate;
  public JFXButton deleteItemButton;
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
  private static final String CUSTOM = "Custom";

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
    loadingLabel.setVisible(false); // tongkototongogntosdfjk;ahlldjhsad;lfkj the quick brown fox jumps over the lazy fox
    fillDataSelector();
    fillSpanSelector();
    dataSelector.setValue(SELECT_OVERALL);
    spanSelector.setValue(MONTHLY);
    expenseSpan.setValue(TODAY);
    expensesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    initOverallTable();
//    setDailies();
    setExpensesTable();
    String option = dataSelector.getValue();
    String span = spanSelector.getValue();
    CLEARBOIOTBP(option, span);
    UITools.numberOnlyTextField(capitalTextBox);
    UITools.numberOnlyTextField(expenseAmountTextBox);
    UITools.numberOnlyTextField(salesTextBox);
    UITools.numberOnlyTextField(purchasesTextBox);
    UITools.numberOnlyTextField(expensesTextBox);
  }

  @FXML
  public void saveChanges() {
    Alert alert = UITools.quickLoadingAlert();
    String capital = capitalTextBox.getText();
    Cash cash = cashHandler.today(PublicCache.getAccountId());
    cash.setCapital(capital);
    TaskTool<Cash> tool = new TaskTool<>();
    Task<Cash> task = tool.createTask(() -> {
      return cashHandler.sendCash(cash, PublicCache.getAccountId());
//      setAmounts(savedCash);
//      setDailies();
//      return null;
    });
    task.setOnSucceeded(e -> {
      Cash savedCash = task.getValue();
      setAmounts(savedCash);
      alert.close();
    });
    tool.execute(task);
    alert.show();
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
    Alert alert = UITools.quickLoadingAlert();
    String name = expenseNameTextBox.getText();
    String amount = expenseAmountTextBox.getText();
    String span = expenseSpan.getValue();

    TaskTool<Map<String, Object>> tool = new TaskTool<>();
    Task<Map<String, Object>> task = tool.createTask(() -> {
      return expenseHandler.sendExpense(new Expense(name, noteTextbox.getText(), amount));
//      setExpensesTable();
//      return null;
    });
    task.setOnSucceeded(e -> {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = task.getValue();
      try {
        List<Expense> expenseList = mapper.readValue(mapper.writeValueAsString(map.get("expenses")), new TypeReference<List<Expense>>() {});
        Cash cash = mapper.readValue(mapper.writeValueAsString(map.get("cash")), new TypeReference<Cash>() {});
        List<Expense> expenses = span.equals(DAILY)
            ? expenseList
            : span.equals(MONTHLY)
            ? expenseHandler.getMonthlyExpenses(PublicCache.getAccountId())
            : expenseList.stream()
            .filter(ex -> ex.getDate().equals(LocalDate.now().toString()))
            .collect(Collectors.toList());
        expensesTable.setItems(FXCollections.observableList(expenses.stream()
                                                                .map(ExpenseFX::new)
                                                                .collect(Collectors.toList())));
        setAmounts(cash);
        alert.close();

      } catch (IOException ex) {
        ex.printStackTrace();
      }
//      saveChanges();
    });
    tool.execute(task);
    alert.show();
  }

  @FXML
  public void deleteExpense() {
    Alert alert = UITools.quickLoadingAlert();
    String span = expenseSpan.getValue();
    List<Expense> selectedItem = expensesTable.getSelectionModel().getSelectedItems()
        .stream()
        .map(Expense::new)
        .collect(Collectors.toList());
    if (selectedItem.size() > 0) {
      TaskTool<Map<String, Object>> tool = new TaskTool<>();
      Task<Map<String, Object>> task = tool.createTask(() -> {
        return expenseHandler.deleteExpense(selectedItem);
//        return null;
      });
      task.setOnSucceeded(e -> {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = task.getValue();
        try {
          List<Expense> expenseList = mapper.readValue(mapper.writeValueAsString(map.get("expenses")), new TypeReference<List<Expense>>() {});
          Cash cash = mapper.readValue(mapper.writeValueAsString(map.get("cash")), new TypeReference<Cash>() {});
          List<Expense> expenses = span.equals(DAILY)
              ? expenseList
              : span.equals(MONTHLY)
              ? expenseHandler.getMonthlyExpenses(PublicCache.getAccountId())
              : expenseList.stream()
              .filter(ex -> ex.getDate().equals(LocalDate.now().toString()))
              .collect(Collectors.toList());
          expensesTable.setItems(FXCollections.observableList(expenses.stream()
                                                                  .map(ExpenseFX::new)
                                                                  .collect(Collectors.toList())));
          setAmounts(cash);
          alert.close();

        } catch (IOException ex) {
          ex.printStackTrace();
        }
      });
      tool.execute(task);
      alert.show();
    }
  }

  @FXML
  public void changeData() {
    String option = dataSelector.getValue();
    String span = spanSelector.getValue();
    CLEARBOIOTBP(option, span);
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

  private void CLEARBOIOTBP(String option, String span) {
    customDate.setVisible(span.equals(CUSTOM) && !option.equals(SELECT_OVERALL));
    deleteItemButton.setVisible(!span.equals(MONTHLY) && !option.equals(SELECT_OVERALL));
  }

  private void initSalesTable() {
    Alert alert = UITools.quickLoadingAlert();
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
        alert.close();
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
      String deto = customDate.isVisible() && customDate.getValue() != null ? customDate.getValue().toString() : null;
      Task<List<Sale>> task = tool.createTask(() -> saleHandler.getSales(null, PublicCache.getAccountId(), deto));
      task.setOnSucceeded(e -> {
        Stream<Sale> salesEntityStream = task.getValue()
            .stream();
        List<SaleFX> sales1 = salesEntityStream
            .map(SaleFX::new)
            .collect(Collectors.toList());
        sales1 = s.equals(DAILY) || s.equals(CUSTOM)
            ? sales1
            : sales1.stream()
            .filter(sa -> sa.getDate().get().equals(LocalDate.now().toString()))
            .collect(Collectors.toList());
        dataTable.setItems(FXCollections.observableList(sales1));
        alert.close();
      });
      tool.execute(task);

    }
    alert.show();
  }

  private void initPurchasesTable() {
    Alert alert = UITools.quickLoadingAlert();
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
      Task<List<PurchaseSummaryFX>> task = tool.createTask(() -> {
        return purchaseHandler.getMonthlyPurchaseSummary(PublicCache.getAccountId());
      });
      task.setOnSucceeded(e -> {
        dataTable.setItems(FXCollections.observableList(task.getValue()));
        alert.close();
      });
      tool.execute(task);
    } else {
      TableColumn<PurchaseFX, String> receiptNumber = new TableColumn<>("Receipt #");
      receiptNumber.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getReceiptNumber().get()));

      TableColumn<PurchaseFX, String> price = new TableColumn<>("Total Price");
      price.setCellValueFactory(e -> e.getValue().getTotalPrice());

      TableColumn<PurchaseFX, String> date = new TableColumn<>("Date");
      date.setCellValueFactory(e -> e.getValue().getDate());

      TableColumn<PurchaseFX, String> items = new TableColumn<>("No. Items");
      items.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getPurchaseItems().size()));

      dataTable.getColumns().addAll(receiptNumber,
                                    items,
                                    price,
                                    date);
      TaskTool<ObservableList<PurchaseFX>> tool = new TaskTool<>();
      Task<ObservableList<PurchaseFX>> task = tool.createTask(() -> {
//        alert.show();
        String deto = customDate.isVisible() && customDate.getValue() != null ? customDate.getValue().toString() : null;
        System.out.println(deto);
        return purchaseHandler.getAllPurchases(PublicCache.getAccountId(), deto);
      });
      task.setOnSucceeded(e -> {
        List<PurchaseFX> junkStream = new ArrayList<>(task.getValue());
        junkStream = span.equals(DAILY) || span.equals(CUSTOM)
            ? junkStream
            : junkStream.stream()
            .filter(j -> j.getDate().get().equals(LocalDate.now().toString()))
            .collect(Collectors.toList());
        dataTable.setItems(FXCollections.observableList(junkStream));
        alert.close();
      });
      tool.execute(task);
    }

    alert.show();

  }

  private void initOverallTable() {
    Alert alert = UITools.quickLoadingAlert();
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
    Task<List<Cash>> task = tool.createTask(() -> {

      return span.equals(DAILY)
          ? cashHandler.getCash(PublicCache.getAccountId())
          : span.equals(TODAY)
          ? cashHandler.getCash(PublicCache.getAccountId()).stream().filter(c -> c.getDate().equals(LocalDate.now().toString())).collect(Collectors.toList())
          : cashHandler.getMonthlyCash(PublicCache.getAccountId());
    });
    task.setOnSucceeded(e -> {
      Stream<Cash> cashStream = task.getValue().stream();
      List<CashFX> cash = cashStream.map(CashFX::new).collect(Collectors.toList());
      dataTable.setItems(FXCollections.observableList(cash));
      alert.close();
    });
    tool.execute(task);
    alert.show();
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
    new PrintUtil().print(lines);
  }

  public void deleteItem() {
    Alert alert = UITools.quickLoadingAlert();
    String data = dataSelector.getValue();
    switch (data) {
      case SELECT_PURCHASES:
        ObservableList<PurchaseFX> selectedItems = dataTable.getSelectionModel().getSelectedItems();
        TaskTool<Object> tool = new TaskTool<>();
        List<JunkList> junklists = selectedItems.stream()
            .map(JunkList::new)
            .collect(Collectors.toList());
        Task<Object> task = tool.createTask(() -> {
          purchaseHandler.deleteJunk(junklists);
          return null;
        });
        task.setOnSucceeded(e->{
          dataTable.getItems().removeAll(selectedItems);
          alert.close();
        });
        tool.execute(task);
        break;
      case SELECT_SALES:
        ObservableList<SaleFX> selectedSales = dataTable.getSelectionModel().getSelectedItems();
        TaskTool<Object> t = new TaskTool<>();
        List<Sale> sales = selectedSales.stream()
            .map(Sale::new)
            .collect(Collectors.toList());
        Task<Object> ta = t.createTask(() -> {
          saleHandler.deleteSale(sales);
          return null;
        });
        ta.setOnSucceeded(e->{
          dataTable.getItems().removeAll(selectedSales);
          alert.close();
        });
        t.execute(ta);
        break;
      default:
        break;
    }
    alert.show();
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
    options.add(TODAY);
    expenseSpan.setItems(FXCollections.observableList(new ArrayList<>(options)));
    options.add(CUSTOM);
    spanSelector.setItems(FXCollections.observableList(options));
  }

  private List<Expense> getExpenses() {
    return expenseHandler.getExpenses(PublicCache.getAccountId());
  }

  private void setExpensesTable() {
    Alert alert = UITools.quickLoadingAlert();
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
    TaskTool<Map<String, Object>> tool = new TaskTool<>();
    Task<Map<String, Object>> task = tool.createTask(() -> {
      return expenseHandler.getExpensesAndDailies(PublicCache.getAccountId());
    });
    task.setOnSucceeded(e -> {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = task.getValue();
      try {
        List<Expense> expenseList = mapper.readValue(mapper.writeValueAsString(map.get("expenses")), new TypeReference<List<Expense>>() {});
        Cash cash = mapper.readValue(mapper.writeValueAsString(map.get("cash")), new TypeReference<Cash>() {});
        List<Expense> expenses = span.equals(DAILY)
            ? expenseList
            : span.equals(MONTHLY)
            ? expenseHandler.getMonthlyExpenses(PublicCache.getAccountId())
            : expenseList.stream()
            .filter(ex -> ex.getDate().equals(LocalDate.now().toString()))
            .collect(Collectors.toList());
        expensesTable.setItems(FXCollections.observableList(expenses.stream()
                                                                .map(ExpenseFX::new)
                                                                .collect(Collectors.toList())));
        setAmounts(cash);
        alert.close();

      } catch (IOException ex) {
        ex.printStackTrace();
      }
//      setAmounts();
    });
    tool.execute(task);
    alert.show();
  }

  private void setDailies() {
    TaskTool<Cash> tool = new TaskTool<>();
    Task<Cash> task = tool.createTask(() -> {
      return cashHandler.today(PublicCache.getAccountId());
    });
    task.setOnSucceeded(e -> {
      Cash cash = task.getValue();
      setAmounts(cash);
    });
    tool.execute(task);
  }

  private void setAmounts(Cash cash) {
    capitalTextBox.setText(cash.getCapital());
    salesTextBox.setText(cash.getSales());
    purchasesTextBox.setText(cash.getPurchases());
    expensesTextBox.setText(cash.getExpenses());
    cashOnHandTextBox.setText(cash.getCashOnHand());
  }
}
