package pl.futurecollars.invoice.controller.tax;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.service.tax.TaxCalculatorResult;
import pl.futurecollars.invoice.service.tax.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

  private final TaxCalculatorService taxService;

  @Override
  public TaxCalculatorResult getCalculatedTaxes(Company company) {
    return taxService.getTaxCalculatorResult(company);
  }
}
