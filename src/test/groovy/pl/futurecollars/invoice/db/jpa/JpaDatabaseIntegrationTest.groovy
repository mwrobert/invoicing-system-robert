package pl.futurecollars.invoice.db.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoice.db.AbstractDatabaseTest
import pl.futurecollars.invoice.db.Database

@DataJpaTest
@IfProfileValue(name = "spring.profiles.active", value = "jpa")
class JpaDatabaseIntegrationTest extends AbstractDatabaseTest {

    @Autowired
    private InvoiceRepository invoiceRepository

    @Override
    Database getDatabaseInstance() {
        assert invoiceRepository != null
        new JpaDatabase(invoiceRepository)
    }

}