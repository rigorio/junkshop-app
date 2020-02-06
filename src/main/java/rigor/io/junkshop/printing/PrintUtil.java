package rigor.io.junkshop.printing;

import javafx.print.*;
import javafx.scene.control.TextArea;

import java.util.List;

public class PrintUtil {

  public void print(List<String> lines) {
    TextArea textArea = new TextArea();
    lines.forEach(textArea::appendText);
    PrinterJob job = PrinterJob.createPrinterJob();

    JobSettings jobSettings = job.getJobSettings();
    jobSettings.setPrintQuality(PrintQuality.HIGH);

    Printer printer = job.getPrinter();
    PageLayout pl = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, .10, .10, .25, .25);
    boolean printed = job.printPage(pl, textArea);
    if (printed)
      job.endJob();
  }
}
