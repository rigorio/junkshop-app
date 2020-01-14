package rigor.io.junkshop.models.sale;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleHandler {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/sale";

  public List<Sale> getSales(String clientId) {
    URL += "?accountId=" + PublicCache.getAccountId();
    if (clientId != null) {
      URL += "&clientId=" + clientId;
    }
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL)
        .build();
    Call call = client.newCall(request);
    List<Sale> sales = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      sales = new ObjectMapper().readValue(string, new TypeReference<List<Sale>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sales;
  }

  public void sendPurchase(Sale sale) {
    OkHttpClient client = new OkHttpClient();
    try {
      sale.setDate(LocalDate.now().toString());
      sale.setAccountId(PublicCache.getAccountId());
      String jsonString = new ObjectMapper().writeValueAsString(sale);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL + "?accountId=" + PublicCache.getAccountId())
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
