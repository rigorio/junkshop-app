package rigor.io.junkshop.dashboard;

import com.jfoenix.controls.*;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;
import rigor.io.junkshop.api.gown.GownHandler;
import rigor.io.junkshop.api.gown.GownService;
import rigor.io.junkshop.api.gown.GownStatus;
import rigor.io.junkshop.api.gown.StatusOptions;
import rigor.io.junkshop.api.gown.entities.Gown;
import rigor.io.junkshop.api.gown.entities.GownFxAdapter;
import rigor.io.junkshop.api.gown.repository.GownCsvRepository;
import rigor.io.junkshop.edit.EditPresenter;
import rigor.io.junkshop.edit.EditView;
import rigor.io.junkshop.utils.GuiManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DashboardPresenter implements Initializable {
  @FXML
  private JFXComboBox<String> customSelect;
  @FXML
  private JFXDatePicker datePicker;
  @FXML
  private JFXTextArea addressText;
  @FXML
  private JFXTextField orNumber;
  @FXML
  private JFXTextField balanceText;
  @FXML
  private JFXTextField depositText;
  @FXML
  private JFXDatePicker dateReturnedText;
  @FXML
  private JFXDatePicker pickupDateText;
  @FXML
  private JFXButton updateButton;
  @FXML
  private JFXTextField clientText;
  @FXML
  private JFXTextField contactText;
  @FXML
  private JFXDatePicker dateRentedText;
  @FXML
  private JFXDatePicker dueDateText;
  @FXML
  private JFXComboBox<String> statusSearchText;
  @FXML
  private JFXTextField filterText;
  @FXML
  private TableView<GownFxAdapter> gownsTable;
  @FXML
  private JFXComboBox statusBox;
  @FXML
  private JFXTextField priceText;
  @FXML
  private JFXTextArea descText;
  @FXML
  private JFXTextField nameText;

  private GownService gownService;
  private static final String ALL = "All";
  private static final String LESS_THAN = "Less than or equals";
  private static final String MORE_THAN = "More than or equals";

  public DashboardPresenter() {
    gownService = new GownHandler(new GownCsvRepository());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
    priceText.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change1 ->
        pattern.matcher(change1.getControlNewText())
            .matches()
            ? change1
            : null));

    ObservableList es = FXCollections.observableArrayList(Arrays.stream(GownStatus.values()).map(GownStatus::getStatus).collect(Collectors.toList()));
    statusSearchText.setItems(FXCollections.observableArrayList(Arrays.stream(GownStatus.values()).map(GownStatus::getStatus).collect(Collectors.toList())));
    statusSearchText.getItems().add("All");
    statusSearchText.getItems().add("Not Returned");
    statusSearchText.setValue("Not Returned");
    statusBox.setItems(FXCollections.observableArrayList(Arrays.stream(GownStatus.values()).map(GownStatus::getStatus).collect(Collectors.toList())));
    customSelect.setItems(FXCollections.observableArrayList(Arrays.stream(StatusOptions.values()).map(StatusOptions::getStatus).collect(Collectors.toList())));

    TableColumn<GownFxAdapter, String> name = new TableColumn<>("Name");
    name.setCellValueFactory(param -> param.getValue().getName());

    TableColumn<GownFxAdapter, String> description = new TableColumn<>("Description");
    description.setCellValueFactory(param -> param.getValue().getDescription());

    TableColumn<GownFxAdapter, String> price = new TableColumn<>("Price");
    price.setCellValueFactory(param -> new SimpleStringProperty("" + param.getValue().getPrice().get()));

    TableColumn<GownFxAdapter, String> status = new TableColumn<>("Status");
    status.setCellValueFactory(param -> param.getValue().getStatus());
//
//    TableColumn<GownFxAdapter, String> dateRented = new TableColumn<>("Date Rented");
//    dateRented.setCellValueFactory(param -> param.getValue().getDateRented());

    TableColumn<GownFxAdapter, String> dueDate = new TableColumn<>("Due Date");
    dueDate.setCellValueFactory(param -> param.getValue().getDueDate());

    TableColumn<GownFxAdapter, String> dateReturned = new TableColumn<>("DateReturned");
    dateReturned.setCellValueFactory(param -> param.getValue().getDateReturned());

    TableColumn<GownFxAdapter, String> contact = new TableColumn<>("Contact");
    contact.setCellValueFactory(param -> param.getValue().getContact());

    TableColumn<GownFxAdapter, String> orNumber = new TableColumn<>("OR #");
    orNumber.setCellValueFactory(param -> param.getValue().getOrNumber());

    TableColumn<GownFxAdapter, String> address = new TableColumn<>("Address");
    address.setCellValueFactory(param -> param.getValue().getAddress());

    TableColumn<GownFxAdapter, String> deposit = new TableColumn<>("Deposit");
    deposit.setCellValueFactory(param -> param.getValue().getPartialPayment());

    TableColumn<GownFxAdapter, String> balance = new TableColumn<>("Balance");
    balance.setCellValueFactory(param -> param.getValue().getBalance());

    TableColumn<GownFxAdapter, String> pickupDate = new TableColumn<>("Pickup date");
    pickupDate.setCellValueFactory(param -> param.getValue().getPickupDate());


    gownsTable.getColumns().addAll(
        orNumber,
        name,
        description,
        price,
//        dateRented,
        pickupDate,
        dueDate,
        dateReturned,
        deposit,
        balance,
        address,
        contact,
        status
                                  );

    gownsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    gownsTable.setOnMouseClicked(tableSelectionEvent());
    gownsTable.setRowFactory(deselectCells());

    refreshItems(getFXGowns());
    filter();

  }

  @FXML
  public void refresh() {
    statusSearchText.setValue(null);
    statusSearchText.setPromptText("Search by status");
    filterText.setText(null);
    customSelect.setValue(null);
    customSelect.setPromptText("Custom select");
    datePicker.getEditor().clear();
    datePicker.setValue(null);
    refreshItems(getFXGowns());
  }

  @FXML
  public void add() {
    String text = updateButton.getText();
    Gown gown = Gown.builder()
        .orNumber(orNumber.getText())
        .name(nameText.getText())
        .address(addressText.getText())
        .description(descText.getText())
        .price(!priceText.getText().equals("") ? Double.valueOf(priceText.getText()) : 0.0)
        .dueDate(dueDateText.getValue() != null ? dueDateText.getValue().toString() : "")
//        .dateRented(dateRentedText.getValue() != null ? dateRentedText.getValue().toString() : "")
        .status(statusBox.getValue().toString())
        .dateReturned(dateReturnedText.getValue() != null ? dateReturnedText.getValue().toString() : "")
        .pickupDate(pickupDateText.getValue() != null ? pickupDateText.getValue().toString() : "")
        .partialPayment(depositText.getText())
        .balance(balanceText.getText())
        .contact(contactText.getText())
        .build();
    clearDetails();
    if (text.toLowerCase().contains("edit")) {
      GownFxAdapter selectedItem = gownsTable.getSelectionModel().getSelectedItem();
      gown.setId(selectedItem.getId().get());
      gownService.edit(gown);
      refresh();
      return;
    }
    boolean success = gownService.add(gown);
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
    stage.setAlwaysOnTop(true);
    alert.setTitle(success ? "Success" : "Failed");
    alert.setHeaderText(success ? "Gown added" : "Error encountered. Gown was not added, try again.");
    alert.showAndWait();
    refresh();
  }

  @FXML
  public void filter() {
    String text = filterText.getText() != null ? filterText.getText().toLowerCase() : "";
    String statusText = statusSearchText.getValue();
//    if (text.length() < 1) {
//      refreshItems(getFXGowns());
//      return;
//    }

    FilteredList<GownFxAdapter> gownList = getFXGowns()
        .filtered(gown -> {
          StringProperty name = gown.getName();
          StringProperty description = gown.getDescription();
          StringProperty orNumber = gown.getOrNumber();
          boolean searchFilter = true;
          if (text.length() > 1) {
            searchFilter = (name != null && name.get().toLowerCase().contains(text))
                || (description != null && description.get().toLowerCase().contains(text))
                || (orNumber != null && orNumber.get().toLowerCase().contains(text));
          }

          String custom = customSelect.getValue();
          LocalDate customDate = datePicker.getValue();
          boolean customFilter = true;
          if (custom != null && customDate != null) {
            if (custom.equals(StatusOptions.DUE_ON.getStatus())) {
              StringProperty dueDate = gown.getDueDate();
              if (dueDate != null) {
                LocalDate actualDueDate = LocalDate.parse(dueDate.get());
                customFilter = actualDueDate.isEqual(customDate);
              }
            } else if (custom.equals(StatusOptions.PICKUP.getStatus())) {
              StringProperty pickup = gown.getPickupDate();
              if (pickup != null) {
                LocalDate pickupDate = LocalDate.parse(pickup.get());
                customFilter = pickupDate.isEqual(customDate);
              }
            }
          }

          StringProperty status = gown.getStatus();
          boolean statusFilter = true;
          System.out.println(statusText);
          System.out.println(status);
          if (statusText != null) {
            statusFilter = statusText.equalsIgnoreCase("all")
                ? true
                : statusText.equalsIgnoreCase("not returned")
                ? status != null && !status.get().equalsIgnoreCase(GownStatus.RETURNED.getStatus())
                : status != null && status.get().equalsIgnoreCase(statusText)
                ? true
                : false;
          }

          LocalDate value = datePicker.getValue();

          return searchFilter && statusFilter && customFilter;
        });
    refreshItems(gownList);

  }

  @FXML
  public void edit() {
    EditPresenter.selectedGown = gownsTable.getSelectionModel().getSelectedItem();
    GuiManager.getInstance().displayView(new EditView());
  }

  @FXML
  public void delete() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
    stage.setAlwaysOnTop(true);
    alert.setTitle("Confirm deletion");
    alert.setHeaderText("Delete item?");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      ObservableList<GownFxAdapter> selectedItems = gownsTable.getSelectionModel().getSelectedItems();
      for (GownFxAdapter gownAdapter : selectedItems) {
        LongProperty id = gownAdapter.getId();
        if (id != null) {
          gownService.delete(id.get());
        }
      }
      refresh();
    }
  }

  private void refreshItems(ObservableList<GownFxAdapter> gowns) {
    gownsTable.setItems(gowns);
  }

  @NotNull
  private ObservableList<GownFxAdapter> getFXGowns() {
    return FXCollections.observableArrayList(allGowns().stream()
                                                 .map(GownFxAdapter::new)
                                                 .collect(Collectors.toList()));
  }

  private List<Gown> allGowns() {
    return gownService.all();
  }

  private void fillDetails(GownFxAdapter gown) {
    orNumber.setText(gown.getOrNumber() != null ? gown.getOrNumber().get() : "");
    nameText.setText(gown.getName().get());
    if (gown.getContact() != null)
      contactText.setText(gown.getContact().get());
    if (gown.getDescription() != null)
      descText.setText(gown.getDescription().get());
    priceText.setText("" + gown.getPrice().get());
    statusBox.setValue(gown.getStatus().get());
//    StringProperty dr = gown.getDateRented();
//    if (dr != null)
//      dateRentedText.setValue(LocalDate.parse(dr.get()));
    StringProperty pd = gown.getPickupDate();
    if (pd != null)
      pickupDateText.setValue(LocalDate.parse(pd.get()));
    StringProperty dd = gown.getDueDate();
    if (dd != null)
      dueDateText.setValue(LocalDate.parse(dd.get()));
    StringProperty dep = gown.getPartialPayment();
    if (dep != null)
      depositText.setText(dep.get());
    StringProperty balance = gown.getBalance();
    if (balance != null)
      balanceText.setText(balance.get());
    StringProperty dr = gown.getDateReturned();
    if (dr != null)
      dateReturnedText.setValue(LocalDate.parse(dr.get()));
    StringProperty address = gown.getAddress();

//    if (gown.getDateReturned() != null)
//      clientText.setText(gown.getDateReturned().get());

    updateButton.setText("Edit Item");
  }

  private void clearDetails() {
    orNumber.clear();
    nameText.clear();
    contactText.clear();
    descText.clear();
    addressText.clear();
    priceText.clear();
    statusBox.getSelectionModel().clearSelection();
    statusBox.setPromptText("Select status");
//    dateRentedText.getEditor().clear();
    pickupDateText.getEditor().clear();
    pickupDateText.setValue(null);
    dueDateText.getEditor().clear();
    dueDateText.setValue(null);
//    dateRentedText.setValue(null);
    dateReturnedText.getEditor().clear();
    dateReturnedText.setValue(null);
    depositText.clear();
    balanceText.clear();
//    clientText.clear();
    updateButton.setText("Add Item");
  }

  @NotNull
  private Callback<TableView<GownFxAdapter>, TableRow<GownFxAdapter>> deselectCells() {
    return c -> {
      final TableRow<GownFxAdapter> row = new TableRow<>();
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        final int index = row.getIndex();
        if (index >= 0 && index < gownsTable.getItems().size() && gownsTable.getSelectionModel().isSelected(index)) {
          gownsTable.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    };
  }

  @NotNull
  private EventHandler<MouseEvent> tableSelectionEvent() {
    return e -> {
      ObservableList<GownFxAdapter> selectedItems = gownsTable.getSelectionModel().getSelectedItems();
      if (selectedItems.size() == 1) {
        GownFxAdapter gown = selectedItems.get(0);
        clearDetails();
        fillDetails(gown);
      } else if (selectedItems.size() > 1) {
        clearDetails();
      } else {
        clearDetails();
      }
    };
  }

  public void statusCheckingService() {

  }
}
