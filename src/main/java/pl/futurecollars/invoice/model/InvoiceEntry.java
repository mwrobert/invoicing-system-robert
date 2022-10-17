package pl.futurecollars.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_entries")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceEntry {

  @Id
  @JsonIgnore
  @JoinColumn(name = "invoice_entry_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ApiModelProperty(value = "Product/service description", required = true, example = "Programming course")
  private String description;

  @ApiModelProperty(value = "Product/service quantity", required = true, example = "2")
  private BigDecimal quantity;

  @ApiModelProperty(value = "Product/service net price", required = true, example = "1000")
  private BigDecimal netPrice;

  @ApiModelProperty(value = "Product/service tax value", required = true, example = "230")
  private BigDecimal vatValue;

  @Enumerated(EnumType.STRING)
  @ApiModelProperty(value = "Tax rate", required = true)
  private Vat vatRate;

  @JoinColumn(name = "expense_related_to_car")
  @OneToOne(cascade = CascadeType.ALL)
  @ApiModelProperty(value = "Car expense this invoice entry is related to, empty in other case")
  private Car expenseRelatedToCar;

}
