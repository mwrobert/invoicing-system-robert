package pl.futurecollars.invoice.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

  @ApiModelProperty(value = "Company name", required = true, example = "iCode Trust Sp. z o.o")
  private String name;

  @ApiModelProperty(value = "Company tax identification number", required = true, example = "5212205778")
  private String taxIdentificationNumber;

  @ApiModelProperty(value = "Company address", required = true, example = "ul. Nowa 24d/5 02-703 Warszawa, Polska")
  private String address;

  @ApiModelProperty(value = "Health insurance cost", required = true, example = "500")
  private BigDecimal healthInsurance = BigDecimal.ZERO;

  @ApiModelProperty(value = "Pension insurance cost", required = true, example = "1400")
  private BigDecimal pensionInsurance = BigDecimal.ZERO;

}
