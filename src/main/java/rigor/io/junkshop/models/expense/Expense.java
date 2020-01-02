package rigor.io.junkshop.models.expense;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
  private String id;
  private String name;
  private String amount;
  private String note;
  private String date;

  public Expense(ExpenseFX expenseFX) {
    StringProperty id = expenseFX.getId();
    this.id = id != null ? id.get() : null;

    StringProperty name = expenseFX.getName();
    this.name = name != null ? name.get() : null;

    StringProperty amount = expenseFX.getAmount();
    this.amount = amount != null ? amount.get() : null;

    StringProperty date = expenseFX.getDate();
    this.date = date != null ? date.get() : null;
  }

  public Expense(String name, String note, String amount) {
    this.name = name;
    this.note = note;
    this.amount = amount;
    this.date = LocalDate.now().toString();
  }
}
