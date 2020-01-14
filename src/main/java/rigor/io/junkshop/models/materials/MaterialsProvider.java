package rigor.io.junkshop.models.materials;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialsProvider {

  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String url = Configurations.getInstance().getHost() +  "/materials";

  public List<Material> getMaterials() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(url+ "?accountId="+ PublicCache.getAccountId())
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
        .url(url + "/page"+ "?accountId="+ PublicCache.getAccountId())
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

  public List<Material> addMaterial(Material material) {
    OkHttpClient client = new OkHttpClient();
    try {
      material.setAccountId(PublicCache.getAccountId());
      String jsonString = new ObjectMapper().writeValueAsString(material);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(url)
          .post(reqbody)
          .build();
      Call call = client.newCall(request);
      List<Material> mlist = new ArrayList<>();
      ResponseBody body = call.execute().body();
      if (body != null) {
        String string = body.string();
        mlist = new ObjectMapper().readValue(string, new TypeReference<List<Material>>() {});
        return mlist;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  public List<Material> deleteMaterial(Material material) {
    OkHttpClient client = new OkHttpClient();
    try {
      String jsonString = new ObjectMapper().writeValueAsString(material);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(url)
          .delete(reqbody)
          .build();
      Call call = client.newCall(request);
      List<Material> mlist = new ArrayList<>();
      ResponseBody body = call.execute().body();
      if (body != null) {
        String string = body.string();
        mlist = new ObjectMapper().readValue(string, new TypeReference<List<Material>>() {});
        return mlist;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
}
