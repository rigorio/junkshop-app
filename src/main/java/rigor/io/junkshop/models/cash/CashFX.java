package rigor.io.junkshop.models.cash;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashFX {
  private StringProperty id;
  private StringProperty capital;
  private StringProperty sales;
  private StringProperty purchases;
  private StringProperty expenses;
  private StringProperty cashOnHand;
  private StringProperty date;

  public CashFX(Cash cash) {
    String id = cash.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String capital = cash.getCapital();
    this.capital = capital != null ? new SimpleStringProperty(capital) : null;

    String sales = cash.getSales();
    this.sales = sales != null ? new SimpleStringProperty(sales) : null;

    String purchases = cash.getPurchases();
    this.purchases = purchases != null ? new SimpleStringProperty(purchases) : null;

    String expenses = cash.getExpenses();
    this.expenses = expenses != null ? new SimpleStringProperty(expenses) : null;

    String cashOnHand = cash.getCashOnHand();
    this.cashOnHand = cashOnHand != null ? new SimpleStringProperty(cashOnHand) : null;

    String date = cash.getDate();
    this.date = date != null ? new SimpleStringProperty(date) : null;
  }
}
