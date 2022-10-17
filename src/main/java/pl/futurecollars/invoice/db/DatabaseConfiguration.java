package pl.futurecollars.invoice.db;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoice.db.file.FileRepository;
import pl.futurecollars.invoice.db.jpa.InvoiceRepository;
import pl.futurecollars.invoice.db.jpa.JpaDatabase;
import pl.futurecollars.invoice.db.memory.MemoryRepository;
import pl.futurecollars.invoice.db.nosql.MongoBasedDatabase;
import pl.futurecollars.invoice.db.nosql.MongoIdProvider;
import pl.futurecollars.invoice.db.sql.SqlDatabase;
import pl.futurecollars.invoice.model.Invoice;
import pl.futurecollars.invoice.utils.FilesService;
import pl.futurecollars.invoice.utils.IdService;
import pl.futurecollars.invoice.utils.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "mongo")
  public MongoDatabase mongoDatabase(
      @Value("${database.name}") String databaseName) {
    CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    MongoClientSettings settings = MongoClientSettings.builder()
        .codecRegistry(codecRegistry)
        .build();

    MongoClient mongoClient = MongoClients.create(settings);

    return mongoClient.getDatabase(databaseName);
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "mongo")
  public MongoIdProvider mongoIdProvider(
      @Value("${database.counter.collection}") String collectionName, MongoDatabase mongoDatabase) {
    return new MongoIdProvider(mongoDatabase.getCollection(collectionName, Document.class));
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "mongo")
  public Database mongoBasedDatabase(@Value("${database.collection}") String collectionName,
                                     MongoDatabase mongoDatabase,
                                     MongoIdProvider mongoIdProvider) {
    log.info("Running on nosql database");
    return new MongoBasedDatabase(mongoDatabase.getCollection(collectionName, Invoice.class), mongoIdProvider);
  }

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
  @ConditionalOnProperty(value = "database.type", havingValue = "infile")
  public FilesService filesService() {
    return new FilesService();
  }

  @Bean
  @ConditionalOnExpression("'${database.type}'.equals('infile') or '${database.type}'.equals('mongo')")
  public JsonService jsonService() {
    return new JsonService();
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "infile")
  public IdService idService(
      FilesService filesService,
      @Value("${database.idPath}") String idPathString) throws IOException {

    Path idPath = filesService.createFile(idPathString);
    return new IdService(idPath, filesService);
  }

  @Bean
  @ConditionalOnProperty(value = "database.type", havingValue = "infile")
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
