package pl.futurecollars.invoice.controller.tax;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoice.service.TaxCalculatorResult;

@RequestMapping("tax")
@Api(tags = {"tax-controller"})
public interface TaxCalculatorApi {

  @ApiOperation(value = "Get incomes, costs, vat and taxes to pay")
  @GetMapping(value = "/{taxIdentificationNumber}", produces = {"application/json;charset=UTF-8"})
  TaxCalculatorResult getCalculatedTaxes(@PathVariable @ApiParam(example = "12345678") String taxIdentificationNumber);

}
