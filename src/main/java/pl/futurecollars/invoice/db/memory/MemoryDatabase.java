package pl.futurecollars.invoice.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.WithId;

public class MemoryDatabase<T extends WithId> implements Database<T> {

  private long index = 1;
  private final Map<Long, T> items = new HashMap<>();

  @Override
  public long save(T item) {
    long newId = index++;
    item.setId(newId);
    items.put(newId, item);
    return newId;
  }

  @Override
  public Optional<T> findById(long id) {
    return Optional.ofNullable(items.get(id));
  }

  @Override
  public Optional<T> update(long id, T item) {
    Optional<T> saved = findById(id);
    if (saved.isEmpty()) {
      throw new IllegalArgumentException("Id " + id + " does not exist");
    }
    item.setId(id);
    items.put(id, item);
    return Optional.of(item);
  }

  @Override
  public Optional<T> delete(long id) {
    Optional<T> itemOptional = findById(id);
    items.remove(id);
    return itemOptional;
  }

  @Override
  public List<T> getAll() {
    return new ArrayList<>(items.values());
  }

}
