package rigor.io.junkshop.models.junk.junklist;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rigor.io.junkshop.models.junk.JunkFX;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseFX {
  private StringProperty id;
  private StringProperty receiptNumber;
  private List<JunkFX> purchaseItems;
  private StringProperty totalPrice;
  private StringProperty date;
  private StringProperty clientId;

  public PurchaseFX(JunkList purchase) {
    String id = purchase.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String receiptNumber = purchase.getReceiptNumber();
    this.receiptNumber = receiptNumber != null ? new SimpleStringProperty(receiptNumber) : null;

    this.purchaseItems = purchase.getPurchaseItems().stream().map(JunkFX::new).collect(Collectors.toList());

    String totalPrice = purchase.getTotalPrice();
    this.totalPrice = totalPrice != null ? new SimpleStringProperty(totalPrice) : null;

    String date = purchase.getDate();
    this.date = date != null ? new SimpleStringProperty(date) : null;

    String clientId = purchase.getClientId();
    this.clientId = clientId != null ? new SimpleStringProperty(clientId) : null;
  }
}
