package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.AbstractDatabaseTest

class InMemoryDatabaseTest extends AbstractDatabaseTest {

    def setup() {
        database = new InMemoryDatabase()
    }

}