package rigor.io.junkshop.newGown;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import rigor.io.junkshop.api.gown.GownHandler;
import rigor.io.junkshop.api.gown.GownService;
import rigor.io.junkshop.api.gown.GownStatus;
import rigor.io.junkshop.api.gown.entities.Gown;
import rigor.io.junkshop.api.gown.repository.GownCsvRepository;

import java.net.URL;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewGownPresenter implements Initializable {
  @FXML
  private JFXComboBox<String> statusBox;
  @FXML
  private JFXTextField priceText;
  @FXML
  private JFXTextField descText;
  @FXML
  private JFXTextField nameText;

  private GownService gownService;

  public NewGownPresenter() {
    gownService = new GownHandler(new GownCsvRepository());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GownStatus[] values = GownStatus.values();
    List<String> status = new ArrayList<>(Arrays.asList(values))
        .stream()
        .map(GownStatus::getStatus)
        .collect(Collectors.toList());
    statusBox.getItems().addAll(status);
    Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
    TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
        pattern.matcher(change.getControlNewText())
            .matches()
            ? change
            : null);

    priceText.setTextFormatter(formatter);
  }

  @FXML
  public void add() {
    Gown gown = Gown.builder()
        .name(nameText.getText())
        .description(descText.getText())
        .price(!priceText.getText().equals("") ? Double.valueOf(priceText.getText()) : 0.0)
        .dueDate("1")
        .dateRented("f")
        .status(statusBox.getValue())
        .build();
    boolean success = gownService.add(gown);
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
    stage.setAlwaysOnTop(true);
    alert.setTitle(success ? "Success" : "Failed");
    alert.setHeaderText(success ? "Gown added" : "Error encountered. Gown was not added, try again.");
    if (success)
      alert.setContentText("Please refresh the table to see changes");
    Optional<ButtonType> result = alert.showAndWait();
    alert.close();
    if (result.isPresent() && result.get() == ButtonType.OK)
      ((Stage) priceText.getScene().getWindow()).close();
  }

  @FXML
  public void cancel() {
  }
}
