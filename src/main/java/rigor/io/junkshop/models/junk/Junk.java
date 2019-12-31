package rigor.io.junkshop.models.junk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Junk {
  private String id;
  private String material;
  private String price;
  private String note;
  private String weight;
  private String totalPrice;
  private String date;

  public Junk(JunkFX junk) {
    StringProperty id = junk.getId();
    this.id = id != null ? id.get() : null;

    StringProperty material = junk.getMaterial();
    this.material = material != null ? material.get() : null;

    StringProperty price = junk.getPrice();
    this.price = price != null ? price.get() : null;

    StringProperty weight = junk.getWeight();
    this.weight = weight != null ? weight.get() : null;

    StringProperty totalPrice = junk.getTotalPrice();
    this.totalPrice = totalPrice != null ? totalPrice.get() : null;

    StringProperty date = junk.getDate();
    this.date = date != null ? date.get() : null;

    StringProperty note = junk.getNote();
    this.note = note != null ? note.get() : null;
  }
}
