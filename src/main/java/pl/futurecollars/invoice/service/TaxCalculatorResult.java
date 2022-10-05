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

  @ApiModelProperty(value = "Company income (calculated by application)", required = true, example = "76011,62")
  private BigDecimal income;

  @ApiModelProperty(value = "Company costs (calculated by application)", required = true, example = "11329,47")
  private BigDecimal costs;

  @ApiModelProperty(value = "Company income minus costs (calculated by application)", required = true, example = "64682,15")
  private BigDecimal incomeMinusCosts;

  @ApiModelProperty(value = "Company pension insurance (calculated by application)", required = true, example = "514,57")
  private BigDecimal pensionInsurance;

  @ApiModelProperty(value = "Company income minus costs and pension insurance (calculated by application)", required = true, example = "64167,58")
  private BigDecimal incomeMinusCostsAndPensionInsurance;

  @ApiModelProperty(value = "Company tax calculation base (calculated by application)", required = true, example = "64168,00")
  private BigDecimal taxCalculationBase;

  @ApiModelProperty(value = "Company 19% income tax(calculated by application)", required = true, example = "12191,92")
  private BigDecimal incomeTax;

  @ApiModelProperty(value = "9% of company health insurance (calculated by application)", required = true, example = "319,94")
  private BigDecimal healthInsurance;

  @ApiModelProperty(value = "Deducible 7,75% of company health insurance (calculated by application)", required = true, example = "275,50")
  private BigDecimal healthInsuranceDeductible;

  @ApiModelProperty(value = "Company income tax minus deductible health insurance (calculated by application)", required = true, example = "11916,42")
  private BigDecimal incomeTaxMinusHealthInsurance;

  @ApiModelProperty(value = "Company final income tax(calculated by application)", required = true, example = "11916,00 ")
  private BigDecimal finalIncomeTax;

  @ApiModelProperty(value = "Collected VAT (calculated by application)", required = true, example = "23000")
  private BigDecimal incomingVat;

  @ApiModelProperty(value = "Payed VAT (calculated by application)", required = true, example = "2300")
  private BigDecimal outgoingVat;

  @ApiModelProperty(value = "Outstanding VAT (calculated by application)", required = true, example = "20700")
  private BigDecimal vatToReturn;

}
