package pl.futurecollars.invoice.db;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoice.model.WithId;

public interface Database<T extends WithId> {

  long save(T item);

  Optional<T> findById(long id);

  List<T> getAll();

  Optional<T> update(long id, T updatedItem);

  Optional<T> delete(long id);

}
