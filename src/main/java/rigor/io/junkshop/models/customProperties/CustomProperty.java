package rigor.io.junkshop.models.customProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomProperty {
  private String id;
  private String property;
  private String value;

  public CustomProperty(String property, String value) {
    this.property = property;
    this.value = value;
  }
}
