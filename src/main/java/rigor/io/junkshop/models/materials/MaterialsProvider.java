package rigor.io.junkshop.models.materials;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MaterialsProvider {

  public List<Material> getMaterials() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url("http://localhost:8080/api/materials")
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
}
