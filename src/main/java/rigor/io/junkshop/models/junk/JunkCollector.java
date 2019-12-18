package rigor.io.junkshop.models.junk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JunkCollector {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");

  public List<Junk> getJunk() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url("http://localhost:8080/api/junk")
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

  public void sendJunk(Junk junk) {
    OkHttpClient client = new OkHttpClient();
    try {
      junk.setDate(LocalDate.now().toString());
      String jsonString = new ObjectMapper().writeValueAsString(junk);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url("http://localhost:8080/api/junk")
          .post(reqbody)
          .build();
      Call call = client.newCall(request);
      Junk junks = new Junk();
      ResponseBody body = call.execute().body();
      String string = body.string();
      System.out.println("ah yeah");
      System.out.println(string);
      junks = new ObjectMapper().readValue(string, new TypeReference<Junk>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
