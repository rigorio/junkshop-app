package rigor.io.junkshop.models.sale;

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
public class Sale {
  private String id;
  private List<SaleItem> saleItems;
  private String totalPrice;
  private String date;

  public Sale(SaleFX purchase) {
    StringProperty id = purchase.getId();
    this.id = id != null ? id.get() : null;

    StringProperty totalPrice = purchase.getTotalPrice();
    this.totalPrice = totalPrice != null ? totalPrice.get() : null;

    List<SaleItemFX> purchaseItems = purchase.getPurchaseItems();
    this.saleItems = purchaseItems.stream()
        .map(SaleItem::new)
        .collect(Collectors.toList());

    StringProperty date = purchase.getDate();
    this.date = date != null ? date.get() : null;
  }
}
