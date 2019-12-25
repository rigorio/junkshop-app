package rigor.io.junkshop.models.purchase;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItem {
  private String id;
  private String material;
  private String price;
  private String weight;

  public PurchaseItem(PurchaseItemFX purchaseItem) {
    StringProperty id = purchaseItem.getId();
    this.id = id != null ? id.get() : null;

    StringProperty material = purchaseItem.getMaterial();
    this.material = material != null ? material.get() : null;

    StringProperty price = purchaseItem.getPrice();
    this.price = price != null ? price.get() : null;

    StringProperty weight = purchaseItem.getWeight();
    this.weight = weight != null ? weight.get() : null;
  }
}
