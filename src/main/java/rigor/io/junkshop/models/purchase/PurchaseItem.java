package rigor.io.junkshop.models.purchase;

import javafx.beans.property.LongProperty;
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
  private Long id;
  private String material;
  private String type;
  private String price;
  private String weight;

  public PurchaseItem(PurchaseItemFX purchaseItem) {
    LongProperty id = purchaseItem.getId();
    this.id = id != null ? id.get() : null;

    StringProperty material = purchaseItem.getMaterial();
    this.material = material != null ? material.get() : null;

    StringProperty type = purchaseItem.getType();
    this.type = type != null ? type.get() : null;

    StringProperty price = purchaseItem.getPrice();
    this.price = price != null ? price.get() : null;

    StringProperty weight = purchaseItem.getWeight();
    this.weight = weight != null ? weight.get() : null;
  }
}
