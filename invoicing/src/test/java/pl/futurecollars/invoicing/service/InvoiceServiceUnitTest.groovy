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
        def invoiceId = 1
        def invoice = invoice(invoiceId)
        when:
        service.save(invoice)
        then:
        1 * database.save(invoice)
    }

    def "calling delete() should delegate to database delete() method"() {
        given:
        def invoiceId = 12
        when:
        service.delete(invoiceId)
        then:
        1 * database.delete(invoiceId)
    }

    def "calling findForId() should delegate to database findForId() method"() {
        given:
        def invoiceId = 21
        when:
        service.getById(invoiceId)
        then:
        1 * database.get(invoiceId)
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
        service.update(invoice.getId(), invoice)
        then:
        1 * database.update(invoice.getId(), invoice)
    }

}


