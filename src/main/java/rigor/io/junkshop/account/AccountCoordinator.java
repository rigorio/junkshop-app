package rigor.io.junkshop.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;

public class AccountCoordinator {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/accounts";

  public boolean login(String username, String password) {
    try {
      Account account = new Account(username, password);
      String jsonString = new ObjectMapper().writeValueAsString(account);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL + "/login")
          .post(reqbody)
          .build();
      Call call = new OkHttpClient().newCall(request);
      Response response = call.execute();
      if (response.code() == 202) {
        PublicCache.values.put("id", response.body().string());
        return true;
      } else
        return false;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}
