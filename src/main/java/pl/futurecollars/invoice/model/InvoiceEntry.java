package pl.futurecollars.invoice.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceEntry {

  @ApiModelProperty(value = "Product/service description", required = true, example = "Programming course")
  private String description;

  @ApiModelProperty(value = "Product/service price", required = true, example = "1000")
  private BigDecimal price;

  @ApiModelProperty(value = "Product/service tax value", required = true, example = "230")
  private BigDecimal vatValue;

  @ApiModelProperty(value = "Tax rate", required = true)
  private Vat vatRate;

}
