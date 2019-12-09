package rigor.io.junkshop;

import javafx.application.Application;
import javafx.stage.Stage;
import rigor.io.junkshop.inventory.InventoryView;
import rigor.io.junkshop.utils.GuiManager;

public class JunkshopApp extends Application {

  private static final GuiManager MANAGER = GuiManager.getInstance();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
//    GownRepository gownRepository = new GownListRepository();
//    gownRepository.setList(new GownCsvRepository().getGowns());
    stage.setResizable(true);
//    stage.getIcons().add(ngew Image("/img/.png"));
    MANAGER.setPrimaryStage(stage);
    MANAGER.changeView(new InventoryView());
//    MANAGER.changeView(new LoginView());
  }

  @Override
  public void stop() {
  }


}
