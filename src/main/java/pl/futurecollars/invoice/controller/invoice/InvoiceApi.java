package pl.futurecollars.invoice.controller.invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.futurecollars.invoice.model.Invoice;

@Api(tags = {"invoice-controller"})
public interface InvoiceApi {

  @ApiOperation(value = "Get invoice with given id")
  @GetMapping(value = "/invoices/{id}", produces = {"application/json;charset=UTF-8"})
  ResponseEntity<Invoice> getInvoice(@PathVariable long id);

  @ApiOperation(value = "Get list of all invoices")
  @GetMapping(value = "/invoices", produces = {"application/json;charset=UTF-8"})
  List<Invoice> getAll();

  @ApiOperation(value = "Add new invoice to system")
  @PostMapping("/invoices")
  long saveInvoice(@RequestBody Invoice invoice);

  @ApiOperation(value = "Delete invoice with given id")
  @DeleteMapping("/invoices/{id}")
  ResponseEntity<?> deleteInvoice(@PathVariable long id);

  @ApiOperation(value = "Update invoice with given id")
  @PutMapping("/invoices/{id}")
  ResponseEntity<?> updateInvoice(@RequestBody Invoice invoice, @PathVariable long id);
}
