package pl.futurecollars.invoice.service.invoice;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Invoice;

@Service
public class InvoiceService {

  private final Database<Invoice> database;

  public InvoiceService(Database<Invoice> database) {
    this.database = database;
  }

  public long saveInvoice(Invoice invoice) {
    return database.save(invoice);
  }

  public Optional<Invoice> findInvoiceById(long id) {
    return database.findById(id);
  }

  public boolean updateInvoice(long id, Invoice invoice) {
    var invoiceToUpdate = database.findById(id);
    if (invoiceToUpdate.isPresent()) {
      database.update(id, invoice);
    }
    return invoiceToUpdate.isPresent();
  }

  public boolean deleteInvoice(long id) {
    var invoiceToDelete = database.findById(id);
    if (invoiceToDelete.isPresent()) {
      database.delete(id);
    }
    return invoiceToDelete.isPresent();
  }

  public List<Invoice> getAllInvoices() {
    return database.getAll();
  }

}
