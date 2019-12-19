package rigor.io.junkshop.ui.cashier;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.purchase.Purchase;
import rigor.io.junkshop.models.purchase.PurchaseHandler;
import rigor.io.junkshop.models.purchase.PurchaseItem;
import rigor.io.junkshop.models.purchase.PurchaseItemFX;
import rigor.io.junkshop.ui.purchaseHistory.PurchaseHistoryView;
import rigor.io.junkshop.utils.GuiManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CashierPresenter implements Initializable {
  @FXML
  private JFXButton purchaseButton;
  @FXML
  private TableView<PurchaseItemFX> purchaseTable;
  @FXML
  private JFXComboBox<String> typeBox;
  @FXML
  private JFXComboBox<String> materialBox;
  @FXML
  private JFXTextField priceText;
  @FXML
  private JFXTextField weightText;
  @FXML
  private JFXButton deleteButton;

  private PurchaseHandler purchaseHandler;

  private List<PurchaseItemFX> purchaseItemList = new ArrayList<>();

  public CashierPresenter() {
    purchaseHandler = new PurchaseHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<PurchaseItemFX, String> material = new TableColumn<>("Material");
    material.setCellValueFactory(e -> e.getValue().getMaterial());

    TableColumn<PurchaseItemFX, String> type = new TableColumn<>("Type");
    type.setCellValueFactory(e -> e.getValue().getType());

    TableColumn<PurchaseItemFX, String> price = new TableColumn<>("Price");
    price.setCellValueFactory(e -> e.getValue().getPrice());

    TableColumn<PurchaseItemFX, String> weight = new TableColumn<>("Weight");
    weight.setCellValueFactory(e -> e.getValue().getWeight());

    purchaseTable.getColumns().addAll(type,
                                      material,
                                      weight,
                                      price);
    purchaseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    materialBox.setDisable(true);
    fillTypeBox();
  }

  @FXML
  public void selectType() {
    String type = typeBox.getValue();
    List<String> materials = new ArrayList<>();
    switch (type) {
      case "Wood":
        materials = getWood();
        break;
      case "Metal":
        materials = getMetal();
        break;
      case "Plastic":
        materials = getPlastic();
        break;
    }
    materialBox.setDisable(false);
    materialBox.setItems(FXCollections.observableList(materials));
  }

  @FXML
  public void addItem() {
    String type = typeBox.getValue();
    String material = materialBox.getValue();
    String weight = weightText.getText();
    String price = priceText.getText();
    PurchaseItem item = PurchaseItem.builder()
        .type(type)
        .material(material)
        .weight(weight)
        .price(price)
        .build();
    purchaseItemList.add(new PurchaseItemFX(item));
    purchaseTable.setItems(FXCollections.observableList(purchaseItemList));
  }

  @FXML
  public void deleteItem() {
    purchaseTable.getItems()
        .removeAll(purchaseTable.getSelectionModel().getSelectedItems());
  }

  @FXML
  public void purchaseItems() {
    List<PurchaseItem> purchaseItems = purchaseTable.getItems()
        .stream()
        .map(PurchaseItem::new)
        .collect(Collectors.toList());
    double totalPrice = purchaseItems.stream()
        .mapToDouble(item -> Double.parseDouble(item.getPrice()))
        .sum();

    Purchase purchase = Purchase.builder()
        .purchaseItems(purchaseItems)
        .date(LocalDate.now().toString())
        .totalPrice("" + totalPrice)
        .build();
    purchaseHandler.sendPurchase(purchase);
  }

  @FXML
  public void selectMaterial() {

  }

  @FXML
  public void viewPurchaseHistory() {
    GuiManager.getInstance().displayView(new PurchaseHistoryView());
  }

  private void fillTypeBox() {
    List<String> types = new ArrayList<>(
        Arrays.asList("Wood", "Metal", "Plastic"));
    typeBox.setItems(FXCollections.observableList(types));
  }

  private List<String> getWood() {
    return new ArrayList<>(
        Arrays.asList("Wood A", "Wood B", "Wood C"));
  }

  private List<String> getMetal() {
    return new ArrayList<>(
        Arrays.asList("Metal A", "Metal B", "Metal C"));
  }

  private List<String> getPlastic() {
    return new ArrayList<>(
        Arrays.asList("Plastic A", "Plastic B", "Plastic C"));
  }
}
