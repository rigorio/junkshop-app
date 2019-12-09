package rigor.io.junkshop.junkSummary;

import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.junk.Junk;
import rigor.io.junkshop.junk.JunkCollector;
import rigor.io.junkshop.junk.JunkFX;
import rigor.io.junkshop.materials.MaterialsProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class JunkSummaryPresenter implements Initializable {

  @FXML
  private JFXDatePicker datePicker;
  @FXML
  private TableView summaryTable;
  private JunkCollector junkCollector;
  private MaterialsProvider materialsProvider;

  public JunkSummaryPresenter() {
    junkCollector = new JunkCollector();
    materialsProvider = new MaterialsProvider();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<JunkFX, String> material = new TableColumn<>("Material");
    material.setCellValueFactory(e -> e.getValue().getMaterial());

    TableColumn<JunkFX, String> price = new TableColumn<>("Total Price");
    price.setCellValueFactory(e -> e.getValue().getPrice());

    TableColumn<JunkFX, String> weight = new TableColumn<>("Total Weight");
    weight.setCellValueFactory(e -> e.getValue().getWeight());

    summaryTable.getColumns().addAll(material,
                                     weight,
                                     price);

    summaryTable.setItems(FXCollections.observableList(summarizeJunk()
                                                           .stream()
                                                           .map(JunkFX::new)
                                                           .collect(Collectors.toList())));
  }

  @FXML
  public void changeDate() {
    String date = datePicker.getValue().toString();

  }

  private List<Junk> summarizeJunk() {
    List<Junk> allJunk = getAllJunk();
    List<Junk> summarizedJunk = new ArrayList<>();
    materialsProvider.getMaterials().forEach(material -> {
      List<Junk> tempJunk = allJunk.stream().filter(e -> e.getMaterial().equalsIgnoreCase(material.getMaterial()))
          .collect(Collectors.toList());
      summarizedJunk.add(new Junk(material.getMaterial(),
                                  "" + tempJunk.stream()
                                      .mapToDouble(j -> Double.parseDouble(j.getPrice())*Double.parseDouble(j.getWeight()))
                                      .sum(),
                                  "" + tempJunk.stream().mapToDouble(j -> Double.parseDouble(j.getWeight()))
                                      .sum()));
    });

    return summarizedJunk;
  }


  private List<Junk> getAllJunk() {
    return junkCollector.getJunk();
  }
}
