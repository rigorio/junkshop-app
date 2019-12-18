package rigor.io.junkshop.models.purchase;

import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {
  private Long id;
  private List<PurchaseItem> purchaseItems;
  private String totalPrice;
  private String date;

  public Purchase(PurchaseFX purchase) {
    LongProperty id = purchase.getId();
    this.id = id != null ? id.get() : null;

    StringProperty totalPrice = purchase.getTotalPrice();
    this.totalPrice = totalPrice != null ? totalPrice.get() : null;

    List<PurchaseItemFX> purchaseItems = purchase.getPurchaseItems();
    this.purchaseItems = purchaseItems.stream()
        .map(PurchaseItem::new)
        .collect(Collectors.toList());

    StringProperty date = purchase.getDate();
    this.date = date != null ? date.get() : null;
  }
}
