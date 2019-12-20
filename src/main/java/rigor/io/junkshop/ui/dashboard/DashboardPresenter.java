package rigor.io.junkshop.ui.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import rigor.io.junkshop.ui.cashier.CashierView;
import rigor.io.junkshop.ui.inventory.InventoryView;
import rigor.io.junkshop.ui.salesSummary.SalesSummaryView;
import rigor.io.junkshop.ui.settings.SettingsView;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {

  @FXML
  private AnchorPane dynamicContentPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    updateDynamicPaneContent(new InventoryView().getView());
  }

  @FXML
  public void viewInventory() {
    updateDynamicPaneContent(new InventoryView().getView());
  }

  @FXML
  public void viewCashier() {
    updateDynamicPaneContent(new CashierView().getView());
  }

  @FXML
  public void viewSales() {
    updateDynamicPaneContent(new SalesSummaryView().getView());
  }

  private void updateDynamicPaneContent(Parent child) { // shut up
    AnchorPane.setTopAnchor(child, 0.0);
    AnchorPane.setLeftAnchor(child, 0.0);
    AnchorPane.setBottomAnchor(child, 0.0);
    AnchorPane.setRightAnchor(child, 0.0);

    dynamicContentPane.getChildren().clear();
    dynamicContentPane.getChildren().add(child);
  }

  @FXML
  public void viewSettings(ActionEvent actionEvent) {
    updateDynamicPaneContent(new SettingsView().getView());
  }
}
