package pl.futurecollars.invoice.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Invoice;

public class InMemoryDatabase implements Database {

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
  public void update(long id, Invoice invoice) {
    Optional<Invoice> saved = findById(id);
    if (saved.isEmpty()) {
      throw new IllegalArgumentException("Id " + id + " does not exist");
    }
    invoice.setId(id);
    invoices.put(id, invoice);
  }

  @Override
  public void delete(long id) {
    invoices.remove(id);
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices.values());
  }

}
