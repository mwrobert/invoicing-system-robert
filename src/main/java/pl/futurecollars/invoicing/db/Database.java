package pl.futurecollars.invoicing.db;

import java.util.List;
import pl.futurecollars.invoicing.model.Invoice;

public interface Database {

  long save(Invoice invoice);

  Invoice findById(long id);

  void update(long id, Invoice updatedInvoice);

  void delete(long id);

  List<Invoice> getAll();

}
