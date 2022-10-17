package pl.futurecollars.invoice.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

  @ApiModelProperty(value = "Car registration number", required = true, example = "KRA1234L")
  private String registrationNumber;

  @ApiModelProperty(value = "Specifies if car is used also for personal reasons", required = true, example = "true")
  private boolean personalUse;

}
