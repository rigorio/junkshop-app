package rigor.io.junkshop.junk;

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
public class JunkFX {
  private LongProperty id;
  private StringProperty material;
  private StringProperty price;
  private StringProperty weight;

  public JunkFX(Junk junk) {
    Long id = junk.getId();
    this.id = id != null ? new SimpleLongProperty(id) : null;

    String material = junk.getMaterial();
    this.material = material != null ? new SimpleStringProperty(material) : null;

    String price = junk.getPrice();
    this.price = price != null ? new SimpleStringProperty(price) : null;

    String weight = junk.getWeight();
    this.weight = weight != null ? new SimpleStringProperty(weight) : null;
  }
}
