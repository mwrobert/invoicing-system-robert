package pl.futurecollars.invoice.controller;

import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoice.model.Invoice;
import pl.futurecollars.invoice.service.InvoiceService;

@Api(tags = {"invoice-controller"})
@RestController
public class InvoiceController {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @GetMapping(value = "/invoices/{id}", produces = {"application/json;charset=UTF-8"})
  public ResponseEntity<Invoice> getInvoice(@PathVariable long id) {
    return ResponseEntity.of(invoiceService.findForId(id));
  }

  @GetMapping(value = "/invoices", produces = {"application/json;charset=UTF-8"})
  public List<Invoice> getAll() {
    return invoiceService.getAll();
  }

  @PostMapping("/invoices")
  public long saveInvoice(@RequestBody Invoice invoice) {
    return invoiceService.saveInvoice(invoice);
  }

  @DeleteMapping("/invoices/{id}")
  public ResponseEntity<?> deleteInvoice(@PathVariable long id) {
    if (this.invoiceService.deleteInvoice(id)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/invoices/{id}")
  public ResponseEntity<?> updateInvoice(@RequestBody Invoice invoice, @PathVariable long id) {
    if (this.invoiceService.updateInvoice(id, invoice)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
