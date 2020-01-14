package rigor.io.junkshop.models.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import rigor.io.junkshop.cache.PublicCache;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "clients";

  public List<Client> getClients() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL + "?accountId="+ PublicCache.getAccountId())
        .build();
    Call call = client.newCall(request);
    List<Client> clients = new ArrayList<>();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      clients = new ObjectMapper().readValue(string, new TypeReference<List<Client>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return clients;
  }

  public Client askCash(String id, String amount) {
    String url = URL + "/loan?clientId=" + id + "&amount=" + amount + "&accountId="+ PublicCache.getAccountId();
    System.out.println(url);
    Request request = new Request.Builder()
        .url(url)
        .build();
    Call call = new OkHttpClient().newCall(request);
    Client client = new Client();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      client = new ObjectMapper().readValue(string, new TypeReference<Client>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return client;
  }


  public Client payCash(String id, String amount) {
    String url = URL + "/pay?clientId=" + id + "&amount=" + amount + "&accountId="+ PublicCache.getAccountId();
    System.out.println(url);
    Request request = new Request.Builder()
        .url(url)
        .build();
    Call call = new OkHttpClient().newCall(request);
    Client client = new Client();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      client = new ObjectMapper().readValue(string, new TypeReference<Client>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return client;
  }

  public Client getClient(String id) {
    Request request = new Request.Builder()
        .url(URL + "/" + id + "?accountId="+ PublicCache.getAccountId())
        .build();
    Call call = new OkHttpClient().newCall(request);
    Client client = new Client();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      client = new ObjectMapper().readValue(string, new TypeReference<Client>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return client;
  }

  public ClientResponse getClientData(String id) {
    Request request = new Request.Builder()
        .url(URL + "/sp/" + id + "?accountId="+ PublicCache.getAccountId())
        .build();
    Call call = new OkHttpClient().newCall(request);
    ClientResponse client = new ClientResponse();
    try {
      ResponseBody body = call.execute().body();
      String string = body.string();
      client = new ObjectMapper().readValue(string, new TypeReference<ClientResponse>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return client;
  }

  public Client addClient(Client client) {
    try {
      client.setAccountId(PublicCache.getAccountId());
      String jsonString = new ObjectMapper().writeValueAsString(client);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .post(reqbody)
          .build();
      Call call = new OkHttpClient().newCall(request);
      Response response = call.execute();
      ResponseBody body = response.body();
      String string = body.string();
      return new ObjectMapper().readValue(string, new TypeReference<Client>() {});

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean deleteClient(Client client) {
    try {
      String jsonString = new ObjectMapper().writeValueAsString(client);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .delete(reqbody)
          .build();
      Call call = new OkHttpClient().newCall(request);
      Response response = call.execute();
      ResponseBody body = response.body();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

}
