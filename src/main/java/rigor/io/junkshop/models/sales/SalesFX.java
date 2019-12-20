package rigor.io.junkshop.models.sales;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesFX {
  private StringProperty span;
  private StringProperty sales;

  public SalesFX(SalesEntity salesEntity) {
    String span = salesEntity.getSpan();
    this.span = span != null ? new SimpleStringProperty(span) : null;

    String sales = salesEntity.getSales();
    this.sales = sales != null ? new SimpleStringProperty(sales) : null;
  }
}
