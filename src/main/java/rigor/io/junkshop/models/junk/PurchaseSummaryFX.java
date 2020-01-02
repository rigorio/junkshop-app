package rigor.io.junkshop.models.junk;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSummaryFX {
  private StringProperty span;
  private StringProperty amount;
}
