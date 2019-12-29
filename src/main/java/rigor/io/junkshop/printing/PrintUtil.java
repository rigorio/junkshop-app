package rigor.io.junkshop.printing;

import javafx.collections.ObservableSet;
import javafx.print.Printer;

public class PrintingService {
  public Printer defaultPrinter() {
    return Printer.getDefaultPrinter();
  }

  public ObservableSet<Printer> allPrinters() {
    return Printer.getAllPrinters();
  }



}
