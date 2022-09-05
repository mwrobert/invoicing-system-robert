package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.IdService;
import pl.futurecollars.invoicing.utils.JsonService;

public class FileBasedDatabase implements Database {

  private final FilesService filesService;
  private final JsonService jsonService;
  private final IdService idService;
  private final Path databasePath;

  public FileBasedDatabase(Path databasePath, FilesService filesService, JsonService jsonService, IdService idService) {
    this.filesService = filesService;
    this.jsonService = jsonService;
    this.idService = idService;
    this.databasePath = databasePath;
    initialize();
  }

  private void initialize() {
    if (!Files.exists(databasePath)) {
      try {
        filesService.createFile(databasePath.toString());
      } catch (IOException e) {
        throw new RuntimeException("Unable to initialize database path", e);
      }
    }
  }

  @Override
  public long save(Invoice invoice) {
    long currentId = idService.getCurrentIdAndIncrement();
    invoice.setId(currentId);
    try {
      filesService.appendLineToFile(databasePath, jsonService.toJson(invoice));
    } catch (IOException e) {
      throw new RuntimeException("Problem save invoice to database repository", e);
    }
    return currentId;
  }

  @Override
  public Optional<Invoice> get(long id) {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .filter(line -> containsId(line, id))
          .map(line -> jsonService.toObject(line, Invoice.class))
          .findFirst();
    } catch (IOException ex) {
      throw new RuntimeException("Database failed to getById invoice with id: " + id, ex);
    }
  }

  @Override
  public Optional<Invoice> update(long id, Invoice updatedInvoice) {
    try {
      List<String> allInvoices = filesService.readAllLines(databasePath);
      final String invoiceAsJson = allInvoices.stream().filter(line -> containsId(line, id)).findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Id " + id + " does not exist"));

      allInvoices.remove(invoiceAsJson);
      final Invoice invoice = jsonService.toObject(invoiceAsJson, Invoice.class);

      invoice.setDate(updatedInvoice.getDate());
      invoice.setFromCompany(updatedInvoice.getFromCompany());
      invoice.setToCompany(updatedInvoice.getToCompany());
      invoice.setInvoiceEntries(updatedInvoice.getInvoiceEntries());

      allInvoices.add(jsonService.toJson(invoice));
      filesService.writeLinesToFile(databasePath, allInvoices);
      return Optional.of(invoice);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to update invoice with id: " + id);
    }
  }

  @Override
  public void delete(long id) {
    try {
      var invoicesExceptDeleted = filesService.readAllLines(databasePath)
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());

      filesService.writeLinesToFile(databasePath, invoicesExceptDeleted);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to delete invoice with id: " + id, ex);
    }
  }

  private boolean containsId(String line, long id) {
    return line.contains("\"id\":" + id + ",");
  }

  @Override
  public List<Invoice> getAll() {
    try {
      return filesService.readAllLines(databasePath).stream()
          .map(line -> jsonService.toObject(line, Invoice.class))
          .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new RuntimeException("Failed to load all invoices", ex);
    }
  }

}
