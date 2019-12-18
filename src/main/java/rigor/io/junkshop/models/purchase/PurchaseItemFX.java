package rigor.io.junkshop.models.purchase;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemFX {
  private LongProperty id;
  private StringProperty material;
  private StringProperty type;
  private StringProperty price;
  private StringProperty weight;

  public PurchaseItemFX(PurchaseItem purchaseItem) {
    Long id = purchaseItem.getId();
    this.id = id != null ? new SimpleLongProperty(id) : null;

    String material = purchaseItem.getMaterial();
    this.material = material != null ? new SimpleStringProperty(material) : null;

    String type = purchaseItem.getType();
    this.type = type != null ? new SimpleStringProperty(type) : null;

    String price = purchaseItem.getPrice();
    this.price = price != null ? new SimpleStringProperty(price) : null;

    String weight = purchaseItem.getWeight();
    this.weight = weight != null ? new SimpleStringProperty(weight) : null;
  }
}
