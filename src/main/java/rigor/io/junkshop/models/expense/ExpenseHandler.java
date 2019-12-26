package rigor.io.junkshop.models.expense;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseHandler {

  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/expenses";


  public List<Expense> getExpenses() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL)
        .build();
    Call call = client.newCall(request);
    List<Expense> expenses = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      expenses = new ObjectMapper().readValue(string, new TypeReference<List<Expense>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return expenses;
  }

  public void sendExpense(Expense expense) {
    OkHttpClient client = new OkHttpClient();
    try {
      String jsonString = new ObjectMapper().writeValueAsString(expense);
      RequestBody reqBody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .post(reqBody)
          .build();
      Call call = client.newCall(request);
      Expense expenses = new Expense();
      ResponseBody body = call.execute().body();
      String string = body.string();
      expenses = new ObjectMapper().readValue(string, new TypeReference<Expense>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void deleteExpense(Expense expense) {
    OkHttpClient client = new OkHttpClient();
    try {
      String jsonString = new ObjectMapper().writeValueAsString(expense);
      RequestBody reqBody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .delete(reqBody)
          .build();
      Call call = client.newCall(request);
      Expense expenses = new Expense();
      ResponseBody body = call.execute().body();
      String string = body.string();
      expenses = new ObjectMapper().readValue(string, new TypeReference<Expense>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
