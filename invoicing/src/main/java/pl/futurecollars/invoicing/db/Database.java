package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.model.Invoice;

public interface Database {

  long save(Invoice invoice);

  Optional<Invoice> get(long id);

  Optional<Invoice> update(long id, Invoice updatedInvoice);

  void delete(long id);

  List<Invoice> getAll();

}
