package pl.futurecollars.invoice.service

import pl.futurecollars.invoice.db.Database
import pl.futurecollars.invoice.db.memory.InMemoryDatabase
import pl.futurecollars.invoice.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.invoice

class InvoiceServiceIntegrationTest extends Specification {

    private InvoiceService service
    private List<Invoice> invoices

    def setup() {
        Database db = new InMemoryDatabase()
        service = new InvoiceService(db)

        invoices = (1..12).collect { invoice(it) }
    }

    def "should save invoices returning sequential id, invoice should have id set to correct value, get by id returns saved invoice"() {
        when:
        def ids = invoices.collect({ service.saveInvoice(it) })

        then:
        ids.forEach({ assert service.findForId(it) != null })
        ids.forEach({ assert service.findForId(it).getId() == it })
    }

    def "should get by id returns null when there is no invoice with given id"() {
        expect:
        service.findForId(1) == null
    }

    def "should get all returns empty collection if there were no invoices"() {
        expect:
        service.getAll().isEmpty()
    }

    def "should get all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach({ service.saveInvoice(it) })

        expect:
        service.getAll().size() == invoices.size()

        when:
        service.deleteInvoice(1)

        then:
        service.getAll().size() == invoices.size() - 1
    }

    def "should can delete all invoices"() {
        given:
        invoices.forEach({ service.saveInvoice(it) })

        when:
        invoices.forEach({ service.deleteInvoice(it.getId()) })

        then:
        service.getAll().isEmpty()
    }

    def "should deleting not existing invoice is not causing any error"() {
        expect:
        service.deleteInvoice(123)
    }

    def "should be possible to update the invoice"() {
        given:
        long id = service.saveInvoice(invoices.get(0))
        when:
        service.updateInvoice(id, invoices.get(1))
        then:
        service.findForId(id).get() == invoices.get(1)
    }

    def "should updating not existing invoice throws exception"() {
        when:
        service.updateInvoice(213, invoices.get(1))

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Id 213 does not exist"
    }

    def "should find for id invoice returns invoice"() {
        given:
        long id = service.saveInvoice(invoices.get(0))

        when:
        Invoice invoice = service.findForId(id).get()

        then:
        invoice == invoices.get(0)
    }

}
