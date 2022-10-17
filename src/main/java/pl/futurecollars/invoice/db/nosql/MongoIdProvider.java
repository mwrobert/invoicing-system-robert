package pl.futurecollars.invoice.db.nosql;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javax.annotation.PostConstruct;
import org.bson.Document;

public class MongoIdProvider {

  private static final String LAST_VALUE_KEY = "lastValue";
  private static final String ID_VALUE = "invoiceCounter";
  private static final String ID_KEY = "_id";
  private static final Document FILTER_DOCUMENT = new Document(ID_KEY, ID_VALUE);

  private final MongoCollection<Document> idCollection;
  private long lastValue = 0;

  public MongoIdProvider(MongoCollection<Document> idCollection) {
    this.idCollection = idCollection;
  }

  @PostConstruct
  private void initialize() {
    MongoCursor<Document> iterator = idCollection.find(FILTER_DOCUMENT).iterator();

    if (iterator.hasNext()) {
      lastValue = (long) iterator.next().get(LAST_VALUE_KEY);
    } else {
      idCollection.insertOne(counterDocument(0L));
    }
  }

  public long getNextIdAndIncrement() {
    idCollection.findOneAndReplace(
        FILTER_DOCUMENT,
        counterDocument(++lastValue)
    );
    return lastValue;
  }

  private Document counterDocument(long value) {
    Document document = new Document();
    document.append(ID_KEY, ID_VALUE);
    document.append(LAST_VALUE_KEY, value);

    return document;
  }

}
