package pl.futurecollars.invoicing.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class Invoice {

  @Setter
  private long id;
  private LocalDate date;
  private Company fromCompany;
  private Company toCompany;
  private List<InvoiceEntry> invoiceEntries;

}
