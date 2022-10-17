package pl.futurecollars.invoice.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Invoice;

public class MemoryRepository implements Database {

  private long index = 1;
  private final Map<Long, Invoice> invoices = new HashMap<>();

  @Override
  public long save(Invoice invoice) {
    long newId = index++;
    invoice.setId(newId);
    invoices.put(newId, invoice);
    return newId;
  }

  @Override
  public Optional<Invoice> findById(long id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public Optional<Invoice> update(long id, Invoice invoice) {
    Optional<Invoice> saved = findById(id);
    if (saved.isEmpty()) {
      throw new IllegalArgumentException("Id " + id + " does not exist");
    }
    invoice.setId(id);
    invoices.put(id, invoice);
    return Optional.of(invoice);
  }

  @Override
  public Optional<Invoice> delete(long id) {
    Optional<Invoice> invoiceOptional = findById(id);
    invoices.remove(id);
    return invoiceOptional;
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices.values());
  }

}
