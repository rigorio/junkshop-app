package rigor.io.junkshop.ui.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import rigor.io.junkshop.account.Account;
import rigor.io.junkshop.account.AccountCoordinator;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.models.cash.Cash;
import rigor.io.junkshop.models.cash.CashFX;
import rigor.io.junkshop.models.cash.CashHandler;
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
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminPresenter implements Initializable {
  public JFXComboBox<String> dataSelector;
  public JFXComboBox<String> spanSelector;
  public JFXComboBox<Account> accountBox;
  public JFXTextField usernameText;
  public JFXPasswordField newPassText;
  public JFXPasswordField confirmPassText;
  public TableView dataTable;
  public JFXButton saveButton;
  public Label confirmLabel;
  private AccountCoordinator accountCoordinator;

  private SalesMan salesMan;
  private PurchaseHandler purchaseHandler;
  private CashHandler cashHandler;
  private SaleHandler saleHandler;
  private static final String SELECT_SALES = "Sales";
  private static final String SELECT_PURCHASES = "Purchases";
  private static final String SELECT_OVERALL = "Overall";
  private static final String SELECT_EXPENSES = "Expenses";
  private static final String DAILY = "Daily";
  private static final String MONTHLY = "Monthly";
  private static final String TODAY = "Today";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String CONF_PASSWORD = "conf_password";
  private ExpenseHandler expenseHandler;

  public AdminPresenter() {
    accountCoordinator = new AccountCoordinator();
    expenseHandler = new ExpenseHandler();
    salesMan = new SalesMan();
    purchaseHandler = new PurchaseHandler();
    cashHandler = new CashHandler();
    saleHandler = new SaleHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    fillDataSelector();
    fillSpanSelector();
    saveButton.setDisable(true);
    confirmPassText.textProperty().addListener((o, ov, nv) -> {
      if (newPassText.getText().equals(confirmPassText.getText())) {
        confirmPassText.setStyle("-fx-color: black");
        confirmLabel.setStyle("-fx-color: black");
        saveButton.setDisable(false);
      } else {
        confirmPassText.setStyle("-fx-color: black");
        confirmLabel.setStyle("-fx-color: black");
        saveButton.setDisable(true);
      }
    });
    TaskTool<List<Account>> tool = new TaskTool<>();
    Task<List<Account>> task = tool.createTask(() -> accountCoordinator.allAccounts());
    task.setOnSucceeded(e -> {
      setAccountCellFactory();
      dataSelector.setValue(SELECT_OVERALL);
      spanSelector.setValue(MONTHLY);
      accountBox.setItems(FXCollections.observableList(task.getValue()));
      accountBox.getSelectionModel().selectFirst();
      Account account = accountBox.getValue();
      usernameText.setText(account.getUsername());
//      initOverallTable(account.getId());

    });
    tool.execute(task);
  }

  public void newAccount() {
    Alert alert = UITools.quickLoadingAlert();
    Dialog<Map<String, String>> form = new Dialog<>();
    form.setTitle("New client");
    form.setHeaderText("Fill out client details");
    ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
    form.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField username = new TextField();
    username.setPromptText("Username");

    PasswordField password = new PasswordField();
    password.setPromptText("Password");

    PasswordField confPass = new PasswordField();
    confPass.setPromptText("Confirm Password");

    grid.add(new Label("Username"), 0, 0);
    grid.add(username, 1, 0);
    grid.add(new Label("Password"), 0, 1);
    grid.add(password, 1, 1);
    grid.add(new Label("Confirm Password"), 0, 2);
    grid.add(confPass, 1, 2);
    Label matcher = new Label("Password Mismatch");
    matcher.setVisible(false);
    grid.add(matcher, 1, 3);


    Node createButton = form.getDialogPane().lookupButton(createButtonType);
    createButton.setDisable(true);

    username.textProperty().addListener((observable, oldValue, newValue) -> createButton.setDisable(newValue.trim().isEmpty()));
    confPass.textProperty().addListener((o, ov, nv) -> {
      matcher.setVisible(password.getText().equals(confPass.getText()));
      createButton.setDisable(!(password.getText().equals(confPass.getText())) || username.getText().trim().isEmpty());
    });

    form.getDialogPane().setContent(grid);

    Platform.runLater(username::requestFocus);

    form.setResultConverter(param -> {
      if (param == createButtonType) {
        Map<String, String> map = new HashMap<>();
        map.put(USERNAME, username.getText());
        map.put(PASSWORD, password.getText());
        return map;
      }
      return null;
    });

    Optional<Map<String, String>> result = form.showAndWait();
    if (result.isPresent()) {
      Map<String, String> formData = result.get();

      Account acc = new Account();
      acc.setUsername(formData.get(USERNAME));
      String pash = formData.get(PASSWORD);
      acc.setPassword(pash);
      TaskTool<Account> tool = new TaskTool<>();
      Task<Account> task = tool.createTask(() -> {
        alert.showAndWait();
        return accountCoordinator.save(acc);
      });
      task.setOnSucceeded(e -> {
        alert.close();
        accountBox.getItems().add(task.getValue());
        accountBox.setValue(task.getValue());
      });
      tool.execute(task);
    }

  }

  public void changeData() {
    Account account = accountBox.getValue();
    if (account != null) {

      String accountId = account.getId();
      String option = dataSelector.getValue();
      switch (option) {
        case SELECT_PURCHASES:
          initPurchasesTable(accountId);
          break;
        case SELECT_OVERALL:
          initOverallTable(accountId);
          break;
        case SELECT_SALES:
          initSalesTable(accountId);
          break;
        case SELECT_EXPENSES:
          setExpensesTable(accountId);
          break;
      }
    }
  }

  public void selectClient() {
    Account account = accountBox.getValue();
    usernameText.setText(account.getUsername());
    newPassText.clear();
    confirmPassText.clear();
    changeData();
  }

  public void saveAccount() {
    String newPass = newPassText.getText();
    String confPass = confirmPassText.getText();
    String username = usernameText.getText();
    if (checkFields(newPass, confPass, username)) return;
    Account account = accountBox.getValue();
    account.setUsername(username);
    account.setPassword(newPass);
    Alert alert = UITools.quickLoadingAlert();
    TaskTool<Account> tool = new TaskTool<>();
    Task<Account> task = tool.createTask(() -> {
      alert.showAndWait();
      return accountCoordinator.save(account);
    });
    task.setOnSucceeded(e -> {
      alert.close();
    });
    tool.execute(task);

  }

  public void deleteAccount() {
  }

  private void setAccountCellFactory() {
    accountBox.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
      @Override
      public ListCell<Account> call(ListView<Account> param) {
        return new ListCell<Account>() {
          @Override
          protected void updateItem(Account c, boolean bln) {
            super.updateItem(c, bln);
            setText(c != null ? c.getUsername() : null);
          }
        };
      }
    });
  }

  private void fillDataSelector() {
    List<String> options = new ArrayList<>();
    options.add(SELECT_SALES);
    options.add(SELECT_PURCHASES);
    options.add(SELECT_OVERALL);
    options.add(SELECT_EXPENSES);
    dataSelector.setItems(FXCollections.observableList(options));
  }

  private void fillSpanSelector() {
    List<String> options = new ArrayList<>();
    options.add(DAILY);
    options.add(MONTHLY);
    options.add(TODAY);
    spanSelector.setItems(FXCollections.observableList(options));
  }

  private void initSalesTable(String accountId) {

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
      Task<List<SalesEntity>> task = tool.createTask(() -> salesMan.getSales(accountId));
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
      Task<List<Sale>> task = tool.createTask(() -> saleHandler.getSales(null, accountId));
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

  private void initPurchasesTable(String accountId) {
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
      Task<List<PurchaseSummaryFX>> task = tool.createTask(() -> purchaseHandler.getMonthlyPurchaseSummary(accountId));
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
      Task<List<Junk>> task = tool.createTask(() -> purchaseHandler.getJunk(null, accountId));
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

  private void initOverallTable(String accountId) {
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
        ? cashHandler.getCash(accountId)
        : span.equals(TODAY)
        ? cashHandler.getCash(accountId).stream().filter(c -> c.getDate().equals(LocalDate.now().toString())).collect(Collectors.toList())
        : cashHandler.getMonthlyCash(accountId));
    task.setOnSucceeded(e -> {
      Stream<Cash> cashStream = task.getValue().stream();
      List<CashFX> cash = cashStream.map(CashFX::new).collect(Collectors.toList());
      dataTable.setItems(FXCollections.observableList(cash));
    });
    tool.execute(task);

  }

  private void setExpensesTable(String accountId) {
    String span = spanSelector.getValue();
    dataTable.setItems(null);
    dataTable.getColumns().clear();
    switch (span) {
      case DAILY: {

        TableColumn<ExpenseFX, String> expense = new TableColumn<>("Expense");
        expense.setCellValueFactory(e -> e.getValue().getName());

        TableColumn<ExpenseFX, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(e -> e.getValue().getDate());

        TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(e -> e.getValue().getAmount());


        dataTable.getColumns().addAll(date,
                                      expense,
                                      amount);
        break;
      }
      case MONTHLY: {

        TableColumn<ExpenseFX, String> date = new TableColumn<>("Month");
        date.setCellValueFactory(e -> e.getValue().getDate());

        TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(e -> e.getValue().getAmount());


        dataTable.getColumns().addAll(date,
                                      amount);
        break;
      }
      case TODAY: {
        TableColumn<ExpenseFX, String> expense = new TableColumn<>("Expense");
        expense.setCellValueFactory(e -> e.getValue().getName());

        TableColumn<ExpenseFX, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(e -> e.getValue().getAmount());

        dataTable.getColumns().addAll(expense,
                                      amount);
        break;
      }
    }
    TaskTool<List<Expense>> tool = new TaskTool<>();
    Task<List<Expense>> task = tool.createTask(() -> span.equals(DAILY)
        ? getExpenses(accountId)
        : span.equals(MONTHLY)
        ? expenseHandler.getMonthlyExpenses(accountId)
        : getExpenses(accountId).stream()
        .filter(e -> e.getDate().equals(LocalDate.now().toString()))
        .collect(Collectors.toList()));
    task.setOnSucceeded(e -> {
      Stream<Expense> expenseStream = task.getValue()
          .stream();
      List<ExpenseFX> expenses = expenseStream
          .map(ExpenseFX::new)
          .collect(Collectors.toList());
      dataTable.setItems(FXCollections.observableList(expenses));
    });
    tool.execute(task);
  }

  private List<Expense> getExpenses(String accountId) {
    return expenseHandler.getExpenses(accountId);
  }

  private boolean checkFields(String newPass, String confPass, String username) {
    if (newPass == null || confPass == null || username == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Missing info");
      alert.setHeaderText(null);
      alert.setGraphic(null);
      alert.setContentText("Missing info, please fill all info.");
      alert.showAndWait();
      return true;
    }
    if (!newPass.equals(confPass)) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Mismatch");
      alert.setHeaderText(null);
      alert.setGraphic(null);
      alert.setContentText("Passwords didn't match");
      return true;
    }
    return false;
  }


}
