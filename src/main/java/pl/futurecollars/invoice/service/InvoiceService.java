package pl.futurecollars.invoice.service;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Invoice;

public class InvoiceService {

  private final Database database;

  public InvoiceService(Database database) {
    this.database = database;
  }

  public long saveInvoice(Invoice invoice) {
    return database.save(invoice);
  }

  public Optional<Invoice> findForId(long id) {
    return database.findById(id);
  }

  public boolean updateInvoice(long id, Invoice invoice) {
    if (database.findById(id).isPresent()) {
      database.update(id, invoice);
      return true;
    } else {
      return false;
    }
  }

  public boolean deleteInvoice(long id) {
    if (database.findById(id).isPresent()) {
      database.delete(id);
      return true;
    } else {
      return false;
    }
  }

  public List<Invoice> getAll() {
    return database.getAll();
  }

}
