package pl.futurecollars.invoice.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoice.model.Invoice;
import pl.futurecollars.invoice.service.InvoiceService;

@RestController
public class InvoiceController implements InvoiceApi {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @Override
  public ResponseEntity<Invoice> getInvoice(@PathVariable long id) {
    return ResponseEntity.of(invoiceService.findForId(id));
  }

  @Override
  public List<Invoice> getAll() {
    return invoiceService.getAll();
  }

  @Override
  public long saveInvoice(@RequestBody Invoice invoice) {
    return invoiceService.saveInvoice(invoice);
  }

  @Override
  public ResponseEntity<?> deleteInvoice(@PathVariable long id) {
    if (this.invoiceService.deleteInvoice(id)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<?> updateInvoice(@RequestBody Invoice invoice, @PathVariable long id) {
    if (this.invoiceService.updateInvoice(id, invoice)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
