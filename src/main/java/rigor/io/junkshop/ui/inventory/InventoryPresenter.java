package rigor.io.junkshop.ui.inventory;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.junk.Junk;
import rigor.io.junkshop.models.junk.JunkCollector;
import rigor.io.junkshop.models.junk.JunkFX;
import rigor.io.junkshop.ui.junkSummary.JunkSummaryView;
import rigor.io.junkshop.models.materials.Material;
import rigor.io.junkshop.models.materials.MaterialsProvider;
import rigor.io.junkshop.utils.GuiManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class InventoryPresenter implements Initializable {
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

    priceText.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*(\\.\\d*)?")) {
          priceText.setText(oldValue);
        }
      }
    });
    weightText.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*(\\.\\d*)?")) {
          weightText.setText(oldValue);
        }
      }
    });
  }

  private void fillTableData() {
    junkTable.setItems(FXCollections.observableList(getJunk()
                                                        .stream()
                                                        .filter(j -> j.getDate()!= null && j.getDate().equalsIgnoreCase(LocalDate.now().toString()))
                                                        .map(JunkFX::new)
                                                        .collect(Collectors.toList())));
  }

  private List<Junk> getJunk() {
    return junkCollector.getJunk();
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
    if (priceText.getText().length() < 1 || weightText.getText().length() < 1 || materialBox.getValue() == null) {
      return;
    }
    String materialName = materialBox.getValue();
    String price = priceText.getText();
    String weight = weightText.getText();
    Junk junk = new Junk(materialName,
                         price,
                         weight);
    junkCollector.sendJunk(junk);
    fillTableData();
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
