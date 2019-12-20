package rigor.io.junkshop.ui.salesSummary;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.junk.JunkFX;
import rigor.io.junkshop.models.sales.SalesFX;
import rigor.io.junkshop.models.sales.SalesMan;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SalesSummaryPresenter implements Initializable {

  public TableView salesTable;
  private SalesMan salesMan;

  public SalesSummaryPresenter() {
    salesMan = new SalesMan();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<SalesFX, String> span = new TableColumn<>("Span");
    span.setCellValueFactory(e -> e.getValue().getSpan());

    TableColumn<SalesFX, String> sales = new TableColumn<>("Sales");
    sales.setCellValueFactory(e -> e.getValue().getSales());


    salesTable.getColumns().addAll(span,
                                   sales);

    salesTable.setItems(FXCollections.observableList(salesMan.getSales()
                                                    .stream()
                                                    .map(SalesFX::new)
                                                    .collect(Collectors.toList())));
  }
}
