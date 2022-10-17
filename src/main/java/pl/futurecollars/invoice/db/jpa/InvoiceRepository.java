package pl.futurecollars.invoice.db.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoice.model.Invoice;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
}
