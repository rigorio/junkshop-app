package rigor.io.junkshop.ui.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import rigor.io.junkshop.models.client.Client;
import rigor.io.junkshop.models.client.ClientHandler;
import rigor.io.junkshop.models.client.ClientResponse;
import rigor.io.junkshop.models.junk.Junk;
import rigor.io.junkshop.models.junk.JunkFX;
import rigor.io.junkshop.models.junk.PurchaseHandler;
import rigor.io.junkshop.models.junk.PurchaseSummaryFX;
import rigor.io.junkshop.models.junk.junklist.JunkList;
import rigor.io.junkshop.models.sale.Sale;
import rigor.io.junkshop.models.sale.SaleFX;
import rigor.io.junkshop.models.sale.SaleHandler;
import rigor.io.junkshop.models.sales.SalesEntity;
import rigor.io.junkshop.models.sales.SalesFX;
import rigor.io.junkshop.utils.TaskTool;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ClientPresenter implements Initializable {
  public TableView dataTable;
  public Hyperlink addClientLink;
  public JFXComboBox<Client> clientBox;
  public JFXButton addCashButton;
  public JFXButton payCashButton;
  public JFXTextField nameText;
  public JFXTextField contactText;
  public JFXTextField addressText;
  public Label cashAdvanceLabel;
  public Label purchasesLabel;
  public Label salesLabel;
  public JFXTextField purchasesText;
  public JFXTextField salesText;
  public JFXTextField cashAdvanceAmount;
  public JFXComboBox<String> tableSelector;
  public JFXComboBox<String> spanSelector;
  public JFXButton saveChangesButton;
  public JFXButton deleteButton;

  private static final String SELECT_SALES = "Sales";
  private static final String SELECT_PURCHASES = "Purchases";
  private static final String DAILY = "Daily";
  private static final String MONTHLY = "Monthly";
  private static final String TODAY = "Today";
  private static final String NEW_NAME = "name";
  private static final String NEW_CONTACT = "contact";
  private static final String NEW_ADDRESS = "address";

  private ClientHandler clientHandler;
  private PurchaseHandler purchaseHandler;
  private SaleHandler saleHandler;
  private List<Sale> salesCache;
  private List<JunkList> purchasesCache;

  public ClientPresenter() {
    clientHandler = new ClientHandler();
    purchaseHandler = new PurchaseHandler();
    saleHandler = new SaleHandler();
    salesCache = new ArrayList<>();
    purchasesCache = new ArrayList<>();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TaskTool<List<Client>> tool = new TaskTool<>();
    Task<List<Client>> task = tool.createTask(() -> clientHandler.getClients());
    task.setOnSucceeded(e -> {
      List<Client> clients = task.getValue();
      boolean isEmpty = clients.isEmpty();
      hideFields(isEmpty);
      if (!isEmpty) {
        fillOptions();
        clientBox.setItems(FXCollections.observableList(clients));
        clientBox.getSelectionModel().selectFirst();
//        Optional<Client> any = clients.stream().findAny();
//        if (any.isPresent()) {
//          Client firstClient = any.get();
//          setClientDetails(firstClient);
//        }
        customClientCellFactory();
      }
    });
    tool.execute(task);
  }

  public void addClient() {


    Dialog<Map<String, String>> form = new Dialog<>();
    form.setTitle("New client");
    form.setHeaderText("Fill out client details");
    ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
    form.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField name = new TextField();
    name.setPromptText("Client name");

    TextField contact = new TextField();
    contact.setPromptText("Contact");

    TextField address = new TextField();
    address.setPromptText("Address");

    grid.add(new Label("Client name"), 0, 0);
    grid.add(name, 1, 0);
    grid.add(new Label("Contact"), 0, 1);
    grid.add(contact, 1, 1);
    grid.add(new Label("Address"), 0, 2);
    grid.add(address, 1, 2);

    Node createButton = form.getDialogPane().lookupButton(createButtonType);
    createButton.setDisable(true);

    name.textProperty().addListener((observable, oldValue, newValue) -> createButton.setDisable(newValue.trim().isEmpty()));

    form.getDialogPane().setContent(grid);

    Platform.runLater(name::requestFocus);

    form.setResultConverter(param -> {
      if (param == createButtonType) {
        Map<String, String> map = new HashMap<>();
        map.put(NEW_NAME, name.getText());
        map.put(NEW_CONTACT, contact.getText());
        map.put(NEW_ADDRESS, address.getText());
        return map;
      }
      return null;
    });

    Optional<Map<String, String>> result = form.showAndWait();
    if (result.isPresent()) {
      Map<String, String> formData = result.get();
      Client client = Client.builder()
          .name(formData.get(NEW_NAME))
          .contact(formData.get(NEW_CONTACT))
          .address(formData.get(NEW_ADDRESS))
          .build();
      TaskTool<Client> tool = new TaskTool<>();
      Task<Client> task = tool.createTask(() -> clientHandler.addClient(client));
      task.setOnSucceeded(e -> {
        List<Client> clients = clientHandler.getClients();
        boolean isEmpty = clients.isEmpty();
        hideFields(isEmpty);
        if (!isEmpty) {
          clientBox.setItems(FXCollections.observableList(clients));
          clientBox.getSelectionModel().selectFirst();
        }
      });
      tool.execute(task);
    }

  }

  public void selectClient() {
    Client selectedClient = clientBox.getValue();
    TaskTool<ClientResponse> tool = new TaskTool<>();
    Task<ClientResponse> task = tool.createTask(() -> clientHandler.getClientData(selectedClient.getId()));
    task.setOnSucceeded(e -> {
      ClientResponse response = task.getValue();
      Client client = response.getClient();
      salesCache = response.getSales();
      purchasesCache = response.getPurchases();
      setClientDetails(client);
      double totalSales = salesCache.stream()
          .mapToDouble(sale -> Double.valueOf(sale.getTotalPrice()))
          .sum();
      double totalPurchases = purchasesCache.stream()
          .mapToDouble(purchase -> Double.valueOf(purchase.getTotalPrice()))
          .sum();
      salesText.setText("" + totalSales);
      purchasesText.setText("" + totalPurchases);
    });
    tool.execute(task);

  }

  public void addCash() {
    TextInputDialog dialog = new TextInputDialog("0.0");
    dialog.setTitle("Add cash advancement");
    dialog.setHeaderText(null);
    dialog.setGraphic(null);
    dialog.setContentText("Enter amount");
    Optional<String> result = dialog.showAndWait();
    result.ifPresent(amount -> {
      Client value = clientBox.getValue();
      TaskTool<Client> tool = new TaskTool<>();
      Task<Client> task = tool.createTask(() -> clientHandler.askCash(value.getId(), amount));
      task.setOnSucceeded(e -> {
        String cashAdvance = task.getValue().getCashAdvance();
        cashAdvanceAmount.setText(cashAdvance);
      });
      tool.execute(task);
    });
  }

  public void payCash() {
    TextInputDialog dialog = new TextInputDialog("0.0");
    dialog.setTitle("Pay cash advancement");
    dialog.setHeaderText(null);
    dialog.setGraphic(null);
    dialog.setContentText("Enter amount");
    Optional<String> result = dialog.showAndWait();
    System.out.println(result.isPresent());
    System.out.println(result);
    result.ifPresent(amount -> {
      Client value = clientBox.getValue();
      TaskTool<Client> tool = new TaskTool<>();
      Task<Client> task = tool.createTask(() -> clientHandler.payCash(value.getId(), amount));
      task.setOnSucceeded(e -> {
        String cashAdvance = task.getValue().getCashAdvance();
        cashAdvanceAmount.setText(cashAdvance);
      });
      tool.execute(task);
    });
  }

  public void changeTable() {
    dataTable.setItems(null);
    dataTable.getColumns().clear();
    String table = tableSelector.getValue();
    String span = spanSelector.getValue();
    switch (table) {
      case SELECT_PURCHASES:
        if (span.equals(MONTHLY)) {
          TableColumn<PurchaseSummaryFX, String> s = new TableColumn<>("Span");
          s.setCellValueFactory(e -> e.getValue().getSpan());

          TableColumn<PurchaseSummaryFX, String> t = new TableColumn<>("Total");
          t.setCellValueFactory(e -> e.getValue().getAmount());


          dataTable.getColumns().addAll(s,
                                        t);

          List<Junk> junks = purchasesCache.stream()
              .map(JunkList::getPurchaseItems)
              .flatMap(List::stream)
              .collect(Collectors.toList());

          dataTable.setItems(FXCollections.observableList(purchaseHandler.getMonthlies(junks)));
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

          List<JunkFX> junks = purchasesCache.stream()
              .map(JunkList::getPurchaseItems)
              .flatMap(List::stream)
              .map(JunkFX::new)
              .collect(Collectors.toList());
          dataTable.setItems(FXCollections.observableList(junks));
        }
        break;
      case SELECT_SALES:

        if (span.equals(MONTHLY)) {
          TableColumn<SalesFX, String> s = new TableColumn<>("Span");
          s.setCellValueFactory(e -> e.getValue().getSpan());

          TableColumn<SalesFX, String> sales = new TableColumn<>("Sales");
          sales.setCellValueFactory(e -> e.getValue().getSales());


          dataTable.getColumns().addAll(s,
                                        sales);
          List<SalesEntity> saleSummaries = new ArrayList<>();
          for (Sale purchase : salesCache) {
            LocalDate date = LocalDate.parse(purchase.getDate());
            String time = date.getMonth() + " " + date.getYear();
            Optional<SalesEntity> any = saleSummaries.stream()
                .filter(sale -> sale.getSpan().equals(time))
                .findAny();
            SalesEntity sale = new SalesEntity();
            if (any.isPresent()) {
              sale = any.get();
              Double totalSale = Double.valueOf(sale.getSales());
              sale.setSales("" + (totalSale + Double.valueOf(purchase.getTotalPrice())));
            } else {
              sale.setSpan(time);
              sale.setSales(purchase.getTotalPrice());
              saleSummaries.add(sale);
            }
          }
          dataTable.setItems(FXCollections.observableList(saleSummaries));
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
          dataTable.setItems(FXCollections.observableList(salesCache.stream().map(SaleFX::new).collect(Collectors.toList())));
        }
        break;
    }
  }

  public void changeSpan() {
    changeTable();
  }

  public void saveChanges() {
    Client selectedClient = clientBox.getValue();
    String newName = nameText.getText();
    String newContact = contactText.getText();
    String newAddress = addressText.getText();
    if (selectedClient.getName().equals(newName)
        || selectedClient.getContact().equals(newContact)
        || selectedClient.getAddress().equals(newAddress)) {
      selectedClient.setName(newName);
      selectedClient.setContact(newContact);
      selectedClient.setAddress(newAddress);
      clientHandler.addClient(selectedClient);
    }
  }

  public void deleteClient() {
    Client client = clientBox.getValue();
    clientHandler.deleteClient(client);
  }

  private void hideFields(boolean hontou) {
    hontou = !hontou;
    addCashButton.setVisible(hontou);
    payCashButton.setVisible(hontou);
    nameText.setVisible(hontou);
    contactText.setVisible(hontou);
    addressText.setVisible(hontou);
    cashAdvanceLabel.setVisible(hontou);
    purchasesLabel.setVisible(hontou);
    salesLabel.setVisible(hontou);
    purchasesText.setVisible(hontou);
    salesText.setVisible(hontou);
    cashAdvanceAmount.setVisible(hontou);
    tableSelector.setVisible(hontou);
    spanSelector.setVisible(hontou);
    saveChangesButton.setVisible(hontou);
    deleteButton.setVisible(hontou);
  }

  private void fillOptions() {
    List<String> tableOptions = new ArrayList<>();
    tableOptions.add(SELECT_PURCHASES);
    tableOptions.add(SELECT_SALES);
    tableSelector.setItems(FXCollections.observableList(tableOptions));
    List<String> spanOptions = new ArrayList<>();
    spanOptions.add(DAILY);
    spanOptions.add(MONTHLY);
    spanSelector.setItems(FXCollections.observableList(spanOptions));
  }

  private void customClientCellFactory() {
    clientBox.setCellFactory(new Callback<ListView<Client>, ListCell<Client>>() {
      @Override
      public ListCell<Client> call(ListView<Client> param) {
        return new ListCell<Client>() {
          @Override
          protected void updateItem(Client c, boolean bln) {
            super.updateItem(c, bln);
            setText(c != null ? c.getName() : null);
          }
        };
      }
    });
  }

  private void setClientDetails(Client firstClient) {
    nameText.setText(firstClient.getName());
    contactText.setText(firstClient.getContact());
    addressText.setText(firstClient.getAddress());
    cashAdvanceAmount.setText(firstClient.getCashAdvance());
  }
}
