package rigor.io.junkshop.ui.cashier;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.materials.Material;
import rigor.io.junkshop.models.materials.MaterialsProvider;
import rigor.io.junkshop.models.purchase.Purchase;
import rigor.io.junkshop.models.purchase.PurchaseHandler;
import rigor.io.junkshop.models.purchase.PurchaseItem;
import rigor.io.junkshop.models.purchase.PurchaseItemFX;
import rigor.io.junkshop.ui.purchaseHistory.PurchaseHistoryView;
import rigor.io.junkshop.utils.GuiManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CashierPresenter implements Initializable {
  @FXML
  private Label quantityLabel;
  @FXML
  private JFXButton purchaseButton;
  @FXML
  private TableView<PurchaseItemFX> purchaseTable;
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
  private MaterialsProvider materialsProvider;

  public CashierPresenter() {
    purchaseHandler = new PurchaseHandler();
    materialsProvider = new MaterialsProvider();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<PurchaseItemFX, String> material = new TableColumn<>("Material");
    material.setCellValueFactory(e -> e.getValue().getMaterial());

    TableColumn<PurchaseItemFX, String> price = new TableColumn<>("Price");
    price.setCellValueFactory(e -> e.getValue().getPrice());

    TableColumn<PurchaseItemFX, String> weight = new TableColumn<>("Weight");
    weight.setCellValueFactory(e -> e.getValue().getWeight());

    purchaseTable.getColumns().addAll(material,
                                      weight,
                                      price);
    purchaseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    fillMaterialBox();
  }


  @FXML
  public void addItem() {
    String material = materialBox.getValue();
    String weight = weightText.getText();
    String price = priceText.getText();
    PurchaseItem item = PurchaseItem.builder()
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
    selectMaterial();
  }

  @FXML
  public void selectMaterial() {
    String materialName = materialBox.getValue();
    Optional<Material> any = materialsProvider.getMaterials().stream()
        .filter(material -> material.getMaterial().equalsIgnoreCase(materialName))
        .findAny();
    if (any.isPresent()) {
      Material material = any.get();
      priceText.setText(material.getStandardPrice());
      quantityLabel.setText(material.getWeight());
    }
  }

  @FXML
  public void viewPurchaseHistory() {
    GuiManager.getInstance().displayView(new PurchaseHistoryView());
  }

  private void fillMaterialBox() {
    Task<List<Material>> getMaterials = new Task<List<Material>>() {
      @Override
      protected List<Material> call() {
        return materialsProvider.getMaterials();
      }
    };
    getMaterials.setOnSucceeded(e -> materialBox.setItems(FXCollections.observableList(getMaterials.getValue().stream().map(Material::getMaterial).collect(Collectors.toList()))));
    Executor exec = Executors.newCachedThreadPool(runnable -> {
      Thread t = new Thread(runnable);
      t.setDaemon(true);
      return t;
    });
    exec.execute(getMaterials);
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
