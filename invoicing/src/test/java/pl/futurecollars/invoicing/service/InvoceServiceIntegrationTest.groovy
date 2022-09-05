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
        def ids = invoices.collect({ service.save(it) })

        then:
        ids.forEach({ assert service.getById(it) != null })
        ids.forEach({ assert service.getById(it).getId() == it })
    }

    def "should get by id returns null when there is no invoice with given id"() {
        expect:
        service.getById(1) == null
    }

    def "should get all returns empty collection if there were no invoices"() {
        expect:
        service.getAll().isEmpty()
    }

    def "should get all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach({ service.save(it) })

        expect:
        service.getAll().size() == invoices.size()

        when:
        service.delete(1)

        then:
        service.getAll().size() == invoices.size() - 1
    }

    def "should can delete all invoices"() {
        given:
        invoices.forEach({ service.save(it) })

        when:
        invoices.forEach({ service.delete(it.getId()) })

        then:
        service.getAll().isEmpty()
    }

    def "should deleting not existing invoice is not causing any error"() {
        expect:
        service.delete(123)
    }

    def "should be possible to update the invoice"() {
        given:
        long id = service.save(invoices.get(0))
        when:
        service.update(id, invoices.get(1))
        then:
        service.getById(id).get() == invoices.get(1)
    }

    def "should updating not existing invoice throws exception"() {
        when:
        service.update(213, invoices.get(1))

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Id 213 does not exist"
    }

    def "should find for id invoice returns invoice"() {
        given:
        long id = service.save(invoices.get(0))

        when:
        Invoice invoice = service.getById(id).get()

        then:
        invoice == invoices.get(0)
    }

}
