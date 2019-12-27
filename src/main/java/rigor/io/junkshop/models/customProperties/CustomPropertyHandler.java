package rigor.io.junkshop.models.customProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.config.Configurations;
import rigor.io.junkshop.models.expense.Expense;

import java.io.IOException;

public class CustomPropertyHandler {

  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/properties";


  public CustomProperty getProperty(String property) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL + "/" + property)
        .build();
    Call call = client.newCall(request);
    CustomProperty customProperty = new CustomProperty();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      System.out.println(string);
      customProperty = new ObjectMapper().readValue(string, new TypeReference<CustomProperty>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return customProperty;
  }

  public void sendProperty(CustomProperty customProperty) {
    OkHttpClient client = new OkHttpClient();
    try {
      String jsonString = new ObjectMapper().writeValueAsString(customProperty);
      RequestBody reqBody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .post(reqBody)
          .build();
      Call call = client.newCall(request);
      CustomProperty savedProperty = new CustomProperty();
      ResponseBody body = call.execute().body();
      String string = body.string();
      savedProperty = new ObjectMapper().readValue(string, new TypeReference<CustomProperty>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
