package rigor.io.junkshop.models.junk;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JunkFX {
  private StringProperty id;
  private StringProperty material;
  private StringProperty price;
  private StringProperty totalPrice;
  private StringProperty weight;
  private StringProperty date;
  private StringProperty note;

  public JunkFX(Junk junk) {
    String id = junk.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String material = junk.getMaterial();
    this.material = material != null ? new SimpleStringProperty(material) : null;

    String price = junk.getPrice();
    this.price = price != null ? new SimpleStringProperty(price) : null;

    String weight = junk.getWeight();
    this.weight = weight != null ? new SimpleStringProperty(weight) : null;

    String totalPrice = junk.getTotalPrice();
    this.totalPrice = totalPrice != null ? new SimpleStringProperty(totalPrice) : null;

    String date = junk.getDate();
    this.date = date != null ? new SimpleStringProperty(date) : null;

    String note = junk.getNote();
    this.note = note != null ? new SimpleStringProperty(note) : null;
  }
}
