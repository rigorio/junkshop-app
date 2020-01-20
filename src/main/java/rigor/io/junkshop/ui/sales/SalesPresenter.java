package rigor.io.junkshop.ui.sales;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.util.Callback;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.models.client.Client;
import rigor.io.junkshop.models.client.ClientHandler;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SalesPresenter implements Initializable {

  public JFXComboBox<Client> clientBox;
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
  private ClientHandler clientHandler;


  public SalesPresenter() {
    saleHandler = new SaleHandler();
    materialsProvider = new MaterialsProvider();
    clientHandler = new ClientHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    customClientCellFactory();

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
    omotteta();
  }

  @FXML
  public void deleteItem() {
    purchaseTable.getItems()
        .removeAll(purchaseTable.getSelectionModel().getSelectedItems());
  }

  @FXML
  public void purchaseItems() {
    if (purchaseTable.getItems().isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No items found");
      alert.setHeaderText("Add items to the table/list");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if (receiptNumber.getText() == null || receiptNumber.getText().length() < 1) {
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
      lines.add("Steelman Junkshop\n");
      lines.add(PublicCache.getContact() + "\n");
      lines.add("Sales\n");
      lines.add("Client: " + clientBox.getValue().getName() + "\n");
      lines.add("Receipt #: " + receiptNumber.getText() + "\n");
      lines.add("Date: " + LocalDate.now().toString() + "\n");
      lines.add("Items:" + "\n");


      for (SaleItem purchaseItem : saleItems) {
        lines.add(purchaseItem.getMaterial() + "\n");
        lines.add(purchaseItem.getNote() + "\n");
        lines.add(purchaseItem.getWeight() + "kg * ₱" + purchaseItem.getPrice() + " = ₱" + purchaseItem.getTotalPrice() + "\n");
      }
      lines.add("GRAND TOTAL: ₱ " + totalPrice);
      Client client = clientBox.getValue();
      Sale sale = Sale.builder()
          .receiptNumber(receiptNumber.getText())
          .saleItems(saleItems)
          .clientId(client.getId())
          .date(LocalDate.now().toString())
          .totalPrice("" + totalPrice)
          .build();
      saleHandler.sendPurchase(sale);
      try {
        PrintUtil.print(lines);
        Thread.sleep(3500L);
        PrintUtil.print(lines);
        Thread.sleep(3500L);
        PrintUtil.print(lines);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      return null;
    });
    task.setOnSucceeded(e -> {
      purchaseButton.setText("Purchase");
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Printing");
      alert.setHeaderText("Sale confirmed! Please wait for receipt");
      alert.setContentText(null);
      alert.showAndWait();
      clearFields();
    });
    tool.execute(task);
    selectMaterial();
  }

  private void clearFields() {
    purchaseTable.setItems(null);
    purchaseTable.getItems().clear();
    receiptNumber.clear();
    clientBox.setValue(null);
    clientBox.setPromptText("Select Client");
    omotteta();
    totalTextbox.setText("₱ 0.0");
//    selectMaterial();
  }

  private void omotteta() {
    materialBox.setValue(null);
    materialBox.setPromptText("Select Material");
    otherText.clear();
    noteText.clear();
    quantityLabel.setText("0.0");
    weightText.clear();
    priceText.clear();
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
    TaskTool<Map<String, Object>> tool = new TaskTool<>();
    Task<Map<String, Object>> task = tool.createTask(this::getItems);
    task.setOnSucceeded(e -> {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = task.getValue();
      try {
        List<Client> clients = mapper.readValue(mapper.writeValueAsString(map.get("clients")), new TypeReference<List<Client>>() {});
        if (clients != null && !clients.isEmpty()) {
          clientBox.setItems(FXCollections.observableList(clients));
          Optional<Client> walk_in = clients.stream()
              .filter(c -> c.getName().equalsIgnoreCase("walk in"))
              .findAny();
          walk_in.ifPresent(client -> clientBox.getSelectionModel().select(client));
        }
        List<String> materials = mapper
            .<List<Material>>readValue(mapper.writeValueAsString(map.get("materials")), new TypeReference<List<Material>>() {})
            .stream()
            .map(Material::getMaterial)
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.toList());
        materialBox.setItems(FXCollections.observableList(materials));
        loadingLabel.setVisible(false);

      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });
    tool.execute(task);
  }

  private List<Material> getMaterials() {
    return materialsProvider.getMaterials();
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

  private Map<String, Object> getItems() {
    return materialsProvider.getItems();
  }
}
