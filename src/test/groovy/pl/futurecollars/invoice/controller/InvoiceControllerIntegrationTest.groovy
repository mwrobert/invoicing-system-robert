package pl.futurecollars.invoice.controller

import com.mongodb.client.MongoDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoice.db.nosql.MongoBasedDatabase
import spock.lang.Requires
import spock.lang.Stepwise
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoice.TestHelpers.firstInvoice
import static pl.futurecollars.invoice.TestHelpers.invoice
import static pl.futurecollars.invoice.TestHelpers.secondInvoice
import static pl.futurecollars.invoice.TestHelpers.thirdInvoice

@AutoConfigureMockMvc
@SpringBootTest
@Unroll
@Stepwise
class InvoiceControllerIntegrationTest extends AbstractControllerTest {

    @Autowired
    private ApplicationContext context

    @Requires({ System.getProperty('spring.profiles.active', '').contains('mongo')})
    def "database is dropped to ensure clean state"() {
        expect:
        MongoDatabase mongoDatabase = context.getBean(MongoDatabase)
        mongoDatabase.drop()
    }

    def "should return empty array when database is empty"() {
        expect:
        getAllInvoices() == []
    }

    def "should add invoice and return sequential id"() {
        expect:
        def id = addInvoice(firstInvoice)
        addInvoice(firstInvoice) == id + 1
        addInvoice(firstInvoice) == id + 2
        addInvoice(firstInvoice) == id + 3
        addInvoice(firstInvoice) == id + 4
    }

    def "should return all invoices"() {
        given:
        def numberOfInvoices = 3
        addInvoice(firstInvoice)
        addInvoice(secondInvoice)
        addInvoice(thirdInvoice)

        when:
        def invoices = getAllInvoices()

        then:
        invoices.size() == numberOfInvoices
    }

    def "should return correct invoice when getting by id"() {
        given:
        def expectedInvoices = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoices.get(2)

        when:
        def invoice = getInvoiceById(verifiedInvoice.getId())

        then:
        invoice == verifiedInvoice
    }

    def "should get status 404 when invoice id is not found when getting invoice by id"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                get("$INVOICES_ENDPOINT$id")
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
                delete("$INVOICES_ENDPOINT$id")
        )
                .andExpect(status().isNotFound())


        where:
        id << [-100, -2, -1, 0, 12, 13, 99, 102, 1000]
    }

    def "should get status 404 when invoice id is not found when updating invoice by id"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                put("$INVOICES_ENDPOINT$id")
                        .content(getInvoiceAsJson(invoice(1)))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())


        where:
        id << [-100, -2, -1, 0, 12, 13, 99, 102, 1000]
    }

    def "should update invoice by id"() {
        given:
        def id = addInvoice(firstInvoice)
        def updatedInvoice = secondInvoice
        when:
        mockMvc.perform(
                put("$INVOICES_ENDPOINT$id")
                        .content(getInvoiceAsJson(updatedInvoice))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())

        then:
        secondInvoice.setId(id)
        getInvoiceById(id) == secondInvoice

    }

    def "should delete invoice by id"() {
        given:
        def invoices = addUniqueInvoices(69)

        expect:
        invoices.each { invoice -> deleteInvoice(invoice.getId()) }
        getAllInvoices().size() == 0
    }

}