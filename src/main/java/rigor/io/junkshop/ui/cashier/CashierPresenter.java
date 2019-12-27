package rigor.io.junkshop.ui.cashier;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import rigor.io.junkshop.models.materials.Material;
import rigor.io.junkshop.models.materials.MaterialsProvider;
import rigor.io.junkshop.models.purchase.Purchase;
import rigor.io.junkshop.models.purchase.PurchaseHandler;
import rigor.io.junkshop.models.purchase.PurchaseItem;
import rigor.io.junkshop.models.purchase.PurchaseItemFX;
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

public class CashierPresenter implements Initializable {

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
  private Label jobStatus;
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
    UITools.numberOnlyTextField(weightText);
    UITools.numberOnlyTextField(priceText);
    date.setText(LocalDate.now().toString());
    totalTextbox.setText("₱ 0.0");
  }


  @FXML
  public void addItem() {
    System.out.println(materialBox.getValue());
    System.out.println(priceText.getText());
    System.out.println(weightText.getText());
    if (weightText.getText() == null || priceText.getText() == null || weightText.getText().length() < 1 || priceText.getText().length() < 1) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Enter weight/price");
      alert.setHeaderText("Weight/Price was not entered");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if (Double.valueOf(weightText.getText()) > Double.valueOf(quantityLabel.getText())) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Not enough material");
      alert.setHeaderText("You are trying to buy " + weightText.getText() + " kg. There is only " + quantityLabel.getText() + " kg left.");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if (materialBox.getValue() == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No material selected");
      alert.setHeaderText("Please select a material");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
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
    double total = purchaseItemList.stream()
        .mapToDouble(purchase -> Double.valueOf(purchase.getPrice().get()))
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
      List<PurchaseItem> purchaseItems = purchaseTable.getItems()
          .stream()
          .map(PurchaseItem::new)
          .collect(Collectors.toList());
      double totalPrice = purchaseItems.stream()
          .mapToDouble(item -> Double.parseDouble(item.getPrice()))
          .sum();

//    print(purchaseTable); TODO print function when purchasing

      Purchase purchase = Purchase.builder()
          .purchaseItems(purchaseItems)
          .date(LocalDate.now().toString())
          .totalPrice("" + totalPrice)
          .build();
      purchaseHandler.sendPurchase(purchase);
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
        addButton.setDisable(material.getWeight() == null);
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

  private void print(Node node) {
    // Define the Job Status Message
    jobStatus.textProperty().unbind();
    jobStatus.setText("Creating a printer job...");

    // Create a printer job for the default printer
    PrinterJob job = PrinterJob.createPrinterJob();

    if (job != null) {
      // Show the printer job status
      jobStatus.textProperty().bind(job.jobStatusProperty().asString());

      // Print the node
      PageLayout pageLayout = Printer.getDefaultPrinter().createPageLayout(Paper.A6, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
      boolean printed = job.printPage(pageLayout, node);

      if (printed) {
        // End the printer job
        job.endJob();
      } else {
        // Write Error Message
        jobStatus.textProperty().unbind();
        jobStatus.setText("Printing failed.");
      }
    } else {
      // Write Error Message
      jobStatus.setText("Could not create a printer job.");
    }
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
