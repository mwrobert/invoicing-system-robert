package pl.futurecollars.invoice.db;

import java.io.IOException;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoice.db.file.FileRepository;
import pl.futurecollars.invoice.db.jpa.InvoiceRepository;
import pl.futurecollars.invoice.db.jpa.JpaDatabase;
import pl.futurecollars.invoice.db.memory.MemoryRepository;
import pl.futurecollars.invoice.db.sql.SqlDatabase;
import pl.futurecollars.invoice.utils.FilesService;
import pl.futurecollars.invoice.utils.IdService;
import pl.futurecollars.invoice.utils.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "jpa")
  public Database jpaDatabase(InvoiceRepository invoiceRepository) {
    log.info("Running on jpa database");
    return new JpaDatabase(invoiceRepository);
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "sql")
  public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
    log.info("Running on sql database");
    return new SqlDatabase(jdbcTemplate);
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "in-file")
  public FilesService filesService() {
    return new FilesService();
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "in-file")
  public JsonService jsonService() {
    return new JsonService();
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "in-file")
  public IdService idService(
      FilesService filesService,
      @Value("${database.idPath}") String idPathString) throws IOException {

    Path idPath = filesService.createFile(idPathString);
    return new IdService(idPath, filesService);
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "in-file")
  public Database fileRepository(
      FilesService filesService,
      JsonService jsonService,
      IdService idService,
      @Value("${database.path}") String dbPath) throws IOException {

    log.info("Running on file repository");
    log.debug(dbPath);

    Path databasePath = filesService.createFile(dbPath);
    return new FileRepository(databasePath, filesService, jsonService, idService);
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "in-memory")
  public Database memoryRepository() {

    log.info("Running on memory repository");

    return new MemoryRepository();
  }

}
