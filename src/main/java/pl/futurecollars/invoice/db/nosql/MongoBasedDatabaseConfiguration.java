package pl.futurecollars.invoice.db.nosql;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "database.type", havingValue = "mongo")
public class MongoBasedDatabaseConfiguration {

  @Bean
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
  public MongoIdProvider mongoIdProvider(
      @Value("${database.counter.collection}") String collectionName, MongoDatabase mongoDatabase) {
    return new MongoIdProvider(mongoDatabase.getCollection(collectionName, Document.class));
  }

  @Bean
  public Database<Invoice> mongoBasedInvoiceDatabase(@Value("${database.invoice.collection}") String collectionName,
                                                     MongoDatabase mongoDatabase,
                                                     MongoIdProvider mongoIdProvider) {
    log.info("Running on nosql Mongo database");
    return new MongoBasedDatabase<>(mongoDatabase.getCollection(collectionName, Invoice.class), mongoIdProvider);
  }

  @Bean
  public Database<Company> mongoBasedCompanyDatabase(@Value("${database.company.collection}") String collectionName,
                                                     MongoDatabase mongoDatabase,
                                                     MongoIdProvider mongoIdProvider) {
    return new MongoBasedDatabase<>(mongoDatabase.getCollection(collectionName, Company.class), mongoIdProvider);
  }

}
