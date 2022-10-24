package pl.futurecollars.invoice.db.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.WithId;

@AllArgsConstructor
public class JpaDatabase<T extends WithId> implements Database<T> {

  private final CrudRepository<T, Long> repository;

  @Override
  public long save(T item) {
    return repository.save(item).getId();
  }

  @Override
  public Optional<T> findById(long id) {
    return repository.findById(id);
  }

  @Override
  public Optional<T> update(long id, T updatedItem) {
    Optional<T> itemOptional = repository.findById(id);
    if (itemOptional.isPresent()) {
      updatedItem.setId(id);
      repository.save(updatedItem);
    }
    return itemOptional;
  }

  @Override
  public Optional<T> delete(long id) {
    Optional<T> itemOptional = repository.findById(id);
    itemOptional.ifPresent(repository::delete);
    return itemOptional;
  }

  @Override
  public List<T> getAll() {
    return StreamSupport
        .stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

}
