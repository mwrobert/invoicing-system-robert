package pl.futurecollars.invoice.db.memory

import pl.futurecollars.invoice.db.AbstractDatabaseTest
import pl.futurecollars.invoice.db.Database

class MemoryRepositoryIntegrationTest extends AbstractDatabaseTest {

    @Override
    Database getDatabaseInstance() {
        new MemoryRepository()
    }

}