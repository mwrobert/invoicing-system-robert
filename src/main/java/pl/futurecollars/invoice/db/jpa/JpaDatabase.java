package pl.futurecollars.invoice.db.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Invoice;

public class JpaDatabase implements Database {

  private final InvoiceRepository invoiceRepository;

  public JpaDatabase(InvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  @Override
  public long save(Invoice invoice) {
    return invoiceRepository.save(invoice).getId();
  }

  @Override
  public Optional<Invoice> findById(long id) {
    return invoiceRepository.findById(id);
  }

  @Override
  public Optional<Invoice> update(long id, Invoice updatedInvoice) {
    Optional<Invoice> invoiceOptional = findById(id);
    if (invoiceOptional.isPresent()) {
      Invoice invoice = invoiceOptional.get();
      updatedInvoice.setId(id);
      updatedInvoice.getBuyer().setId(invoice.getBuyer().getId());
      updatedInvoice.getSeller().setId(invoice.getSeller().getId());

      return Optional.of(invoiceRepository.save(updatedInvoice));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Invoice> delete(long id) {
    Optional<Invoice> invoiceOptional = invoiceRepository.findById(id);
    invoiceOptional.ifPresent(invoiceRepository::delete);
    return invoiceOptional;
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport
        .stream(invoiceRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

}
