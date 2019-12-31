package rigor.io.junkshop.models.cash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cash {
  private String id;
  private String capital;
  private String sales;
  private String purchases;
  private String expenses;
  private String cashOnHand;
  private String date;
}
