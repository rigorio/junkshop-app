package rigor.io.junkshop.models.materials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Material {
  private String id;
  private String material;
  private String standardPrice;
  private String weight;

  public Material(MaterialFX material) {
    StringProperty id = material.getId();
    this.id = id != null ? id.get() : null;

    StringProperty materiall = material.getMaterial();
    this.material = materiall != null ? materiall.get() : null;

    StringProperty standardPrice = material.getStandardPrice();
    this.standardPrice = standardPrice != null ? standardPrice.get() : null;

    StringProperty weight = material.getWeight();
    this.weight = weight != null ? weight.get() : null;
  }
}
