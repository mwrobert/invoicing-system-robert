package pl.futurecollars.invoicing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;
import pl.futurecollars.invoicing.service.InvoiceService;

public class App {

  public static void main(String[] args) {
    Database database = new InMemoryDatabase();
    InvoiceService invoiceService = new InvoiceService(database);

    Company companyBuyer = new Company("Drutex", "NIP 12345", "Warszawa ul.Nowa 2");
    Company companySeller = new Company("Polmos", "NIP 42345", "Warszawa ul.Miła 42");

    InvoiceEntry invoiceEntryFirst = new InvoiceEntry("Computer", BigDecimal.valueOf(1000), BigDecimal.valueOf(230), Vat.VAT_23);
    InvoiceEntry invoiceEntrySecond = new InvoiceEntry("Fax", BigDecimal.valueOf(500), BigDecimal.valueOf(40), Vat.VAT_8);
    List<InvoiceEntry> invoiceEntries = List.of(invoiceEntryFirst, invoiceEntrySecond);
    Invoice invoice = Invoice.builder()
        .date(LocalDate.now())
        .fromCompany(companyBuyer)
        .toCompany(companySeller)
        .invoiceEntries(invoiceEntries)
        .build();

    Invoice secondInvoice = Invoice.builder()
        .date(LocalDate.now())
        .fromCompany(companySeller)
        .toCompany(companyBuyer)
        .invoiceEntries(invoiceEntries)
        .build();

    long id = invoiceService.saveInvoice(invoice);
    System.out.println(invoiceService.findForId(id));
    invoiceService.updateInvoice(id, secondInvoice);
    System.out.println(invoiceService.findForId(id));
    invoiceService.deleteInvoice(id);
    System.out.println(invoiceService.findForId(id));
  }

}
