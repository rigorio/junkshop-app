package rigor.io.junkshop.models.purchase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHandler {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/purchase";

  public List<Purchase> getPurchases() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL)
        .build();
    Call call = client.newCall(request);
    List<Purchase> purchases = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      purchases = new ObjectMapper().readValue(string, new TypeReference<List<Purchase>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return purchases;
  }

  public void sendPurchase(Purchase purchase) {
    OkHttpClient client = new OkHttpClient();
    try {
      purchase.setDate(LocalDate.now().toString());
      String jsonString = new ObjectMapper().writeValueAsString(purchase);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .post(reqbody)
          .build();
      Call call = client.newCall(request);
      ResponseBody body = call.execute().body();
      String string = body.string();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
