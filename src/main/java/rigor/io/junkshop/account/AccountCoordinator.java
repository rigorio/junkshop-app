package rigor.io.junkshop.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String string = response.body().string();
        Map<String, String> map = new ObjectMapper().readValue(string, new TypeReference<Map<String, String>>() {});
        PublicCache.values.put("id", map.get("id"));
        PublicCache.values.put("role", map.get("role"));
        return true;
      } else
        return false;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  public List<Account> allAccounts() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL)
        .build();
    Call call = client.newCall(request);
    List<Account> accounts = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      accounts = new ObjectMapper().readValue(string, new TypeReference<List<Account>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return accounts;
  }

  public Account save(Account account) {
    try {
      String jsonString = new ObjectMapper().writeValueAsString(account);
      System.out.println(jsonString);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL + "/create")
          .post(reqbody)
          .build();
      Call call = new OkHttpClient().newCall(request);
      ResponseBody body = call.execute().body();
      String string = body.string();
      account = new ObjectMapper().readValue(string, new TypeReference<List<Account>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return account;
  }
}
