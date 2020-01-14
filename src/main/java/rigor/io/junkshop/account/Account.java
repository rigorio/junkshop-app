package rigor.io.junkshop.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  private String id;
  private String username;
  private String password;

  public Account(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
