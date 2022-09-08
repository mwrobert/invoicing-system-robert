package pl.futurecollars.invoice.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

  @Setter
  private long id;
  private LocalDate date;
  private Company fromCompany;
  private Company toCompany;
  private List<InvoiceEntry> invoiceEntries;

  public Invoice(LocalDate date, Company fromCompany, Company toCompany, List<InvoiceEntry> invoiceEntries) {
    this.date = date;
    this.fromCompany = fromCompany;
    this.toCompany = toCompany;
    this.invoiceEntries = invoiceEntries;
  }

}
