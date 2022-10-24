package pl.futurecollars.invoice.controller.tax;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.service.tax.TaxCalculatorResult;

@RequestMapping("tax")
@Api(tags = {"tax-controller"})
public interface TaxCalculatorApi {

  @ApiOperation(value = "Get company incomes, costs, vat and taxes to pay")
  @PostMapping
  TaxCalculatorResult getCalculatedTaxes(@RequestBody Company company);

}
