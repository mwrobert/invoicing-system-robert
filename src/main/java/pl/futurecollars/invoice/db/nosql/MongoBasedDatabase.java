package pl.futurecollars.invoice.db.nosql;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.Document;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.WithId;

public class MongoBasedDatabase<T extends WithId> implements Database<T> {

  private final MongoCollection<T> items;
  private final MongoIdProvider mongoIdProvider;

  public MongoBasedDatabase(MongoCollection<T> items, MongoIdProvider mongoIdProvider) {
    this.items = items;
    this.mongoIdProvider = mongoIdProvider;
  }

  @Override
  public long save(T item) {
    item.setId(mongoIdProvider.getNextIdAndIncrement());
    items.insertOne(item);
    return item.getId();
  }

  @Override
  public Optional<T> findById(long id) {
    return Optional.ofNullable(items.find(getIdFilterDoc(id)).first());
  }

  @Override
  public Optional<T> update(long id, T updatedItem) {
    updatedItem.setId(id);
    return Optional.ofNullable(items.findOneAndReplace(getIdFilterDoc(id), updatedItem));
  }

  @Override
  public Optional<T> delete(long id) {
    return Optional.ofNullable(items.findOneAndDelete(getIdFilterDoc(id)));
  }

  @Override
  public List<T> getAll() {
    return StreamSupport
        .stream(items.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  private Document getIdFilterDoc(long id) {
    return new Document("_id", id);
  }

}
