package rigor.io.junkshop.models.sale;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleFX {
  private StringProperty id;
  private StringProperty receiptNumber;
  private StringProperty totalPrice;
  private List<SaleItemFX> purchaseItems;
  private StringProperty date;
  private StringProperty clientId;
  private StringProperty accountId;

  public SaleFX(Sale sale) {
    String id = sale.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String totalPrice = sale.getTotalPrice();
    this.totalPrice = totalPrice != null ? new SimpleStringProperty(totalPrice) : null;

    String date = sale.getDate();
    this.date = date != null ? new SimpleStringProperty(date) : null;

    List<SaleItem> saleItems = sale.getSaleItems();
    this.purchaseItems = saleItems.stream()
        .map(SaleItemFX::new)
        .collect(Collectors.toList());
    String receiptNumber = sale.getReceiptNumber();
    this.receiptNumber = receiptNumber != null ? new SimpleStringProperty(receiptNumber) : null;

    String clientId = sale.getClientId();
    this.clientId = clientId != null ? new SimpleStringProperty(clientId) : null;

    String accountId = sale.getAccountId();
    this.accountId = accountId != null ? new SimpleStringProperty(accountId) : null;
  }
}
