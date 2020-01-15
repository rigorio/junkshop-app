package rigor.io.junkshop.ui.purchases;

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
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.models.client.Client;
import rigor.io.junkshop.models.junk.Junk;
import rigor.io.junkshop.models.junk.JunkFX;
import rigor.io.junkshop.models.junk.PurchaseHandler;
import rigor.io.junkshop.models.junk.junklist.JunkList;
import rigor.io.junkshop.models.materials.Material;
import rigor.io.junkshop.models.materials.MaterialsProvider;
import rigor.io.junkshop.printing.PrintUtil;
import rigor.io.junkshop.ui.junkSummary.JunkSummaryView;
import rigor.io.junkshop.utils.GuiManager;
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurchasesPresenter implements Initializable {
  public JFXComboBox<Client> clientBox;
  @FXML
  private Label grandTotal;
  @FXML
  private JFXTextField receiptNumber;
  @FXML
  private JFXTextField otherTextBox;
  @FXML
  private JFXTextArea noteTextBox;
  @FXML
  private JFXButton printButton;
  @FXML
  private Label loadingLabel;
  @FXML
  private JFXComboBox<String> materialBox;
  @FXML
  private JFXTextField priceText;
  @FXML
  private JFXTextField weightText;
  @FXML
  private TableView<JunkFX> junkTable;
  private MaterialsProvider materialsProvider;
  private PurchaseHandler purchaseHandler;
  private List<JunkFX> purchaseItemList = new ArrayList<>();

  public PurchasesPresenter() {
    materialsProvider = new MaterialsProvider();
    purchaseHandler = new PurchaseHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    customClientCellFactory();
    grandTotal.setText("₱ 0.0");
    fillMaterialBox();
    TableColumn<JunkFX, String> material = new TableColumn<>("Material");
    material.setCellValueFactory(e -> e.getValue().getMaterial());

    TableColumn<JunkFX, String> note = new TableColumn<>("Note");
    note.setCellValueFactory(e -> e.getValue().getNote());

    TableColumn<JunkFX, String> price = new TableColumn<>("Price");
    price.setCellValueFactory(e -> e.getValue().getPrice());

    TableColumn<JunkFX, String> weight = new TableColumn<>("Weight");
    weight.setCellValueFactory(e -> e.getValue().getWeight());

    TableColumn<JunkFX, String> totalPrice = new TableColumn<>("Total");
    totalPrice.setCellValueFactory(e -> e.getValue().getTotalPrice());

    junkTable.getColumns().addAll(material,
                                  note,
                                  weight,
                                  price,
                                  totalPrice);
    junkTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//    fillTableData();

    UITools.numberOnlyTextField(priceText);
    UITools.numberOnlyTextField(weightText);
  }


  private void fillTableData() {
    loadingLabel.setVisible(true);
    TaskTool<List<Junk>> tool = new TaskTool<>();
    Task<List<Junk>> task = tool.createTask(this::getJunk);
    task.setOnSucceeded(e -> {
      Stream<Junk> junkStream = task.getValue().stream();
      List<JunkFX> junk = junkStream
          .filter(j -> {
            String today = LocalDate.now().toString();
            return j.getDate() != null && j.getDate().equalsIgnoreCase(today);
          })
          .map(JunkFX::new)
          .collect(Collectors.toList());
      junkTable.setItems(FXCollections.observableList(junk));
      loadingLabel.setVisible(false);
    });
    tool.execute(task);
  }

  private List<Junk> getJunk() {
    return purchaseHandler.getJunk(null, PublicCache.getAccountId());
  }

  private void fillMaterialBox() {
    TaskTool<Map<String, Object>> tool = new TaskTool<>();
    Task<Map<String, Object>> task = tool.createTask(this::getItems);
    task.setOnSucceeded(e -> {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> map = task.getValue();
      try {
        List<Client> clients = mapper.readValue(mapper.writeValueAsString(map.get("clients")), new TypeReference<List<Client>>() {});
        if (clients != null && !clients.isEmpty())
          clientBox.setItems(FXCollections.observableList(clients));
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

  private Map<String, Object> getItems() {
    return materialsProvider.getItems();
  }

  @FXML
  public void addItem() {
    if (materialBox.getValue() == null && (otherTextBox.getText() == null || otherTextBox.getText().length() < 1)) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No material selected");
      alert.setHeaderText("Please select a material from the list");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if (priceText.getText().length() < 1 || weightText.getText().length() < 1) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Price/Weight not specified");
      alert.setHeaderText("Please enter price/weight");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    String materialName = otherTextBox.getText() != null && otherTextBox.getText().length() > 0
        ? otherTextBox.getText()
        : materialBox.getValue();
    String price = priceText.getText();
    String weight = weightText.getText();
    String note = noteTextBox.getText();
    Junk junk = Junk.builder()
        .material(materialName)
        .price(price)
        .weight(weight)
        .note(note)
        .totalPrice("" + (Double.valueOf(price) * Double.valueOf(weight)))
        .build();
    purchaseItemList.add(new JunkFX(junk));
    junkTable.setItems(FXCollections.observableList(purchaseItemList));
    double sum = junkTable.getItems().stream()
        .mapToDouble(j -> Double.valueOf(j.getTotalPrice().get()))
        .sum();
    grandTotal.setText("₱ " + UITools.roundToTwo(sum));
    omotetta();

  }

  @FXML
  public void deleteItem() {
    junkTable.getItems()
        .removeAll(junkTable.getSelectionModel().getSelectedItems());
    double sum = junkTable.getItems().stream()
        .mapToDouble(j -> Double.valueOf(j.getTotalPrice().get()))
        .sum();
    grandTotal.setText("₱ " + UITools.roundToTwo(sum));
  }

  @FXML
  public void selectMaterial() {
    loadingLabel.setVisible(true);
    String materialName = materialBox.getValue();
    TaskTool<Optional<Material>> tool = new TaskTool<>();
    Task<Optional<Material>> task = tool.createTask(() -> getMaterials().stream()
        .filter(material -> material.getMaterial().equalsIgnoreCase(materialName))
        .findAny());
    task.setOnSucceeded(e -> {
      task.getValue().ifPresent(material -> priceText.setText(material.getStandardPrice()));
      loadingLabel.setVisible(false);
    });
    tool.execute(task);
  }

  @FXML
  public void viewDailySummaries() {
    GuiManager.getInstance().displayView(new JunkSummaryView());
  }

  @FXML
  public void printSelectedItems() {
    if (junkTable.getItems().isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No items found");
      alert.setHeaderText("Add items to the table/list");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if (receiptNumber.getText() == null || receiptNumber.getText().length() < 1) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No receipt number");
      alert.setHeaderText("Please enter receipt number");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    Client client = clientBox.getValue();
    List<Junk> junkList = getSelectedJunk();
    JunkList purchase = JunkList.builder()
        .receiptNumber(receiptNumber.getText())
        .date(LocalDate.now().toString())
        .clientId(client.getId())
        .purchaseItems(junkList)
        .totalPrice("" + junkList.stream().mapToDouble(junk -> Double.valueOf(junk.getTotalPrice())).sum())
        .build();
    TaskTool<Object> tool = new TaskTool<>();
    Task<Object> task = tool.createTask(() -> {
      loadingLabel.setVisible(true);
      purchaseHandler.savePurchases(purchase);
      return null;
    });

    List<String> lines = new ArrayList<>();
    lines.add("Steelman Junkshop\n");
    lines.add(PublicCache.getContact() + "\n");
    lines.add("Purchases\n");
    lines.add("Receipt #" + receiptNumber.getText() + "\n");
    lines.add("Date: " + LocalDate.now().toString() + "\n");
    lines.add("Items:" + "\n");
    for (Junk junk : junkList) {
      lines.add(junk.getMaterial() + "\n");
      lines.add(junk.getNote() + "\n");
      lines.add(junk.getWeight() + "kg * ₱" + junk.getPrice() + " = ₱" + junk.getTotalPrice() + "\n");
    }
    double grandTotal = junkList.stream()
        .mapToDouble(junk -> Double.valueOf(junk.getWeight()) * Double.valueOf(junk.getPrice()))
        .sum();
    lines.add("GRAND TOTAL: ₱" + grandTotal);
    task.setOnSucceeded(e -> {
      loadingLabel.setVisible(false);
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Printing");
      alert.setHeaderText("Items purchased successfully! Please wait for receipt");
      alert.setContentText(null);
      alert.showAndWait();
      clearFields();
      try {
        PrintUtil.print(lines);
        Thread.sleep(3500L);
        PrintUtil.print(lines);
        Thread.sleep(3500L);
        PrintUtil.print(lines);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    });

    tool.execute(task);
  }

  private void clearFields() {
    junkTable.setItems(null);
    receiptNumber.clear();
    clientBox.setValue(null);
    clientBox.setPromptText("Select Client");
    omotetta();
    grandTotal.setText("₱ 0.0");
//    selectMaterial();
  }

  private void omotetta() {
    otherTextBox.clear();
    noteTextBox.clear();
    materialBox.setValue(null);
    materialBox.setPromptText("Select Material");
    priceText.clear();
    weightText.clear();
  }

  @NotNull
  private List<Junk> getSelectedJunk() {
    return junkTable.getItems()
        .stream()
        .map(Junk::new)
        .collect(Collectors.toList());
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
}
