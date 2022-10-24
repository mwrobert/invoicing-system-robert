package pl.futurecollars.invoice.db.memory

import pl.futurecollars.invoice.db.AbstractDatabaseTest
import pl.futurecollars.invoice.db.Database

class MemoryDatabaseIntegrationTest extends AbstractDatabaseTest {

    @Override
    Database getDatabaseInstance() {
        new MemoryDatabase()
    }

}
