package rigor.io.junkshop.ui.inventory;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.junk.Junk;
import rigor.io.junkshop.models.junk.JunkCollector;
import rigor.io.junkshop.models.junk.JunkFX;
import rigor.io.junkshop.models.materials.Material;
import rigor.io.junkshop.models.materials.MaterialsProvider;
import rigor.io.junkshop.ui.junkSummary.JunkSummaryView;
import rigor.io.junkshop.utils.GuiManager;
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InventoryPresenter implements Initializable {
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
  private JunkCollector junkCollector;

  public InventoryPresenter() {
    materialsProvider = new MaterialsProvider();
    junkCollector = new JunkCollector();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    fillMaterialBox();
    TableColumn<JunkFX, String> material = new TableColumn<>("Material");
    material.setCellValueFactory(e -> e.getValue().getMaterial());

    TableColumn<JunkFX, String> price = new TableColumn<>("Price");
    price.setCellValueFactory(e -> e.getValue().getPrice());

    TableColumn<JunkFX, String> weight = new TableColumn<>("Weight");
    weight.setCellValueFactory(e -> e.getValue().getWeight());

    junkTable.getColumns().addAll(material,
                                  price,
                                  weight);
    fillTableData();

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
    return junkCollector.getJunk();
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
    });
    tool.execute(task);
  }

  private List<Material> getMaterials() {
    return materialsProvider.getMaterials();
  }

  @FXML
  public void addItem() {
    if (materialBox.getValue() == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("No material selected");
      alert.setHeaderText("Please select a material from the list");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    if (priceText.getText().length() < 1 || weightText.getText().length() < 1 ) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Price/Weight not specified");
      alert.setHeaderText("Please enter price/weight");
      alert.setContentText(null);
      alert.showAndWait();
      return;
    }
    String materialName = materialBox.getValue();
    String price = priceText.getText();
    String weight = weightText.getText();
    Junk junk = new Junk(materialName,
                         price,
                         weight);
    TaskTool<Object> tool = new TaskTool<>();
    Task<Object> task = tool.createTask(() -> {
      junkCollector.sendJunk(junk);
      fillTableData();
      return null;
    });
    tool.execute(task);
  }

  @FXML
  public void selectMaterial() {
    loadingLabel.setVisible(true);
    String materialName = materialBox.getValue();
    Optional<Material> any = getMaterials().stream()
        .filter(material -> material.getMaterial().equalsIgnoreCase(materialName))
        .findAny();
    if (any.isPresent()) {
      Material material = any.get();
      priceText.setText(material.getStandardPrice());
    }
    loadingLabel.setVisible(false);
  }

  @FXML
  public void viewDailySummaries() {
    GuiManager.getInstance().displayView(new JunkSummaryView());
  }
}
