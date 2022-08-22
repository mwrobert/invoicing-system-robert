package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoicing.TestHelpers.invoice

class InMemoryDatabaseTest extends Specification {

    private Database database
    private List<Invoice> invoices

    def setup() {
        database = new InMemoryDatabase()

        invoices = (1..12).collect { invoice(it) }
    }

    def "should save invoices returning sequential id, invoice should have id set to correct value, get by id returns saved invoice"() {
        when:
        def ids = invoices.collect({ database.save(it) })

        then:
        ids.forEach({ assert database.findById(it.longValue()).getId() == it })
    }

    def "get all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach({ database.save(it) })

        when:
        database.delete(1)

        then:
        database.findById(1) == null
    }

    def "can delete all invoices"() {
        given:
        invoices.forEach({ database.save(it) })

        when:
        invoices.forEach({ database.delete(it.getId()) })

        then:
        database.findById(1) == null
    }

    def "deleting not existing invoice is not causing any error"() {
        expect:
        database.delete(123)
    }

    def "it's possible to update the invoice"() {
        given:
        long id = database.save(invoices.get(0))

        when:
        database.update(id, invoices.get(1))

        then:
        database.findById(id) == invoices.get(1)
    }

    def "updating not existing invoice throws exception"() {
        when:
        database.update(1, invoices.get(1))

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Id 1 does not exist"
    }

}