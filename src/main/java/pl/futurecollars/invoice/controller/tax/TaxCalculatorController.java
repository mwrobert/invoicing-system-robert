package pl.futurecollars.invoice.controller.tax;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoice.service.TaxCalculatorResult;
import pl.futurecollars.invoice.service.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

  private final TaxCalculatorService taxService;

  @Override
  public TaxCalculatorResult getCalculatedTaxes(String taxIdentificationNumber) {
    return taxService.getTaxCalculatorResult(taxIdentificationNumber);
  }

}
