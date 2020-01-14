package rigor.io.junkshop.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import rigor.io.junkshop.account.AccountCoordinator;
import rigor.io.junkshop.ui.dashboard.DashboardView;
import rigor.io.junkshop.utils.GuiManager;
import rigor.io.junkshop.utils.TaskTool;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginPresenter implements Initializable {
  public JFXTextField usernameText;
  public JFXPasswordField passwordText;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void login() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Logging In...");
    alert.setHeaderText(null);
    alert.setGraphic(null);
    alert.setContentText("Please wait..");
    alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
    AccountCoordinator coordinator = new AccountCoordinator();
    TaskTool<Boolean> tool = new TaskTool<>();
    Task<Boolean> task = tool.createTask(() -> coordinator.login(usernameText.getText(), passwordText.getText()));
    task.setOnSucceeded(e -> {
      alert.close();
      if (task.getValue()) {
        GuiManager.getInstance().changeView(new DashboardView());
      } else {
        Alert armin = new Alert(Alert.AlertType.ERROR);
        armin.setTitle("Failed!");
        armin.setHeaderText("Wrong username/password");
        armin.setContentText(null);
        armin.showAndWait();
      }

    });
    tool.execute(task);
    alert.showAndWait();
  }
}
