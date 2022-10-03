package pl.futurecollars.invoice.db;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import pl.futurecollars.invoice.model.Invoice;
import pl.futurecollars.invoice.model.InvoiceEntry;

public interface Database {

  long save(Invoice invoice);

  Optional<Invoice> findById(long id);

  void update(long id, Invoice updatedInvoice);

  void delete(long id);

  List<Invoice> getAll();

  default BigDecimal visit(Predicate<Invoice> invoicePredicate, Function<InvoiceEntry, BigDecimal> invoiceEntryToAmount) {
    return getAll()
        .stream()
        .filter(invoicePredicate)
        .flatMap(invoice -> invoice.getInvoiceEntries().stream())
        .map(invoiceEntryToAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}
