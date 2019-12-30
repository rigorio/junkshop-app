package rigor.io.junkshop.ui.sales;

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

  public SalesPresenter() {
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

    TableColumn<PurchaseItemFX, String> totalPrice = new TableColumn<>("Total");
    totalPrice.setCellValueFactory(e -> e.getValue().getTotalPrice());

    purchaseTable.getColumns().addAll(material,
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
    Optional<PurchaseItemFX> any = purchaseItemList.stream().filter(p -> p.getMaterial().get().equals(material)).findAny();
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
      PurchaseItemFX purchaseItemFX = any.get();
      purchaseTable.getItems().removeAll(purchaseItemFX);
      initialWeight = Double.parseDouble(purchaseItemFX.getWeight().get());
    }
    double weight = Double.valueOf(weightText.getText()) + initialWeight;
    String price = priceText.getText();
    PurchaseItem item = PurchaseItem.builder()
        .material(material)
        .weight("" + weight)
        .price(price)
        .totalPrice("" + (weight * Double.valueOf(price)))
        .build();
    purchaseItemList.add(new PurchaseItemFX(item));
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
      List<PurchaseItem> purchaseItems = purchaseTable.getItems()
          .stream()
          .map(PurchaseItem::new)
          .collect(Collectors.toList());
      double totalPrice = purchaseItems.stream()
          .mapToDouble(item -> Double.parseDouble(item.getPrice()))
          .sum();

      List<String> lines = new ArrayList<>();
      lines.add("Steelman Junkshop");
      lines.add("Sales");
      lines.add("Receipt #: " + receiptNumber.getText() + "\n");
      lines.add("Date: " + date.getText() + "\n");
      lines.add("Items:" + "\n");


      for (PurchaseItem purchaseItem : purchaseItems) {
        lines.add(purchaseItem.getMaterial());
        lines.add(purchaseItem.getWeight() + "kg * ₱" + purchaseItem.getPrice() + " = ₱" + purchaseItem.getTotalPrice());
      }
      lines.add("GRAND TOTAL: ₱ " + totalPrice);
      PrintUtil.print(lines);

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
    System.out.println("Creating a printer job...");

    // Create a printer job for the default printer
    PrinterJob job = PrinterJob.createPrinterJob();
    JobSettings jobSettings = job.getJobSettings();
    PageLayout pageLayout = jobSettings.getPageLayout();

    if (job != null) {
      // Show the printer job status

      // Print the node
      pageLayout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
      jobSettings.setPageLayout(pageLayout);
      boolean printed = job.printPage(pageLayout, node);

      if (printed) {
        // End the printer job
        job.endJob();
      } else {
        // Write Error Message
        System.out.println("Printing failed.");
      }
    } else {
      // Write Error Message
      System.out.println("Could not create a printer job.");
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
