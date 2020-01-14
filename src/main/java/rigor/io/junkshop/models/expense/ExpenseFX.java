package rigor.io.junkshop.models.expense;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseFX {
  private StringProperty id;
  private StringProperty name;
  private StringProperty amount;
  private StringProperty note;
  private StringProperty date;
  private StringProperty accountId;

  public ExpenseFX(Expense expense) {
    String id = expense.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String name = expense.getName();
    this.name = name != null ? new SimpleStringProperty(name) : null;

    String amount = expense.getAmount();
    this.amount = amount != null ? new SimpleStringProperty(amount) : null;

    String date = expense.getDate();
    this.date = date != null ? new SimpleStringProperty(date) : null;

    String note = expense.getNote();
    this.note = note != null ? new SimpleStringProperty(note) : null;

    String accountId = expense.getAccountId();
    this.accountId = accountId != null ? new SimpleStringProperty(accountId) : null;
  }
}
