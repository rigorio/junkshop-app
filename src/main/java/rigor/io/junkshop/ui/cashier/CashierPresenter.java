package rigor.io.junkshop.ui.cashier;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class CashierPresenter implements Initializable {
  @FXML
  private TableView junkTable;
  @FXML
  private JFXComboBox typeBox;
  @FXML
  private JFXComboBox materialBox;
  @FXML
  private JFXTextField priceText;
  @FXML
  private JFXTextField weightText;
  @FXML
  public JFXButton deleteButton;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  @FXML
  public void selectMaterial() {

  }

  @FXML
  public void addItem() {

  }

  @FXML
  public void viewPurchaseHistory() {

  }
}
