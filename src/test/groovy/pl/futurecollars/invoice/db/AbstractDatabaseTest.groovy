package pl.futurecollars.invoice.db

import pl.futurecollars.invoice.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.invoice

abstract class AbstractDatabaseTest extends Specification {

    Database database = getDatabaseInstance()
    List<Invoice> invoices = (1L..12L).collect { invoice(it) }

    abstract Database getDatabaseInstance()

    def "should save invoices returning sequential id, id should have correct value, findById should return saved invoice"() {
        when:
        def ids = invoices.collect({it.id = database.save(it) })
        then:
        ids == (1L..invoices.size()).collect()
        ids.forEach({ assert database.findById(it).isPresent() })
        ids.forEach({ assert database.findById(it).get().getId() == it })
        ids.forEach({ assert resetIds(database.findById(it).get()) == invoices.get(it as int - 1) })
    }

    def "should return correct number of invoices saved in database"() {
        given:
        invoices.forEach({ database.save(it) })

        expect:
        assert database.getAll().size() == invoices.size()
    }

    def "find by id should returns null when there is no invoice with given id"() {
        expect:
        database.findById(1).isEmpty()
    }

    def "should get all returns empty collection if there were no invoices"() {
        expect:
        database.getAll().isEmpty()
    }

    def "the database should be empty when all invoices are deleted"() {
        given:
        invoices.forEach({it.id = database.save(it) })
        when:
        invoices.forEach({ database.delete(it.getId()) })
        then:
        database.getAll().isEmpty()
    }

    def "should be possible to update the invoice"() {
        given:
        def originalInvoice = invoices.get(0)
        originalInvoice.id = database.save(originalInvoice)
        def expectedInvoice = invoices.get(1)
        expectedInvoice.id = originalInvoice.id
        when:
        database.update(originalInvoice.id, expectedInvoice)
        then:
        def updatedInvoice = database.findById(originalInvoice.id).get()
        resetIds(updatedInvoice) == expectedInvoice
    }

    def "getting all should returns all invoices in the database, deleted invoice should not be returned"() {
        given:
        invoices.forEach({ database.save(it) })
        expect:
        database.getAll().size() == invoices.size()
        when:
        database.delete(1)
        then:
        database.getAll().size() == invoices.size() - 1
    }

    static Invoice resetIds(Invoice invoice) {
        invoice.getBuyer().id = 0
        invoice.getSeller().id = 0
        return invoice
    }

}
