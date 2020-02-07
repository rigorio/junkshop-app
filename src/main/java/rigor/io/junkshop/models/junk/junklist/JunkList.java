package rigor.io.junkshop.models.junk.junklist;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rigor.io.junkshop.models.junk.Junk;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JunkList {
  private String id;
  private String receiptNumber;
  private List<Junk> purchaseItems;
  private String totalPrice;
  private String date;
  private String clientId;
  private String accountId;

  public JunkList(PurchaseFX purchaseFX) {
    StringProperty id = purchaseFX.getId();
    this.id = id != null ? id.get() : null;

    StringProperty receiptNumber = purchaseFX.getReceiptNumber();
    this.receiptNumber = receiptNumber != null ? receiptNumber.get() : null;

    this.purchaseItems = purchaseFX.getPurchaseItems().stream()
        .map(Junk::new)
        .collect(Collectors.toList());

    StringProperty totalPrice = purchaseFX.getTotalPrice();
    this.totalPrice = totalPrice != null ? totalPrice.get() : null;

    StringProperty date = purchaseFX.getDate();
    this.date = date != null ? date.get() : null;

    StringProperty clientId = purchaseFX.getClientId();
    this.clientId = clientId != null ? clientId.get() : null;

    StringProperty accountId = purchaseFX.getAccountId();
    this.accountId = accountId != null ? accountId.get() : null;
  }
}
