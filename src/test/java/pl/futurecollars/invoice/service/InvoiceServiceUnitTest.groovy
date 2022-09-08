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
        def invoiceId = 1
        def invoice = invoice(invoiceId)
        when:
        service.saveInvoice(invoice)
        then:
        1 * database.save(invoice)
    }

    def "calling delete() should delegate to database delete() method"() {
        given:
        def invoiceId = 12
        when:
        service.deleteInvoice(invoiceId)
        then:
        1 * database.delete(invoiceId)
    }

    def "calling findForId() should delegate to database findForId() method"() {
        given:
        def invoiceId = 21
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
        def invoice = invoice(1)
        when:
        service.updateInvoice(invoice.getId(), invoice)
        then:
        1 * database.update(invoice.getId(), invoice)
    }

}


