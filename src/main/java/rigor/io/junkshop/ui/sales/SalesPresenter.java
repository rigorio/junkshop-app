package rigor.io.junkshop.ui.sales;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import rigor.io.junkshop.models.materials.Material;
import rigor.io.junkshop.models.materials.MaterialsProvider;
import rigor.io.junkshop.models.sale.Sale;
import rigor.io.junkshop.models.sale.SaleHandler;
import rigor.io.junkshop.models.sale.SaleItem;
import rigor.io.junkshop.models.sale.SaleItemFX;
import rigor.io.junkshop.printing.PrintUtil;
import rigor.io.junkshop.ui.purchaseHistory.PurchaseHistoryView;
import rigor.io.junkshop.utils.GuiManager;
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

public class SalesPresenter implements Initializable {

  @FXML
  private JFXTextField otherText;
  @FXML
  private JFXTextArea noteText;
  @FXML
  private JFXButton addButton;
  @FXML
  private BorderPane receiptBox;
  @FXML
  private JFXTextField receiptNumber;
  @FXML
  private Label totalTextbox;
  @FXML
  private Label date;
  @FXML
  private Label loadingLabel;
  @FXML
  private Label quantityLabel;
  @FXML
  private JFXButton purchaseButton;
  @FXML
  private TableView<SaleItemFX> purchaseTable;
  @FXML
  private JFXComboBox<String> materialBox;
  @FXML
  private JFXTextField priceText;
  @FXML
  private JFXTextField weightText;
  @FXML
  private JFXButton deleteButton;

  private SaleHandler saleHandler;

  private List<SaleItemFX> purchaseItemList = new ArrayList<>();
  private MaterialsProvider materialsProvider;

  public SalesPresenter() {
    saleHandler = new SaleHandler();
    materialsProvider = new MaterialsProvider();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<SaleItemFX, String> material = new TableColumn<>("Material");
    material.setCellValueFactory(e -> e.getValue().getMaterial());

    TableColumn<SaleItemFX, String> note = new TableColumn<>("Note");
    note.setCellValueFactory(e -> e.getValue().getNote());

    TableColumn<SaleItemFX, String> price = new TableColumn<>("Price");
    price.setCellValueFactory(e -> e.getValue().getPrice());

    TableColumn<SaleItemFX, String> weight = new TableColumn<>("Weight");
    weight.setCellValueFactory(e -> e.getValue().getWeight());

    TableColumn<SaleItemFX, String> totalPrice = new TableColumn<>("Total");
    totalPrice.setCellValueFactory(e -> e.getValue().getTotalPrice());

    purchaseTable.getColumns().addAll(material,
                                      note,
                                      weight,
                                      price,
                                      totalPrice);
    purchaseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    fillMaterialBox();
    UITools.numberOnlyTextField(weightText);
    UITools.numberOnlyTextField(priceText);
    date.setText(LocalDate.now().toString());
    totalTextbox.setText("₱ 0.0");
  }


  @FXML
  public void addItem() {
    if (weightText.getText() == null || priceText.getText() == null || weightText.getText().length() < 1 || priceText.getText().length() < 1) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Enter weight/price");
      alert.setHeaderText("Weight/Price was not entered");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if ((otherText.getText() == null) && (Double.valueOf(weightText.getText()) > Double.valueOf(quantityLabel.getText()))) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Not enough material");
      alert.setHeaderText("You are trying to buy " + weightText.getText() + " kg. There is only " + quantityLabel.getText() + " kg left.");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if ((otherText.getText() == null) && materialBox.getValue() == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No material selected");
      alert.setHeaderText("Please select a material");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    String material = otherText.getText() != null && otherText.getText().length() > 0
        ? otherText.getText()
        : materialBox.getValue();
    Optional<SaleItemFX> any = purchaseItemList.stream().filter(p -> p.getMaterial().get().equals(material)).findAny();
    double initialWeight = 0.0;
    if (any.isPresent() && Double.valueOf(any.get().getWeight().get()) + Double.valueOf(weightText.getText()) > Double.valueOf(quantityLabel.getText())) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Not enough materials");
      alert.setHeaderText("There are not enough materials");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if (any.isPresent()) {
      SaleItemFX saleItemFX = any.get();
      purchaseTable.getItems().removeAll(saleItemFX);
      initialWeight = Double.parseDouble(saleItemFX.getWeight().get());
    }
    double weight = Double.valueOf(weightText.getText()) + initialWeight;
    String price = priceText.getText();
    SaleItem item = SaleItem.builder()
        .material(material)
        .note(noteText.getText())
        .weight("" + weight)
        .price(price)
        .totalPrice("" + (weight * Double.valueOf(price)))
        .build();
    purchaseItemList.add(new SaleItemFX(item));
    purchaseTable.setItems(FXCollections.observableList(purchaseItemList));
    double total = purchaseItemList.stream()
        .mapToDouble(purchase -> Double.valueOf(purchase.getTotalPrice().get()))
        .sum();
    totalTextbox.setText("₱ " + UITools.roundToTwo(total));
  }

  @FXML
  public void deleteItem() {
    purchaseTable.getItems()
        .removeAll(purchaseTable.getSelectionModel().getSelectedItems());
  }

  @FXML
  public void purchaseItems() {
    if (receiptNumber.getText() == null || receiptNumber.getText().length() < 1 || purchaseTable.getItems().isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No receipt number found");
      alert.setHeaderText("Please enter the receipt number");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    purchaseButton.setText("Purchasing...");
    TaskTool<Object> tool = new TaskTool<>();
    Task<Object> task = tool.createTask(() -> {
      List<SaleItem> saleItems = purchaseTable.getItems()
          .stream()
          .map(SaleItem::new)
          .collect(Collectors.toList());
      double totalPrice = saleItems.stream()
          .mapToDouble(item -> Double.parseDouble(item.getTotalPrice()))
          .sum();

      List<String> lines = new ArrayList<>();
      lines.add("Steelman Junkshop");
      lines.add("Sales");
      lines.add("Receipt #: " + receiptNumber.getText() + "\n");
      lines.add("Date: " + date.getText() + "\n");
      lines.add("Items:" + "\n");


      for (SaleItem purchaseItem : saleItems) {
        lines.add(purchaseItem.getMaterial());
        lines.add(purchaseItem.getWeight() + "kg * ₱" + purchaseItem.getPrice() + " = ₱" + purchaseItem.getTotalPrice());
      }
      lines.add("GRAND TOTAL: ₱ " + totalPrice);
      PrintUtil.print(lines);

      Sale sale = Sale.builder()
          .saleItems(saleItems)
          .date(LocalDate.now().toString())
          .totalPrice("" + totalPrice)
          .build();
      saleHandler.sendPurchase(sale);
      return null;
    });
    task.setOnSucceeded(e -> purchaseButton.setText("Purchase"));
    tool.execute(task);
    selectMaterial();
  }

  @FXML
  public void selectMaterial() {
    loadingLabel.setVisible(true);
    TaskTool<List<Material>> tool = new TaskTool<>();
    Task<List<Material>> task = tool.createTask(this::getMaterials);
    task.setOnSucceeded(e -> {
      String materialName = materialBox.getValue();
      Optional<Material> any = task.getValue().stream()
          .filter(material -> material.getMaterial().equalsIgnoreCase(materialName))
          .findAny();
      if (any.isPresent()) {
        Material material = any.get();
//        addButton.setDisable(material.getWeight() == null);
        priceText.setText(material.getStandardPrice());
        quantityLabel.setText(material.getWeight());
        loadingLabel.setVisible(false);
      } else {
        loadingLabel.setText("Error!");
      }
    });
    tool.execute(task);
  }

  @FXML
  public void viewPurchaseHistory() {
    GuiManager.getInstance().displayView(new PurchaseHistoryView());
  }

  private void fillMaterialBox() {
    TaskTool<List<Material>> tool = new TaskTool<>();
    Task<List<Material>> task = tool.createTask(this::getMaterials);
    task.setOnSucceeded(e -> {
      Stream<Material> materialStream = task.getValue().stream();
      List<String> materials = materialStream
          .map(Material::getMaterial)
          .collect(Collectors.toList());
      materialBox.setItems(FXCollections.observableList(materials));
      loadingLabel.setVisible(false);
    });
    tool.execute(task);
  }

  private List<Material> getMaterials() {
    return materialsProvider.getMaterials();
  }

}
