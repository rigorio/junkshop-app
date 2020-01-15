package rigor.io.junkshop.ui.purchaseHistory;

import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.models.sale.Sale;
import rigor.io.junkshop.models.sale.SaleFX;
import rigor.io.junkshop.models.sale.SaleHandler;
import rigor.io.junkshop.utils.TaskTool;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurchaseHistoryPresenter implements Initializable {
  @FXML
  private TableView<SaleFX> historyTable;
  @FXML
  private JFXDatePicker datePicker;

  private SaleHandler saleHandler;

  public PurchaseHistoryPresenter() {
    saleHandler = new SaleHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<SaleFX, String> receiptNumber = new TableColumn<>("Receipt #");
    receiptNumber.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getReceiptNumber().get()));

    TableColumn<SaleFX, String> price = new TableColumn<>("Total Price");
    price.setCellValueFactory(e -> e.getValue().getTotalPrice());

    TableColumn<SaleFX, String> date = new TableColumn<>("Date");
    date.setCellValueFactory(e -> e.getValue().getDate());

    TableColumn<SaleFX, String> items = new TableColumn<>("No. Items");
    items.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getPurchaseItems().size()));


    historyTable.getColumns().addAll(receiptNumber,
                                     items,
                                     price,
                                     date);

//    datePicker.setValue(LocalDate.now());

    initTable();
  }

  private void initTable() {
    TaskTool<List<Sale>> tool = new TaskTool<>();
    Task<List<Sale>> task = tool.createTask(this::purchaseHistory);
    task.setOnSucceeded(e -> {
      Stream<Sale> purchaseStream = task.getValue()
          .stream();
      List<SaleFX> purchases = purchaseStream
          .map(SaleFX::new)
          .collect(Collectors.toList());
      historyTable.setItems(FXCollections.observableList(purchases));
    });
    tool.execute(task);
  }

  private List<Sale> purchaseHistory() {
    return saleHandler.getSales(null, PublicCache.getAccountId());
  }

  @FXML
  public void changeDate() {

  }
}
