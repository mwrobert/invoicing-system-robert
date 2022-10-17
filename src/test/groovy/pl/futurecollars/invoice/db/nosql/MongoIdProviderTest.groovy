package pl.futurecollars.invoice.db.nosql

import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.IfProfileValue
import spock.lang.Specification

@SpringBootTest
@IfProfileValue(name = "spring.profiles.active", value = "mongo")
class MongoIdProviderTest extends Specification {

    @Value('${database.counter.collection}')
    private String collectionName

    @Autowired
    private MongoDatabase mongoDatabase

    @Autowired
    private MongoIdProvider mongoIdProvider

    private Document FILTER_DOCUMENT = new Document("_id", "invoiceCounter")

    def "should return correct next id and increment it"() {
        given:
        def baseId = mongoDatabase.getCollection(collectionName, Document.class)
                .find(FILTER_DOCUMENT)
                .iterator()
                .next()
                .get("lastValue")

        expect:
        baseId + 1 == mongoIdProvider.getNextIdAndIncrement()

        and:
        baseId + 2 == mongoIdProvider.getNextIdAndIncrement()

        and:
        baseId + 3 == mongoIdProvider.getNextIdAndIncrement()

    }

    def counterDocument(long value) {
        Document document = new Document()
        document.append("_id", "invoiceCounter")
        document.append("lastValue", value)
    }

}
