package rigor.io.junkshop.models.sale;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleItemFX {
  private StringProperty id;
  private StringProperty material;
  private StringProperty note;
  private StringProperty price;
  private StringProperty weight;
  private StringProperty totalPrice;

  public SaleItemFX(SaleItem saleItem) {
    String id = saleItem.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String material = saleItem.getMaterial();
    this.material = material != null ? new SimpleStringProperty(material) : null;

    String price = saleItem.getPrice();
    this.price = price != null ? new SimpleStringProperty(price) : null;

    String weight = saleItem.getWeight();
    this.weight = weight != null ? new SimpleStringProperty(weight) : null;

    String totalPrice = saleItem.getTotalPrice();
    this.totalPrice = totalPrice != null ? new SimpleStringProperty(totalPrice) : null;

    String note = saleItem.getNote();
    this.note = note != null ? new SimpleStringProperty(note) : null;
  }
}
