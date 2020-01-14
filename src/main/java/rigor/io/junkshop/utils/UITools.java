package rigor.io.junkshop.utils;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UITools {
  public static final String PESO = "₱";

  public static void numberOnlyTextField(JFXTextField priceText) {
    priceText.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*(\\.\\d*)?")) {
        priceText.setText(oldValue);
      }
    });
  }

  public static String roundToTwo(Double dd) {
    DecimalFormat format = new DecimalFormat("#.##");
    format.setRoundingMode(RoundingMode.CEILING);
    return format.format(dd);
  }

  public static Alert quickLoadingAlert() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Loading");
    alert.setHeaderText(null);
    alert.setGraphic(null);
    alert.setContentText("Please wait..");
    alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
    return alert;
  }
}
