package pl.futurecollars.invoice.controller.invoice

import com.mongodb.client.MongoDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import pl.futurecollars.invoice.controller.AbstractControllerTest
import pl.futurecollars.invoice.model.Invoice
import spock.lang.Requires

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoice.TestHelpers.*

class InvoiceControllerIntegrationTest extends AbstractControllerTest {

    @Autowired
    private ApplicationContext context

    @Requires({ System.getProperty('spring.profiles.active', '').contains('mongo') })
    def "database is dropped to ensure clean state"() {
        expect:
        MongoDatabase mongoDatabase = context.getBean(MongoDatabase)
        mongoDatabase.drop()
    }

    def "should return empty array when invoice database is empty"() {
        given:
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
        expect:
        getAllInvoices() == []
    }

    def "should return all invoices"() {
        given:
        def numberOfInvoices = 3
        def expectedInvoices = addUniqueInvoices(numberOfInvoices)

        when:
        def invoices = getAllInvoices()

        then:
        invoices.size() == numberOfInvoices
        resetIds(invoices) == resetIds(expectedInvoices)
    }

    def "should add invoice and return sequential id"() {
        expect:
        def id = addInvoiceAndReturnId(firstInvoice)
        addInvoiceAndReturnId(firstInvoice) == id + 1
        addInvoiceAndReturnId(firstInvoice) == id + 2
        addInvoiceAndReturnId(firstInvoice) == id + 3
        addInvoiceAndReturnId(firstInvoice) == id + 4
    }

    def "should return correct invoice when getting by id"() {
        given:
        def expectedInvoices = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoices.get(2)

        when:
        Invoice invoice = getById(verifiedInvoice.getId(), Invoice, INVOICES_ENDPOINT)
        verifiedInvoice.seller.setId(invoice.seller.getId())
        verifiedInvoice.buyer.setId(invoice.buyer.getId())

        then:
        resetIds(invoice) == resetIds(verifiedInvoice)
    }

    def "should get status 404 when invoice id is not found when getting invoice by id"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                get("$INVOICES_ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-100, -2, -1, 0, 168, 1256]
    }

    def "should get status 404 when invoice id is not found when deleting invoice by id"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                delete("$INVOICES_ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())


        where:
        id << [-100, -2, -1, 0, 1000]
    }

    def "should get status 404 when invoice id is not found when updating invoice by id"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                put("$INVOICES_ENDPOINT/$id")
                        .content(getAsJson(invoice(1)))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())


        where:
        id << [-100, -2, -1, 0, 1000]
    }

    def "should update invoice by id"() {
        given:
        def id = addInvoiceAndReturnId(firstInvoice)
        def updatedInvoice = secondInvoice
        updatedInvoice.id = id
        when:
        mockMvc.perform(
                put("$INVOICES_ENDPOINT/$id")
                        .content(getAsJson(updatedInvoice))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())

        then:
        def invoiceFromDbAfterUpdate = resetIds(getInvoiceById(id)).toString()
        def expectedInvoice = resetIds(updatedInvoice).toString()
        invoiceFromDbAfterUpdate == expectedInvoice

    }

    def "should delete invoice by id"() {
        given:
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }

        def invoices = addUniqueInvoices(69)

        expect:
        invoices.each { invoice -> deleteInvoice(invoice.getId()) }
        getAllInvoices().size() == 0
    }

}