package pl.futurecollars.invoice.service;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxCalculatorResult {

  @ApiModelProperty(value = "Income (calculated by application)", required = true, example = "100000")
  private BigDecimal income;

  @ApiModelProperty(value = "Costs (calculated by application)", required = true, example = "10000")
  private BigDecimal costs;

  @ApiModelProperty(value = "Earnings (calculated by application)", required = true, example = "90000")
  private BigDecimal earnings;

  @ApiModelProperty(value = "Collected VAT (calculated by application)", required = true, example = "23000")
  private BigDecimal incomingVat;

  @ApiModelProperty(value = "Payed VAT (calculated by application)", required = true, example = "2300")
  private BigDecimal outgoingVat;

  @ApiModelProperty(value = "Outstanding VAT (calculated by application)", required = true, example = "20700")
  private BigDecimal vatToReturn;

}
