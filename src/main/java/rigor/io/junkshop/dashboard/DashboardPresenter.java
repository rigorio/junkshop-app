package rigor.io.junkshop.dashboard;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.junk.Junk;
import rigor.io.junkshop.junk.JunkCollector;
import rigor.io.junkshop.junk.JunkFX;
import rigor.io.junkshop.junkSummary.JunkSummaryView;
import rigor.io.junkshop.materials.Material;
import rigor.io.junkshop.materials.MaterialsProvider;
import rigor.io.junkshop.utils.GuiManager;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DashboardPresenter implements Initializable {
  @FXML
  private JFXComboBox<String> materialBox;
  @FXML
  private JFXTextField priceText;
  @FXML
  private JFXTextField weightText;
  @FXML
  private TableView junkTable;
  private MaterialsProvider materialsProvider;
  private JunkCollector junkCollector;

  public DashboardPresenter() {
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
    junkTable.setItems(FXCollections.observableList(junkCollector.getJunk()
                                                   .stream()
                                                   .map(JunkFX::new)
                                                   .collect(Collectors.toList())));
  }

  private void fillMaterialBox() {
    Task<List<Material>> getMaterials = new Task<List<Material>>() {
      @Override
      protected List<Material> call() throws Exception {
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

  @FXML
  public void addItem() {
    String materialName = materialBox.getValue();
    String price = priceText.getText();
    String weight = weightText.getText();
    Junk junk = new Junk(materialName,
                         price,
                         weight);
    junkCollector.sendJunk(junk);
    junkTable.setItems(FXCollections.observableList(junkCollector.getJunk()
                                                        .stream()
                                                        .map(JunkFX::new)
                                                        .collect(Collectors.toList())));
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
    }
  }

  @FXML
  public void viewDailySummaries() {
    GuiManager.getInstance().displayView(new JunkSummaryView());
  }
}
