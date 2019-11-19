package rigor.io.junkshop.utils;

import java.util.List;
import java.util.Optional;

public interface StandardRepository<T> {
  List<T> all();

  Optional<T> findById(Long id);

  void delete(Long id);

  void delete(T t);

  boolean add(T t);
  
  boolean update(T t);

  boolean addall(List<T> ts);

  List<T> setList(List<T> ts);
}
