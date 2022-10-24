package pl.futurecollars.invoice.db

import pl.futurecollars.invoice.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.firstInvoice
import static pl.futurecollars.invoice.TestHelpers.invoice

abstract class AbstractDatabaseTest extends Specification {

    abstract Database getDatabaseInstance()

    Database database

    List<Invoice> invoices = (1L..12L).collect { invoice(it) }

    def setup() {
        database = getDatabaseInstance()
    }

    def "should save invoices returning sequential id"() {
        when:
        def ids = invoices.collect { it.id = database.save(it) }

        then:
        (1..invoices.size() - 1).forEach { assert ids[it] == ids[0] + it }
    }

    def "should invoice have id set to correct value"() {
        when:
        def ids = invoices.collect { it.id = database.save(it) }

        then:
        ids.forEach { assert database.findById(it).isPresent() }
        ids.forEach { assert database.findById(it).get().getId() == it }
    }

    def "should get by id returns expected invoice"() {
        when:
        def ids = invoices.collect { it.id = database.save(it) }

        then:
        ids.forEach {
            def expectedInvoice = resetIds(invoices.get((int) (it - ids[0]))).toString()
            def invoiceFromDb = resetIds((Invoice) (database.findById(it).get())).toString()
            assert invoiceFromDb == expectedInvoice
        }
    }

    def "should return empty collection on getAll method call if there were no invoices in database"() {
        when:
        deleteAllInvoices()

        then:
        database.getAll().isEmpty()
    }

    def "the database should be empty when all invoices are deleted"() {
        given:
        deleteAllInvoices()
        invoices.forEach({ it.id = database.save(it) })

        when:
        invoices.forEach({ database.delete(it.getId()) })

        then:
        database.getAll().isEmpty()
    }

    def "should be possible to update saved invoice"() {
        given:
        def id = database.save(invoices.get(1))

        when:
        def newInvoice = invoices.get(2)
        def updateOptional = database.update(id, newInvoice)
        Invoice updatedInvoice = (Invoice) database.findById(id).get()
        newInvoice.setId(id)
        newInvoice.getBuyer().setId(updatedInvoice.getBuyer().getId())
        newInvoice.getSeller().setId(updatedInvoice.getSeller().getId())
        newInvoice.setInvoiceEntries(updatedInvoice.getInvoiceEntries())

        then:
        updateOptional.isPresent()
        updatedInvoice == newInvoice
    }

    def "getAll should returns all invoices in the database, deleted invoice should not be returned"() {
        given:
        def sizeBeforeSave = database.getAll().size()
        def id = database.save(firstInvoice)

        expect:
        database.getAll().size() == sizeBeforeSave + 1

        when:
        database.delete(id)

        then:
        database.getAll().size() == sizeBeforeSave
    }

    def deleteAllInvoices() {
        database.getAll().forEach(invoice -> database.delete(invoice.getId()))
    }

    static Invoice resetIds(Invoice invoice) {
        invoice.getBuyer().id = 0
        invoice.getSeller().id = 0
        return invoice
    }
}
