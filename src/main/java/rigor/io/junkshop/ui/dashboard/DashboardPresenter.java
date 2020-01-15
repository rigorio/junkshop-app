package rigor.io.junkshop.ui.dashboard;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import rigor.io.junkshop.account.Roles;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.printing.PrintUtil;
import rigor.io.junkshop.ui.admin.AdminView;
import rigor.io.junkshop.ui.cashSummary.CashSummaryView;
import rigor.io.junkshop.ui.client.ClientView;
import rigor.io.junkshop.ui.sales.SalesView;
import rigor.io.junkshop.ui.purchases.PurchasesView;
import rigor.io.junkshop.ui.settings.SettingsView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardPresenter implements Initializable {

  public JFXButton adminButton;
  @FXML
  private AnchorPane dynamicContentPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    updateDynamicPaneContent(new PurchasesView().getView());
    String role = PublicCache.getRole();
    System.out.println(role);
    if (role == null || !role.equals(Roles.ADMIN.name())) {
      adminButton.setVisible(false);
    }
  }

  @FXML
  public void viewInventory() {
    updateDynamicPaneContent(new PurchasesView().getView());
  }

  @FXML
  public void viewCashier() {
    updateDynamicPaneContent(new SalesView().getView());
  }

  @FXML
  public void viewCashSummary() {
    updateDynamicPaneContent(new CashSummaryView().getView());
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
  public void viewSettings() {
    updateDynamicPaneContent(new SettingsView().getView());
  }

  public void viewClients() {
    updateDynamicPaneContent(new ClientView().getView());
  }

  public void viewAdmin() {
    updateDynamicPaneContent(new AdminView().getView());
  }
}
