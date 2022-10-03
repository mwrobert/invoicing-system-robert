package pl.futurecollars.invoice.service;

import java.math.BigDecimal;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Invoice;
import pl.futurecollars.invoice.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

  private final Database database;

  public TaxCalculatorResult getTaxCalculatorResult(String taxIdentificationNumber) {

    BigDecimal income = calculateIncome(taxIdentificationNumber);
    BigDecimal costs = calculateCosts(taxIdentificationNumber);
    BigDecimal incomingVat = calculateIncomingVat(taxIdentificationNumber);
    BigDecimal outgoingVat = calculateOutgoingVat(taxIdentificationNumber);

    BigDecimal earnings = income.subtract(costs);
    BigDecimal vatToReturn = incomingVat.subtract(outgoingVat);

    return TaxCalculatorResult.builder()
        .income(income)
        .costs(costs)
        .earnings(earnings)
        .incomingVat(incomingVat)
        .outgoingVat(outgoingVat)
        .vatToReturn(vatToReturn)
        .build();
  }

  private BigDecimal calculateIncome(String taxIdentificationNumber) {
    return database.visit(sellerPredicate(taxIdentificationNumber), this::getTotalPrice);
  }

  private BigDecimal calculateCosts(String taxIdentificationNumber) {
    return database.visit(buyerPredicate(taxIdentificationNumber), this::getTotalPrice);
  }

  private BigDecimal getTotalPrice(InvoiceEntry entry) {
    return entry.getNetPrice().multiply(entry.getQuantity());
  }

  private BigDecimal calculateIncomingVat(String taxIdentificationNumber) {
    return database.visit(sellerPredicate(taxIdentificationNumber), this::getTotalVat);
  }

  private BigDecimal calculateOutgoingVat(String taxIdentificationNumber) {
    return database.visit(buyerPredicate(taxIdentificationNumber), this::getTotalVat);
  }

  private BigDecimal getTotalVat(InvoiceEntry entry) {
    return entry.getVatValue().multiply(entry.getQuantity());
  }

  private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxIdentificationNumber);
  }

  private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxIdentificationNumber);
  }

}
