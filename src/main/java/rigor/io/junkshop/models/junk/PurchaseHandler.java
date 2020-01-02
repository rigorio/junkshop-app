package rigor.io.junkshop.models.junk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import rigor.io.junkshop.config.Configurations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PurchaseHandler {
  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
  private String URL = Configurations.getInstance().getHost() + "/junk";

  public List<Junk> getJunk() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(URL)
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

  public List<PurchaseSummaryFX> getMonthlyPurchaseSummary() {
    List<Junk> junkList = getJunk();
    List<PurchaseSummaryFX> fxList = new ArrayList<>();
    for (Junk junk : junkList) {
      LocalDate date = LocalDate.parse(junk.getDate());
      String span = date.getMonth() + " " + date.getYear();
      Optional<PurchaseSummaryFX> anyPurchase = fxList.stream()
          .filter(fx -> fx.getSpan().get().equals(span))
          .findAny();
      PurchaseSummaryFX summary = new PurchaseSummaryFX();
      if (anyPurchase.isPresent()) {
        summary = anyPurchase.get();
        Double amount = Double.valueOf(summary.getAmount().get());
        summary.setAmount(new SimpleStringProperty("" + (amount + Double.valueOf(junk.getTotalPrice()))));
      } else {
        summary.setSpan(new SimpleStringProperty(span));
        summary.setAmount(new SimpleStringProperty(junk.getTotalPrice()));
        fxList.add(summary);
      }
    }
    return fxList;
  }

  public void sendJunk(Junk junk) {
    OkHttpClient client = new OkHttpClient();
    try {
      junk.setDate(LocalDate.now().toString());
      String jsonString = new ObjectMapper().writeValueAsString(junk);
      RequestBody reqbody = RequestBody.create(JSON, jsonString);
      Request request = new Request.Builder()
          .url(URL)
          .post(reqbody)
          .build();
      Call call = client.newCall(request);
      Junk junks = new Junk();
      ResponseBody body = call.execute().body();
      String string = body.string();
      junks = new ObjectMapper().readValue(string, new TypeReference<Junk>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
