package rigor.io.junkshop.models.sales;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SalesMan {
  private String URL = Configurations.getInstance().getHost() + "/salessummary";

  public List<SalesEntity> getSales() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL + "/month" + "?accountId=" + PublicCache.getAccountId())
        .build();
    Call call = client.newCall(request);
    List<SalesEntity> sales = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      sales = new ObjectMapper().readValue(string, new TypeReference<List<SalesEntity>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sales;
  }
}
