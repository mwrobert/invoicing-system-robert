package pl.futurecollars.invoice.service.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.model.Invoice;
import pl.futurecollars.invoice.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

  private final Database<Invoice> database;

  public TaxCalculatorResult getTaxCalculatorResult(Company company) {

    String taxIdentificationNumber = company.getTaxIdentificationNumber();

    BigDecimal income = calculateIncome(taxIdentificationNumber);
    BigDecimal costs = calculateCosts(taxIdentificationNumber);
    BigDecimal pensionInsurance = company.getPensionInsurance();
    BigDecimal totalHealthInsurance = company.getHealthInsurance();

    BigDecimal incomingVat = calculateIncomingVat(taxIdentificationNumber);
    BigDecimal outgoingVat = calculateOutgoingVat(taxIdentificationNumber);

    BigDecimal incomeMinusCosts = income.subtract(costs);
    BigDecimal vatToReturn = incomingVat.subtract(outgoingVat);
    BigDecimal incomeMinusCostsAndPensionInsurance = incomeMinusCosts.subtract(pensionInsurance);
    BigDecimal taxCalculationBase = incomeMinusCostsAndPensionInsurance.setScale(0, RoundingMode.UP);
    BigDecimal incomeTax = taxCalculationBase.multiply(BigDecimal.valueOf(0.19));
    BigDecimal healthInsurance = totalHealthInsurance.multiply(BigDecimal.valueOf(0.09));
    BigDecimal healthInsuranceDeductible = totalHealthInsurance.multiply(BigDecimal.valueOf(0.0775));
    BigDecimal incomeTaxMinusHealthInsurance = incomeTax.subtract(healthInsuranceDeductible);
    BigDecimal finalIncomeTax = incomeTaxMinusHealthInsurance.setScale(0, RoundingMode.HALF_EVEN);

    return TaxCalculatorResult.builder()
        .income(income)
        .costs(costs)
        .incomeMinusCosts(incomeMinusCosts)
        .pensionInsurance(pensionInsurance)
        .incomeMinusCostsAndPensionInsurance(incomeMinusCostsAndPensionInsurance)
        .taxCalculationBase(taxCalculationBase)
        .incomeTax(incomeTax)
        .healthInsurance(healthInsurance)
        .healthInsuranceDeductible(healthInsuranceDeductible)
        .incomeTaxMinusHealthInsurance(incomeTaxMinusHealthInsurance)
        .finalIncomeTax(finalIncomeTax)
        .incomingVat(incomingVat)
        .outgoingVat(outgoingVat)
        .vatToReturn(vatToReturn)
        .build();
  }

  private BigDecimal calculateIncome(String taxIdentificationNumber) {
    return getTaxRelatedData(sellerPredicate(taxIdentificationNumber), this::getTotalPrice);
  }

  private BigDecimal getTotalPrice(InvoiceEntry entry) {
    return entry.getNetPrice().multiply(entry.getQuantity());
  }

  private BigDecimal calculateCosts(String taxIdentificationNumber) {
    return getTaxRelatedData(buyerPredicate(taxIdentificationNumber), calculateTotalCosts());
  }

  private Function<InvoiceEntry, BigDecimal> calculateTotalCosts() {
    return entry -> {
      if (entry.getExpenseRelatedToCar() != null && entry.getExpenseRelatedToCar().isPersonalUse()) {
        return calculateTotalNetPrice(entry);
      }
      return getTotalPrice(entry);
    };
  }

  private BigDecimal calculateTotalNetPrice(InvoiceEntry entry) {
    return (entry.getNetPrice().add(entry.getVatValue().divide(BigDecimal.valueOf(2), 2, RoundingMode.CEILING)))
        .multiply(entry.getQuantity());
  }

  private BigDecimal calculateIncomingVat(String taxIdentificationNumber) {
    return getTaxRelatedData(sellerPredicate(taxIdentificationNumber), this::getTotalVat);
  }

  private BigDecimal getTotalVat(InvoiceEntry entry) {
    return entry.getVatValue().multiply(entry.getQuantity());
  }

  private BigDecimal calculateOutgoingVat(String taxIdentificationNumber) {
    return getTaxRelatedData(buyerPredicate(taxIdentificationNumber), vatEntryToAmount());
  }

  private Function<InvoiceEntry, BigDecimal> vatEntryToAmount() {
    return entry -> {
      if (entry.getExpenseRelatedToCar() != null && entry.getExpenseRelatedToCar().isPersonalUse()) {
        return calculateTotalVatValue(entry);
      }
      return getTotalVat(entry);
    };
  }

  private BigDecimal calculateTotalVatValue(InvoiceEntry entry) {
    return (entry.getVatValue().divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR))
        .multiply(entry.getQuantity());
  }

  private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxIdentificationNumber);
  }

  private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
    return invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxIdentificationNumber);
  }

  private BigDecimal getTaxRelatedData(Predicate<Invoice> invoicePredicate, Function<InvoiceEntry, BigDecimal> invoiceEntryToAmount) {
    return database.getAll()
        .stream()
        .filter(invoicePredicate)
        .flatMap(invoice -> invoice.getInvoiceEntries().stream())
        .map(invoiceEntryToAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}
