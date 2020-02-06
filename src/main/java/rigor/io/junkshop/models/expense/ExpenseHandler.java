package rigor.io.junkshop.models.expense;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ExpenseHandler {

  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/expenses";


  public List<Expense> getExpenses(String accountId) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL + "?accountId=" + accountId)
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

  public Map<String, Object> getExpensesAndDailies(String accountId) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL + "/daily?accountId=" + accountId)
        .build();
    Call call = client.newCall(request);
    Map<String, Object> expenses = new HashMap<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      expenses = new ObjectMapper().readValue(string, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return expenses;
  }

  public List<Expense> getMonthlyExpenses(String accountId) {
    List<Expense> expenses = getExpenses(accountId);
    List<Expense> monthlyExpenses = new ArrayList<>();
    for (Expense expense : expenses) {
      LocalDate date = LocalDate.parse(expense.getDate());
      String span = date.getMonth() + " " + date.getYear();
      Optional<Expense> any = monthlyExpenses.stream()
          .filter(monthlyExpense -> monthlyExpense.getDate().equals(span))
          .findAny();
      Expense monthlyExpense;
      if (any.isPresent()) {
        monthlyExpense = any.get();
        monthlyExpense.setAmount(futarini(monthlyExpense.getAmount(), expense.getAmount()));
      } else {
        monthlyExpense = new Expense();
        monthlyExpense.setDate(span);
        monthlyExpense.setAmount(expense.getAmount());
        monthlyExpenses.add(monthlyExpense);
      }
    }
    return monthlyExpenses;
  }

  public String futarini(String s1, String s2) {
    return "" + ((s1 != null ? Double.valueOf(s1) : 0.0d) + (s2 != null ? Double.valueOf(s2) : 0.0d));
  }

  public Map<String, Object> sendExpense(Expense expense) {
    OkHttpClient client = new OkHttpClient();
    Map<String, Object> expenses = new HashMap<>();
    try {
      expense.setAccountId(PublicCache.getAccountId());
      String jsonString = new ObjectMapper().writeValueAsString(expense);
      RequestBody reqBody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL + "/daily?accountId=" + PublicCache.getAccountId())
          .post(reqBody)
          .build();
      Call call = client.newCall(request);
      ResponseBody body = call.execute().body();
      String string = body.string();
//      System.out.println(string);
      expenses = new ObjectMapper().readValue(string, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return expenses;
  }

  public Map<String, Object> deleteExpense(List<Expense> expense) {
    OkHttpClient client = new OkHttpClient();
      Map<String, Object> expenses = new HashMap<>();
    try {
      String jsonString = new ObjectMapper().writeValueAsString(expense);
      RequestBody reqBody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL + "/daily?accountId=" + PublicCache.getAccountId())
          .delete(reqBody)
          .build();
      Call call = client.newCall(request);
      ResponseBody body = call.execute().body();
      if (body != null) {
        String string = body.string();
        if (string != null && string.length() > 0)
          expenses = new ObjectMapper().readValue(string, new TypeReference<Map<String, Object>>() {});
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return expenses;
  }

}
