package pl.futurecollars.invoice.db

import pl.futurecollars.invoice.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.invoice

abstract class AbstractDatabaseTest extends Specification {

    protected Database database
    protected List<Invoice> invoices

    def setup() {
        invoices = (1L..12L).collect { invoice(it) }
    }

    def "should save invoices returning sequential id, id should have correct value, findById should return saved invoice"() {
        when:
        def ids = invoices.collect({ database.save(it)})
        then:
        ids == (1L..invoices.size()).collect()
        ids.forEach({ assert database.findById(it).get().getId() == it })
        ids.forEach({ assert database.findById(it).get().getId() == invoices.get(it as int - 1).getId() })
    }

    def "should return correct number of invoices saved in database"() {
        given:
        invoices.forEach({ database.save(it) })

        expect:
        assert database.getAll().size() == invoices.size()
    }

    def "should get by id returns null when there is no invoice with given id"() {
        expect:
        database.findById(1).isEmpty()
    }

    def "should get all returns empty collection if there were no invoices"() {
        expect:
        database.getAll().isEmpty()
    }

    def "should the database be empty when all invoices are deleted"() {
        given:
        invoices.forEach({ database.save(it) })
        when:
        invoices.forEach({ database.delete(it.getId()) })
        then:
        database.getAll().isEmpty()
    }

    def "should the deletion of non-existent invoice returns false"() {
        expect:
        database.delete(123)
    }

    def "should be possible to update the invoice"() {
        given:
        long id = database.save(invoices.get(0))
        when:
        database.update(id, invoices.get(1))
        then:
        database.findById(id).get().getId() == invoices.get(0).getId()
    }

    def "should updating not existing invoice throws exception"() {
        when:
        database.update(213, invoices.get(1))
        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Id 213 does not exist"
    }

    def "should get all returns all invoices in the database, should deleted invoice is not returned"() {
        given:
        invoices.forEach({ database.save(it) })
        expect:
        database.getAll().size() == invoices.size()
        database.getAll().forEach({ assert it.getId() == invoices.get(it.getId() - 1 as int).getId() })
        when:
        database.delete(1)
        then:
        database.getAll().size() == invoices.size() - 1
        database.getAll().forEach({ assert it.getId() == invoices.get(it.getId() - 1 as int).getId() })
        database.getAll().forEach({ assert it.getId() != 1 })
    }

}
