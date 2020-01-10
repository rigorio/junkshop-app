package rigor.io.junkshop.ui.settings;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import rigor.io.junkshop.config.Configurations;
import rigor.io.junkshop.models.customProperties.CustomProperty;
import rigor.io.junkshop.models.customProperties.CustomPropertyHandler;
import rigor.io.junkshop.models.customProperties.CustomPropertyKeys;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsPresenter implements Initializable {
  public JFXTextField contactText;
  @FXML
  private JFXTextField hostTextBox;
  private CustomPropertyHandler propertyHandler;

  public SettingsPresenter() {
    propertyHandler = new CustomPropertyHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    String host = Configurations.getInstance().getHost();
    hostTextBox.setText(host);
    CustomProperty property = propertyHandler.getProperty(CustomPropertyKeys.RECEIPT_CONTACT.name());
    contactText.setText(property.getValue());
  }

  @FXML
  public void updateHost() {
    String host = hostTextBox.getText();
    Configurations.getInstance().setHost(host);
  }

  public void updateContact() {
    String contact = contactText.getText();
    propertyHandler.sendProperty(new CustomProperty(CustomPropertyKeys.RECEIPT_CONTACT.name(), contact));

  }
}
