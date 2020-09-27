package rigor.io.junkshop.ui.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import rigor.io.junkshop.config.Configurations;
import rigor.io.junkshop.models.customProperties.CustomProperty;
import rigor.io.junkshop.models.customProperties.CustomPropertyHandler;
import rigor.io.junkshop.models.customProperties.CustomPropertyKeys;
import rigor.io.junkshop.models.materials.Material;
import rigor.io.junkshop.models.materials.MaterialFX;
import rigor.io.junkshop.models.materials.MaterialsProvider;
import rigor.io.junkshop.utils.TaskTool;
import rigor.io.junkshop.utils.UITools;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SettingsPresenter implements Initializable {
  public JFXTextField contactText;
  public TableView<MaterialFX> materialsTable;
  public JFXTextField materialText;
  public JFXTextField priceText;
  public JFXButton addEditButton;
  public JFXButton deleteButton;
  public JFXTextField nameText;
  @FXML
  private JFXTextField hostTextBox;
  private CustomPropertyHandler propertyHandler;
  private MaterialsProvider materialsProvider;

  public SettingsPresenter() {
    propertyHandler = new CustomPropertyHandler();
    materialsProvider = new MaterialsProvider();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
//    String host = Configurations.getInstance().getHost();
//    hostTextBox.setText(host);
    CustomProperty property = propertyHandler.getProperty(CustomPropertyKeys.RECEIPT_CONTACT.name());
    contactText.setText(property.getValue());
    CustomProperty name = propertyHandler.getProperty(CustomPropertyKeys.JUNKSHOP_NAME.name());
    contactText.setText(name.getValue());
    setMaterialsTable();
//    materialsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    materialsTable.setOnMouseClicked(tableSelectionEvent());
    materialsTable.setRowFactory(deselectCells());
  }

  @FXML
  public void updateHost() {
//    String host = hostTextBox.getText();
//    Configurations.getInstance().setHost(host);
  }

  public void updateContact() {
    String contact = contactText.getText();
    propertyHandler.sendProperty(new CustomProperty(CustomPropertyKeys.RECEIPT_CONTACT.name(), contact));

  }

  public void updateName() {
    String contact = nameText.getText();
    propertyHandler.sendProperty(new CustomProperty(CustomPropertyKeys.JUNKSHOP_NAME.name(), contact));
  }

  public void addMaterial() {
    TaskTool<List<Material>> tool = new TaskTool<>();
    Alert alert = UITools.quickLoadingAlert();
    Task<List<Material>> task = tool.createTask(() -> {
      Material material = new Material();
      if (addEditButton.getText().equalsIgnoreCase("edit"))
        material = new Material(materialsTable.getSelectionModel().getSelectedItem());
      material.setMaterial(materialText.getText());
      material.setStandardPrice(priceText.getText());
      return materialsProvider.addMaterial(material);
    });
    task.setOnSucceeded(e -> {
      alert.close();
      List<Material> value = task.getValue();
      value.sort(Comparator.comparing(Material::getMaterial));
      List<MaterialFX> materials = value
          .stream()
          .map(MaterialFX::new)
          .collect(Collectors.toList());
      materialsTable.setItems(FXCollections.observableList(materials));
    });
    tool.execute(task);
    alert.showAndWait();

  }

  public void deleteMaterial() {
    Material material = new Material(materialsTable.getSelectionModel().getSelectedItem());
    TaskTool<List<Material>> tool = new TaskTool<>();
    Alert alert = UITools.quickLoadingAlert();
    Task<List<Material>> task = tool.createTask(() -> materialsProvider.deleteMaterial(material));
    task.setOnSucceeded(e -> {
      alert.close();
      List<Material> value = task.getValue();
      value.sort(Comparator.comparing(Material::getMaterial));
      List<MaterialFX> materials = value
          .stream()
          .map(MaterialFX::new)
          .collect(Collectors.toList());
      materialsTable.setItems(FXCollections.observableList(materials));
    });
    tool.execute(task);
    alert.showAndWait();
  }

  private void setMaterialsTable() {
    TableColumn<MaterialFX, String> name = new TableColumn<>("material");
    name.setCellValueFactory(item -> item.getValue().getMaterial());

    TableColumn<MaterialFX, String> price = new TableColumn<>("price");
    price.setCellValueFactory(item -> item.getValue().getStandardPrice());

    materialsTable.getColumns().addAll(name, price);

    TaskTool<List<Material>> tool = new TaskTool<>();
    Task<List<Material>> task = tool.createTask(() -> materialsProvider.getMaterials());
    task.setOnSucceeded(e -> {
      List<Material> value = task.getValue();
      value.sort(Comparator.comparing(Material::getMaterial));
      List<MaterialFX> materials = value
          .stream()
          .map(MaterialFX::new)
          .collect(Collectors.toList());
      materialsTable.setItems(FXCollections.observableList(materials));
    });
    tool.execute(task);
  }

  private EventHandler<MouseEvent> tableSelectionEvent() {
    return e -> {
      ObservableList<MaterialFX> selectedItems = materialsTable.getSelectionModel().getSelectedItems();
      if (selectedItems.size() == 1) {
        MaterialFX material = selectedItems.get(0);
        clearDetails();
        fillDetails(material);
      } else if (selectedItems.size() > 1) {
        clearDetails();
      } else {
        clearDetails();
      }
    };
  }

  private void fillDetails(MaterialFX material) {
    StringProperty mn = material.getMaterial();
    if (mn != null)
      materialText.setText(mn.get());
    StringProperty standardPrice = material.getStandardPrice();
    if (standardPrice != null)
      priceText.setText(standardPrice.get());
    addEditButton.setText("Edit");
  }

  private void clearDetails() {
    materialText.clear();
    priceText.clear();
    addEditButton.setText("Add");
  }

  private Callback<TableView<MaterialFX>, TableRow<MaterialFX>> deselectCells() {
    return c -> {
      final TableRow<MaterialFX> row = new TableRow<>();
      row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        final int index = row.getIndex();
        if (index >= 0 && index < materialsTable.getItems().size() && materialsTable.getSelectionModel().isSelected(index)) {
          materialsTable.getSelectionModel().clearSelection();
          event.consume();
        }
      });
      return row;
    };
  }
}
