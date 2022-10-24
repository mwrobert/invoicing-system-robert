package pl.futurecollars.invoice.service.invoice

import pl.futurecollars.invoice.db.Database
import pl.futurecollars.invoice.db.memory.MemoryDatabase
import pl.futurecollars.invoice.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.invoice

class InvoiceServiceIntegrationTest extends Specification {

    private InvoiceService service
    private List<Invoice> invoices

    def setup() {
        Database db = new MemoryDatabase()
        service = new InvoiceService(db)

        invoices = (1..12).collect { invoice(it) }
    }

    def "should returns sequential id and invoice should have id set to correct value using saveInvoice, findInvoiceById returns saved invoice"() {
        when:
        def ids = invoices.collect({ service.saveInvoice(it) })

        then:
        ids.forEach({ assert service.findInvoiceById(it) != null })
        ids.forEach({ assert service.findInvoiceById(it).get().getId() == it })
    }

    def "should returns empty Optional<Invoice> using findInvoiceById when there is no invoice with given id"() {
        expect:
        service.findInvoiceById(1).isEmpty()
    }

    def "should returns empty collection using getAllInvoices if there were no invoices"() {
        expect:
        service.getAllInvoices().isEmpty()
    }

    def "should returns all invoices in the database using getAllInvoices, deleted invoice is not returned"() {
        given:
        invoices.forEach({ service.saveInvoice(it) })

        expect:
        service.getAllInvoices().size() == invoices.size()

        when:
        service.deleteInvoice(1)

        then:
        service.getAllInvoices().size() == invoices.size() - 1
    }

    def "should delete all invoices using deleteInvoice"() {
        given:
        invoices.forEach({ service.saveInvoice(it) })

        when:
        invoices.forEach({ service.deleteInvoice(it.getId()) })

        then:
        service.getAllInvoices().isEmpty()
    }

    def "should not cause any error deleting not existing invoice"() {
        expect:
        !service.deleteInvoice(123)
    }

    def "should be possible to update the invoice using updateInvoice"() {
        given:
        long id = service.saveInvoice(invoices.get(0))
        when:
        service.updateInvoice(id, invoices.get(1))
        then:
        service.findInvoiceById(id).get() == invoices.get(1)
    }

    def "should get false when updating not existing invoice using updateInvoice"() {
        expect:
        !service.updateInvoice(213, invoices.get(1))
    }

    def "should returns invoice using findInvoiceById"() {
        given:
        long id = service.saveInvoice(invoices.get(0))

        when:
        Invoice invoice = service.findInvoiceById(id).get()

        then:
        invoice == invoices.get(0)
    }

}
