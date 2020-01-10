package rigor.io.junkshop.models.materials;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialsProvider {

  private String url = Configurations.getInstance().getHost() +  "/materials";

  public List<Material> getMaterials() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(url)
        .build();
    Call call = client.newCall(request);
    List<Material> materials = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      materials = new ObjectMapper().readValue(string, new TypeReference<List<Material>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return materials;
  }

  public Map<String, Object> getItems() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(url + "/page")
        .build();
    Call call = client.newCall(request);
    Map<String, Object> materials = new HashMap<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      materials = new ObjectMapper().readValue(string, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return materials;
  }
}
