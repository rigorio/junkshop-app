package rigor.io.junkshop.models.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
  private String id;
  private String name;
  private String contact;
  private String address;
  private String cashAdvance;

  @Override
  public String toString() {
    return name;
  }
}