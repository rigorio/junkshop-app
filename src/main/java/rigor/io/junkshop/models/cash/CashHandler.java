package rigor.io.junkshop.models.cash;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CashHandler {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/cash";

  public List<Cash> getCash(String accountId) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL + "?accountId=" + accountId)
        .build();
    Call call = client.newCall(request);
    List<Cash> cash = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      cash = new ObjectMapper().readValue(string, new TypeReference<List<Cash>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cash;
  }

  public List<Cash> getMonthlyCash(String accountId) {
    List<Cash> allCash = getCash(accountId);
    List<Cash> monthlyCashes = new ArrayList<>();
    for (Cash cash : allCash) {
      LocalDate date = LocalDate.parse(cash.getDate());
      String span = date.getMonth() + " " + date.getYear();
      Optional<Cash> anyCash = monthlyCashes.stream()
          .filter(monthlyCash -> monthlyCash.getDate().equals(span))
          .findAny();
      Cash monthlyCash;
      if (anyCash.isPresent()) {
        monthlyCash = anyCash.get();
        monthlyCash.setCapital(futarini(monthlyCash.getCapital(), cash.getCapital()));
        monthlyCash.setCashOnHand(futarini(monthlyCash.getCashOnHand(), cash.getCashOnHand()));
        monthlyCash.setExpenses(futarini(monthlyCash.getExpenses(), cash.getExpenses()));
        monthlyCash.setPurchases(futarini(monthlyCash.getPurchases(), cash.getPurchases()));
        monthlyCash.setSales(futarini(monthlyCash.getSales(), cash.getSales()));
      } else {
        monthlyCash = cash;
        monthlyCash.setDate(span);
        monthlyCashes.add(monthlyCash);
      }
    }
    return monthlyCashes;
  }

  public String futarini(String s1, String s2) {
    return "" + ((s1 != null ? Double.valueOf(s1) : 0.0d) + (s2 != null ? Double.valueOf(s2) : 0.0d));
  }

  public Cash today(String accountId) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL + "/today" + "?accountId=" + accountId)
        .build();
    Call call = client.newCall(request);
    Cash cash = new Cash();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      cash = new ObjectMapper().readValue(string, new TypeReference<Cash>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cash;
  }

  public void sendCash(Cash cash, String accountId) {
    OkHttpClient client = new OkHttpClient();
    try {
      cash.setDate(LocalDate.now().toString());
      cash.setAccountId(PublicCache.getAccountId());
      String jsonString = new ObjectMapper().writeValueAsString(cash);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL + "?accountId="+ accountId)
          .post(reqbody)
          .build();
      Call call = client.newCall(request);
      Cash savedCash = new Cash();
      ResponseBody body = call.execute().body();
      String string = body.string();
      savedCash = new ObjectMapper().readValue(string, new TypeReference<Cash>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
