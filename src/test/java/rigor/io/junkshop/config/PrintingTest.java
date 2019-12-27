package rigor.io.junkshop.config;

import javafx.print.Printer;
import org.junit.Ignore;
import org.junit.Test;
import rigor.io.junkshop.printing.PrintingService;

@Ignore
public class PrintingTest {
  private PrintingService ps = new PrintingService();

  @Test
  public void print() {
    Printer s = ps.defaultPrinter();
    System.out.println(s);
    ps.allPrinters().forEach(System.out::println);
  }
}
