package rigor.io.junkshop.models.sale;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleItem {
  private String id;
  private String material;
  private String note;
  private String price;
  private String weight;
  private String totalPrice;

  public SaleItem(SaleItemFX purchaseItem) {
    StringProperty id = purchaseItem.getId();
    this.id = id != null ? id.get() : null;

    StringProperty material = purchaseItem.getMaterial();
    this.material = material != null ? material.get() : null;

    StringProperty price = purchaseItem.getPrice();
    this.price = price != null ? price.get() : null;

    StringProperty weight = purchaseItem.getWeight();
    this.weight = weight != null ? weight.get() : null;

    StringProperty note = purchaseItem.getNote();
    this.note = note != null ? note.get() : null;

    StringProperty totalPrice = purchaseItem.getTotalPrice();
    this.totalPrice = totalPrice != null ? totalPrice.get() : null;
  }
}
