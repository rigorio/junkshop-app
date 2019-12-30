package rigor.io.junkshop.printing;

import javafx.print.PrinterJob;
import javafx.scene.control.TextArea;

import java.util.List;

public class PrintUtil {
  public static void print(List<String> lines) {
    TextArea textArea = new TextArea();
    lines.forEach(textArea::appendText);
    PrinterJob job = PrinterJob.createPrinterJob();
    boolean printed = job.printPage(textArea);
    if (printed)
      job.endJob();
  }

}
