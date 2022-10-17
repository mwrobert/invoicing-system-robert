package pl.futurecollars.invoice.db.nosql;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.Document;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Invoice;

public class MongoBasedDatabase implements Database {

  private final MongoCollection<Invoice> invoices;
  private final MongoIdProvider mongoIdProvider;

  public MongoBasedDatabase(MongoCollection<Invoice> invoices, MongoIdProvider mongoIdProvider) {
    this.invoices = invoices;
    this.mongoIdProvider = mongoIdProvider;
  }

  @Override
  public long save(Invoice invoice) {
    invoice.setId(mongoIdProvider.getNextIdAndIncrement());
    invoices.insertOne(invoice);
    return invoice.getId();
  }

  @Override
  public Optional<Invoice> findById(long id) {
    return Optional.ofNullable(invoices.find(getIdFilterDoc(id)).first());
  }

  @Override
  public Optional<Invoice> update(long id, Invoice updatedInvoice) {
    updatedInvoice.setId(id);
    return Optional.ofNullable(invoices.findOneAndReplace(getIdFilterDoc(id), updatedInvoice));
  }

  @Override
  public Optional<Invoice> delete(long id) {
    return Optional.ofNullable(invoices.findOneAndDelete(getIdFilterDoc(id)));
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport
        .stream(invoices.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  private Document getIdFilterDoc(long id) {
    return new Document("_id", id);
  }

}
