package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoicing.TestHelpers.invoice

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


    def "get all returns empty collection if there were no invoices"() {
        expect:
        service.getAll().isEmpty()
    }

    def "get all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach({ service.saveInvoice(it) })

        expect:
        service.getAll().size() == invoices.size()

        when:
        service.deleteInvoice(1)

        then:
        service.getAll().size() == invoices.size() - 1
    }

    def "can delete all invoices"() {
        given:
        invoices.forEach({ service.saveInvoice(it) })

        when:
        invoices.forEach({ service.deleteInvoice(it.getId()) })

        then:
        service.getAll().isEmpty()
    }

    def "deleting not existing invoice is not causing any error"() {
        expect:
        service.deleteInvoice(123)
    }

    def "updating not existing invoice throws exception"() {
        when:
        service.updateInvoice(213, invoices.get(1))

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Id 213 does not exist"
    }

    def "find for id invoice"() {
        given:
        long id = service.saveInvoice(invoices.get(0))

        when:
        Invoice invoice = service.findForId(id)

        then:
        invoice == invoices.get(0)
    }

    def "update the invoice"() {
        given:
        long id = service.saveInvoice(invoices.get(0))

        when:
        service.updateInvoice(id, invoices.get(1))

        then:
        service.findForId(id) == invoices.get(1)
    }

    def "delete the invoice"() {
        given:
        long id = service.saveInvoice(invoices.get(0))

        when:
        service.deleteInvoice(id)

        then:
        service.all.isEmpty()
    }

}
