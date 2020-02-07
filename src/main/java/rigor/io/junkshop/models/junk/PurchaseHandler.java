package rigor.io.junkshop.models.junk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;
import rigor.io.junkshop.models.junk.junklist.JunkList;
import rigor.io.junkshop.models.junk.junklist.PurchaseFX;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PurchaseHandler {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/junk";

  public ObservableList<PurchaseFX> getAllPurchases(String accountId, String date) {
    OkHttpClient client = new OkHttpClient();
    String url = URL + "/list?accountId=" + accountId;
    if (date != null)
      url += "&date=" + date;
    System.out.println(url);
    Request request = new Request.Builder()
        .url(url)
        .build();
    Call call = client.newCall(request);
    List<JunkList> junks = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      if (body != null) {
        String string = body.string();
        System.out.println(string);
        junks = new ObjectMapper().readValue(string, new TypeReference<List<JunkList>>() {});
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return FXCollections.observableList(junks.stream().map(PurchaseFX::new).collect(Collectors.toList()));
  }

  public void savePurchases(JunkList purchase) {
    OkHttpClient client = new OkHttpClient();
    try {
      purchase.setAccountId(PublicCache.getAccountId());
      purchase.setDate(LocalDate.now().toString());
      String jsonString = new ObjectMapper().writeValueAsString(purchase);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL + "/list" + "?accountId=" + PublicCache.getAccountId())
          .post(reqbody)
          .build();
      Call call = client.newCall(request);
      JunkList junks = new JunkList();
      ResponseBody body = call.execute().body();
      if (body != null) {
        String string = body.string();
        junks = new ObjectMapper().readValue(string, new TypeReference<JunkList>() {});
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<Junk> getJunk(String clientId, String accountId) {
    OkHttpClient client = new OkHttpClient();
    String url = URL;
    url += "?accountId=" + accountId;
    if (clientId != null) {
      url += "&clientId=" + clientId;
    }
    Request request = new Request.Builder()
        .url(url)
        .build();
    Call call = client.newCall(request);
    List<Junk> junks = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      junks = new ObjectMapper().readValue(string, new TypeReference<List<Junk>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return junks;
  }

  public List<PurchaseSummaryFX> getMonthlyPurchaseSummary(String accountId) {
    List<Junk> junkList = getJunk(null, accountId);
    return getMonthlies(junkList);
  }

  public List<PurchaseSummaryFX> getMonthlies(List<Junk> junkList) {
    List<PurchaseSummaryFX> fxList = new ArrayList<>();
    for (Junk junk : junkList) {
      LocalDate date = LocalDate.parse(junk.getDate());
      String span = date.getMonth() + " " + date.getYear();
      Optional<PurchaseSummaryFX> anyPurchase = fxList.stream()
          .filter(fx -> fx.getSpan().get().equals(span))
          .findAny();
      PurchaseSummaryFX summary = new PurchaseSummaryFX();
      if (anyPurchase.isPresent()) {
        summary = anyPurchase.get();
        Double amount = Double.valueOf(summary.getAmount().get());
        summary.setAmount(new SimpleStringProperty("" + (amount + Double.valueOf(junk.getTotalPrice()))));
      } else {
        summary.setSpan(new SimpleStringProperty(span));
        summary.setAmount(new SimpleStringProperty(junk.getTotalPrice()));
        fxList.add(summary);
      }
    }
    return fxList;
  }

  public void sendJunk(Junk junk) {
    OkHttpClient client = new OkHttpClient();
    try {
      junk.setDate(LocalDate.now().toString());
      String jsonString = new ObjectMapper().writeValueAsString(junk);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .post(reqbody)
          .build();
      Call call = client.newCall(request);
      Junk junks = new Junk();
      ResponseBody body = call.execute().body();
      if (body != null) {
        String string = body.string();
        junks = new ObjectMapper().readValue(string, new TypeReference<Junk>() {});
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void deleteJunk(List<JunkList> purchase) {
    OkHttpClient client = new OkHttpClient();
    try {
      String jsonString = new ObjectMapper().writeValueAsString(purchase);
      System.out.println(jsonString);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL+"/list")
          .delete(reqbody)
          .build();
      Call call = client.newCall(request);
      Response response = call.execute();
      ResponseBody body = response.body();
      if (body != null) {
        System.out.println(response.code());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
