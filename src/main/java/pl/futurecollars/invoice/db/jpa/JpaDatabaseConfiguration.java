package pl.futurecollars.invoice.db.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "database.type", havingValue = "jpa")
public class JpaDatabaseConfiguration {

  @Bean
  public Database<Invoice> jpaInvoiceDatabase(InvoiceRepository repository) {
    log.info("Running on jpa database");
    return new JpaDatabase<>(repository);
  }

  @Bean
  public Database<Company> jpaCompanyDatabase(CompanyRepository repository) {
    return new JpaDatabase<>(repository);
  }

}
