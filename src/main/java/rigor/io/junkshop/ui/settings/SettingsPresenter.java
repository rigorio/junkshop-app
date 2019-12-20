package rigor.io.junkshop.ui.settings;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import rigor.io.junkshop.config.Configurations;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsPresenter implements Initializable {
  @FXML
  private JFXTextField hostTextBox;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    String host = Configurations.getInstance().getHost();
    hostTextBox.setText(host);
  }

  @FXML
  public void updateHost() {
    String host = hostTextBox.getText();
    Configurations.getInstance().setHost(host);
  }
}
