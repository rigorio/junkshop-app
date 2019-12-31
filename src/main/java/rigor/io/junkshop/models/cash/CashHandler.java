package rigor.io.junkshop.models.cash;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CashHandler {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/cash";

  public List<Cash> getCash() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL)
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

  public void sendCash(Cash cash) {
    OkHttpClient client = new OkHttpClient();
    try {
      cash.setDate(LocalDate.now().toString());
      String jsonString = new ObjectMapper().writeValueAsString(cash);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
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
