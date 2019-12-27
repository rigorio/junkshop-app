package rigor.io.junkshop.utils;

import com.jfoenix.controls.JFXTextField;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UITools {
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
}
