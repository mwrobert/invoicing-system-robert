package pl.futurecollars.invoicing.service


import pl.futurecollars.invoicing.db.Database
import spock.lang.Specification

import static pl.futurecollars.invoicing.TestHelpers.invoice

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

    def "calling delete() should delegate to database delete() method"() {
        given:
        def invoiceId = 12L
        when:
        service.deleteInvoice(invoiceId)
        then:
        1 * database.delete(invoiceId)
    }

    def "calling getById() should delegate to database getById() method"() {
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

    def "calling update() should delegate to database update() method"() {
        given:
        def invoice = invoice(1L)
        when:
        service.updateInvoice(invoice.getId(), invoice)
        then:
        1 * database.update(invoice.getId(), invoice)
    }

}


