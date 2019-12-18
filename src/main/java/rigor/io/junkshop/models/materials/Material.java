package rigor.io.junkshop.models.materials;

import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Material {
  private Long id;
  private String material;
  private String standardPrice;

  public Material(MaterialFX material) {
    LongProperty id = material.getId();
    this.id = id != null ? id.get() : null;

    StringProperty materiall = material.getMaterial();
    this.material = materiall != null ? materiall.get() : null;

    StringProperty standardPrice = material.getStandardPrice();
    this.standardPrice = standardPrice != null ? standardPrice.get() : null;
  }
}
