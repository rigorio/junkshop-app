package rigor.io.junkshop;

import javafx.application.Application;
import javafx.stage.Stage;
import rigor.io.junkshop.printing.PrintUtil;
import rigor.io.junkshop.printing.PrinterService;
import rigor.io.junkshop.ui.dashboard.DashboardView;
import rigor.io.junkshop.ui.login.LoginView;
import rigor.io.junkshop.utils.GuiManager;

import javax.print.PrintException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    MANAGER.changeView(new LoginView());
//    List<String> lines = new ArrayList<>();
//    lines.add("aaaaaaaa aaaaaaaaaaaa");
//    lines.add("bbbbbbbb bbbbbbbbb");
//    lines.add("cccccccc  cccccccc");
//    lines.add("aaaaad sfaaaa");
//    lines.add("aqrewaaaaaa");
//    lines.add("aaaaaa143241323421aaaaa");
//    lines.add("aaaaa 42 312341aaaaaa");
//    lines.add("aaaaa14324312aaaaaa");
//    lines.add("aaaa41324132aaaaaa");
//    lines.add("aaaaaaasdadfsfdsaaaaaa");
//    lines.add("aaaaaaa dadsf fsadfsfadsaaa");
//    lines.add("aaa12341234aaaaaaa");
//    lines.add("1234321431234");
//    lines.add("asdzxcbw qet ");
//
////    new PrintUtil().print(lines);
////    new PrintUtil().print2(lines, stage);
//    PrinterService printerService = new PrinterService();
//
//    System.out.println(printerService.getPrinters());
//    StringBuilder builder =  new StringBuilder();
//    lines.forEach(line-> builder.append(line).append("\n"));
//    //print some stuff. Change the printer name to your thermal printer name.
//    printerService.printString(printerService.getDefaultPrinter(), builder.toString());

    // cut that paper!
//    byte[] cutP = new byte[] { 0x1d, 'V', 1 };
//
//    printerService.printBytes("XP-58 (copy 2)", cutP);
//    MANAGER.changeView(new LoginView());
  }

  @Override
  public void stop() {
  }


}
