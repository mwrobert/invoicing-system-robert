package pl.futurecollars.invoice.db.memory

import pl.futurecollars.invoice.db.AbstractDatabaseTest

class InMemoryDatabaseTest extends AbstractDatabaseTest {

    def setup() {
        database = new InMemoryDatabase()
    }

}