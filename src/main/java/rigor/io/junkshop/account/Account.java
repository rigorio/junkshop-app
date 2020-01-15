package rigor.io.junkshop.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  private String id;
  private String username;
  private String password;
  private String role;

  public Account(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public String toString() {
    return username;
  }
}
