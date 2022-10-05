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

  @ApiModelProperty(value = "Product/service quantity", required = true, example = "2")
  private BigDecimal quantity;

  @ApiModelProperty(value = "Product/service net price", required = true, example = "1000")
  private BigDecimal netPrice;

  @ApiModelProperty(value = "Product/service tax value", required = true, example = "230")
  private BigDecimal vatValue;

  @ApiModelProperty(value = "Tax rate", required = true)
  private Vat vatRate;

  @ApiModelProperty(value = "Car expense this invoice entry is related to, empty in other case")
  private Car expenseRelatedToCar;

}
