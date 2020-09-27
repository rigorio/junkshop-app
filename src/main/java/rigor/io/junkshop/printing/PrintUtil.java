package rigor.io.junkshop.printing;

import javafx.print.*;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

  public void print2(List<String> lines, Stage stage) {
    TextArea textArea = new TextArea();
    lines.forEach(textArea::appendText);
    PrinterJob job = PrinterJob.createPrinterJob();

    JobSettings jobSettings = job.getJobSettings();
    job.getJobSettings().setPrintQuality(PrintQuality.HIGH);
//    jobSettings.setPrintQuality(PrintQuality.HIGH);

    Printer printer = job.getPrinter();
    PageLayout pl = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, .10, .10, .25, .25);

    boolean b = job.showPrintDialog(stage);
    System.out.println(b);
    if (b) {
      boolean printed = job.printPage(pl, textArea);
      System.out.println(printed);
      if (printed)
        job.endJob();
    }
  }

  public void printService() throws IOException, PrintException {
    PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
    System.out.println(printService.getName());
    InputStream is = new ByteArrayInputStream("hello world!\f".getBytes("UTF8"));

    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    pras.add(new Copies(1));

    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
    Doc doc = new SimpleDoc(is, flavor, null);
    DocPrintJob job = printService.createPrintJob();

    PrintJobWatcher pjw = new PrintJobWatcher(job);
    job.print(doc, pras);
    pjw.waitForDone();
    is.close();
  }
  class PrintJobWatcher {
    boolean done = false;

    PrintJobWatcher(DocPrintJob job) {
      job.addPrintJobListener(new PrintJobAdapter() {
        public void printJobCanceled(PrintJobEvent pje) {
          System.out.println("cance");
          allDone();
        }
        public void printJobCompleted(PrintJobEvent pje) {
          System.out.println("comple");
          allDone();
        }
        public void printJobFailed(PrintJobEvent pje) {
          System.out.println("fail");
          allDone();
        }
        public void printJobNoMoreEvents(PrintJobEvent pje) {
          allDone();
        }
        void allDone() {
          synchronized (PrintJobWatcher.this) {
            done = true;
            System.out.println("Printing done ...");
            PrintJobWatcher.this.notify();
          }
        }
      });
    }
    public synchronized void waitForDone() {
      try {
        while (!done) {
          wait();
        }
      } catch (InterruptedException e) {
      }
    }
  }

}
