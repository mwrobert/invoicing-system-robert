package pl.futurecollars.invoice.model;

import io.swagger.annotations.ApiModelProperty;
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

}
