package rigor.io.junkshop.models.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientFX {
  private StringProperty id;
  private StringProperty name;
  private StringProperty contact;
  private StringProperty address;
  private StringProperty cashAdvance;
  private StringProperty accountId;

  public ClientFX(Client client) {
    String id = client.getId();
    this.id = id != null ? new SimpleStringProperty(id) : null;

    String name = client.getName();
    this.name = name != null ? new SimpleStringProperty(name) : null;

    String contact = client.getContact();
    this.contact = contact != null ? new SimpleStringProperty(contact) : null;

    String address = client.getAddress();
    this.address = address != null ? new SimpleStringProperty(address) : null;

    String cashAdvance = client.getCashAdvance();
    this.cashAdvance = cashAdvance != null ? new SimpleStringProperty(cashAdvance) : null;

    String accountId = client.getAccountId();
    this.accountId = accountId != null ? new SimpleStringProperty(accountId) : null;
  }
}
