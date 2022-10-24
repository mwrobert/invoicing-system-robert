package pl.futurecollars.invoice.db.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "database.type", havingValue = "memory")
public class MemoryDatabaseConfiguration {

  @Bean
  public Database<Invoice> invoiceMemoryDatabase() {
    log.info("Running on memory database");
    return new MemoryDatabase<>();
  }

  @Bean
  public Database<Company> companyMemoryDatabase() {
    log.info("Running on company database");
    return new MemoryDatabase<>();
  }

}
