package rigor.io.junkshop.models.junk.junklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rigor.io.junkshop.models.junk.Junk;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JunkList {
  private String id;
  private String receiptNumber;
  private List<Junk> purchaseItems;
  private String totalPrice;
  private String date;
}
