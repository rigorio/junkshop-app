package rigor.io.junkshop.models.purchase;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseFX {
  private LongProperty id;
  private StringProperty totalPrice;
  private List<PurchaseItemFX> purchaseItems;
  private StringProperty date;

  public PurchaseFX(Purchase purchase) {
    Long id = purchase.getId();
    this.id = id != null ? new SimpleLongProperty(id) : null;

    String totalPrice = purchase.getTotalPrice();
    this.totalPrice = totalPrice != null ? new SimpleStringProperty(totalPrice) : null;

    String date = purchase.getDate();
    this.date = date != null ? new SimpleStringProperty(date) : null;

    List<PurchaseItem> purchaseItems = purchase.getPurchaseItems();
    this.purchaseItems = purchaseItems.stream()
        .map(PurchaseItemFX::new)
        .collect(Collectors.toList());
  }
}
