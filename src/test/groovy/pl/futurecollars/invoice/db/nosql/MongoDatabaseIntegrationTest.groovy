package pl.futurecollars.invoice.db.nosql

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoice.db.AbstractDatabaseTest
import pl.futurecollars.invoice.db.Database
import pl.futurecollars.invoice.model.Invoice

@SpringBootTest
@IfProfileValue(name = "spring.profiles.active", value = "mongo")
class MongoDatabaseIntegrationTest extends AbstractDatabaseTest {

    @Autowired
    private Database<Invoice> mongoBasedInvoiceDatabase

    @Override
    Database getDatabaseInstance() {
        assert mongoBasedInvoiceDatabase != null
        mongoBasedInvoiceDatabase
    }

}
