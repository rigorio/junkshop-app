package rigor.io.junkshop.models.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rigor.io.junkshop.models.junk.junklist.JunkList;
import rigor.io.junkshop.models.sale.Sale;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
  private Client client;
  private List<Sale> sales;
  private List<JunkList> purchases;
}
