package rigor.io.junkshop.models.sale;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sale {
  private String id;
  private String receiptNumber;
  private List<SaleItem> saleItems;
  private String totalPrice;
  private String date;

  public Sale(SaleFX sale) {
    StringProperty id = sale.getId();
    this.id = id != null ? id.get() : null;

    StringProperty totalPrice = sale.getTotalPrice();
    this.totalPrice = totalPrice != null ? totalPrice.get() : null;

    List<SaleItemFX> purchaseItems = sale.getPurchaseItems();
    this.saleItems = purchaseItems.stream()
        .map(SaleItem::new)
        .collect(Collectors.toList());

    StringProperty date = sale.getDate();
    this.date = date != null ? date.get() : null;

    StringProperty receiptNumber = sale.getReceiptNumber();
    this.receiptNumber = receiptNumber != null ? receiptNumber.get() : null;
  }
}
