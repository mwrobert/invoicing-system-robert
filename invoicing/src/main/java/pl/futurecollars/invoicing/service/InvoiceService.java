package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InvoiceService {

  private final Database database;

  public InvoiceService(Database database) {
    this.database = database;
  }

  public long save(Invoice invoice) {
    return database.save(invoice);
  }

  public Optional<Invoice> getById(long id) {
    return database.get(id);
  }

  public void update(long id, Invoice invoice) {
    database.update(id, invoice);
  }

  public void delete(long id) {
    database.delete(id);
  }

  public List<Invoice> getAll() {
    return database.getAll();
  }

}
