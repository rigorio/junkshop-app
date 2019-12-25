package rigor.io.junkshop.models.materials;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialFX {
  private StringProperty id;
  private StringProperty material;
  private StringProperty standardPrice;
  private StringProperty weight;

  public MaterialFX(Material material) {
    String id = material.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String materiall = material.getMaterial();
    this.material = materiall != null ? new SimpleStringProperty(materiall) : null;

    String standardPrice = material.getStandardPrice();
    this.standardPrice = standardPrice != null ? new SimpleStringProperty(standardPrice) : null;

    String weight = material.getWeight();
    this.weight = weight != null ? new SimpleStringProperty(weight) : null;
  }
}
