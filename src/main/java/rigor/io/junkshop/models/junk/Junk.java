package rigor.io.junkshop.models.junk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
  private Long id;
  private String material;
  private String price;
  private String weight;
  private String date;

  public Junk(String material, String price, String weight) {
    this.material = material;
    this.price = price;
    this.weight = weight;
  }
}
