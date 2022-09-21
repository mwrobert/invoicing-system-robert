package pl.futurecollars.invoice.service


import pl.futurecollars.invoice.db.Database
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.invoice

class InvoiceServiceUnitTest extends Specification {

    private InvoiceService service
    private Database database

    def setup() {
        database = Mock()
        service = new InvoiceService(database)
    }

    def "calling save() should delegate to database save() method"() {
        given:
        def invoiceId = 1L
        def invoice = invoice(invoiceId)
        when:
        service.saveInvoice(invoice)
        then:
        1 * database.save(invoice)
    }

    def "calling deleteInvoice() should return false if invoice does not exist in database"() {
        given:
        def invoiceId = 1L
        database.findById(invoiceId) >> Optional.empty()
        when:
        def result = service.deleteInvoice(invoiceId)
        then:
        !result
    }

    def "calling deleteInvoice() should return true if invoice exists in database"() {
        given:
        def invoiceId = 1L
        def invoice = invoice(invoiceId)
        database.findById(invoiceId) >> Optional.of(invoice)
        when:
        def result = service.deleteInvoice(invoiceId)
        then:
        result
    }

    def "calling findForId() should delegate to database findForId() method"() {
        given:
        def invoiceId = 21L
        when:
        service.findForId(invoiceId)
        then:
        1 * database.findById(invoiceId)
    }

    def "calling getAll() should delegate to database getAll() method"() {
        when:
        service.getAll()
        then:
        1 * database.getAll()
    }

    def "calling updateInvoice() should return true if invoice to update exists in database"() {
        given:
        def invoiceId = 1L
        def invoice = invoice(invoiceId)
        database.findById(invoiceId) >> Optional.of(invoice)
        when:
        def result = service.updateInvoice(invoiceId, invoice)
        then:
        result
    }

    def "calling updateInvoice() should return false if invoice to update does not exist in database"() {
        given:
        def invoiceId = 1L
        def invoice = invoice(invoiceId)
        database.findById(invoiceId) >> Optional.empty()
        when:
        def result = service.updateInvoice(invoiceId, invoice)
        then:
        !result
    }

}


