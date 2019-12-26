package rigor.io.junkshop.utils;

import com.jfoenix.controls.JFXTextField;

public class UITools {
  public static void numberOnlyTextField(JFXTextField priceText) {
    priceText.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*(\\.\\d*)?")) {
        priceText.setText(oldValue);
      }
    });
  }
}
