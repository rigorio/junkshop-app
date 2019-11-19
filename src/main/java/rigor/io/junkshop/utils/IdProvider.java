package rigor.io.junkshop.utils;

import rigor.io.junkshop.api.gown.GownHandler;
import rigor.io.junkshop.api.gown.entities.Gown;
import rigor.io.junkshop.api.gown.repository.GownCsvRepository;

import java.util.List;
import java.util.stream.Collectors;

public class IdProvider {

  private static Long id = 0L;
  private static GownHandler gownService = new GownHandler(new GownCsvRepository());

  public IdProvider() {

  }

  public static Long getId() {
    List<Gown> all = gownService.all();
    if (all.isEmpty())
      return ++id;
    else {
      List<Gown> sortedById = all.stream()
          .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
          .collect(Collectors.toList());
      id = sortedById.get(0).getId();
      id++;
    }
    return id;
  }
}
