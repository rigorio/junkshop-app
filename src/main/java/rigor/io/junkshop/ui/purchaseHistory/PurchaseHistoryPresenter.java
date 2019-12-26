package rigor.io.junkshop.ui.purchaseHistory;

import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import rigor.io.junkshop.models.purchase.Purchase;
import rigor.io.junkshop.models.purchase.PurchaseFX;
import rigor.io.junkshop.models.purchase.PurchaseHandler;
import rigor.io.junkshop.utils.TaskTool;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurchaseHistoryPresenter implements Initializable {
  @FXML
  private TableView<PurchaseFX> historyTable;
  @FXML
  private JFXDatePicker datePicker;

  private PurchaseHandler purchaseHandler;

  public PurchaseHistoryPresenter() {
    purchaseHandler = new PurchaseHandler();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<PurchaseFX, String> receiptNumber = new TableColumn<>("Receipt #");
    receiptNumber.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getId().get()));

    TableColumn<PurchaseFX, String> price = new TableColumn<>("Total Price");
    price.setCellValueFactory(e -> e.getValue().getTotalPrice());

    TableColumn<PurchaseFX, String> date = new TableColumn<>("Date");
    date.setCellValueFactory(e -> e.getValue().getDate());

    TableColumn<PurchaseFX, String> items = new TableColumn<>("No. Items");
    items.setCellValueFactory(e -> new SimpleStringProperty("" + e.getValue().getPurchaseItems().size()));


    historyTable.getColumns().addAll(receiptNumber,
                                     items,
                                     price,
                                     date);

//    datePicker.setValue(LocalDate.now());

    initTable();
  }

  private void initTable() {
    TaskTool<List<Purchase>> tool = new TaskTool<>();
    Task<List<Purchase>> task = tool.createTask(this::purchaseHistory);
    task.setOnSucceeded(e -> {
      Stream<Purchase> purchaseStream = task.getValue()
          .stream();
      List<PurchaseFX> purchases = purchaseStream
          .map(PurchaseFX::new)
          .collect(Collectors.toList());
      historyTable.setItems(FXCollections.observableList(purchases));
    });
    tool.execute(task);
  }

  private List<Purchase> purchaseHistory() {
    return purchaseHandler.getPurchases();
  }

  @FXML
  public void changeDate() {

  }
}
