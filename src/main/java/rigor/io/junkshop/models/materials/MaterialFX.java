package rigor.io.junkshop.models.materials;

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
public class MaterialFX {
  private LongProperty id;
  private StringProperty material;
  private StringProperty standardPrice;

  public MaterialFX(Material material) {
    Long id = material.getId();
    this.id = id != null ? new SimpleLongProperty(id) : null;

    String materiall = material.getMaterial();
    this.material = materiall != null ? new SimpleStringProperty(materiall) : null;

    String standardPrice = material.getStandardPrice();
    this.standardPrice = standardPrice != null ? new SimpleStringProperty(standardPrice) : null;
  }
}
