package pl.futurecollars.invoice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.utils.JsonService
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoice.TestHelpers.invoice

@AutoConfigureMockMvc
@SpringBootTest
@Unroll
class InvoiceControllerIntegrationTest extends Specification {

    private static final String ENDPOINT = "/invoices"

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }

    def "should empty array is returned when no invoices were added"() {
        expect:
        getAllInvoices() == []
    }

    def "should add invoice return sequential id"() {
        given:
        def invoiceAsJson = invoiceAsJson(1)

        expect:
        def firstId = addInvoice(invoiceAsJson)
        addInvoice(invoiceAsJson) == firstId + 1
        addInvoice(invoiceAsJson) == firstId + 2
        addInvoice(invoiceAsJson) == firstId + 3
        addInvoice(invoiceAsJson) == firstId + 4
    }

    def "should all invoices are returned when getting all invoices"() {
        given:
        def numberOfInvoices = 3
        addUniqueInvoices(numberOfInvoices)

        when:
        def invoices = getAllInvoices()

        then:
        invoices.size() == numberOfInvoices
   }

    def "should correct invoice is returned when getting by id"() {
        given:
        def expectedInvoices = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoices.get(2)

        when:
        def invoice = getInvoiceById(verifiedInvoice.getId())

        then:
        invoice == verifiedInvoice
    }

    def "should get status 404 when invoice id is not found when getting invoice by id [#id]"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                get("$ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())

        where:
        id << [-100, -2, -1, 0, 168, 1256]
    }

    def "should get status 404 when invoice id is not found when deleting invoice [#id]"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                delete("$ENDPOINT/$id")
        )
                .andExpect(status().isNotFound())


        where:
        id << [-100, -2, -1, 0, 12, 13, 99, 102, 1000]
    }

    def "should get status 404 when invoice id is not found when updating invoice [#id]"() {
        given:
        addUniqueInvoices(11)

        expect:
        mockMvc.perform(
                put("$ENDPOINT/$id")
                        .content(invoiceAsJson(1))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())


        where:
        id << [-100, -2, -1, 0, 12, 13, 99, 102, 1000]
    }

    def "should invoice date can be modified"() {
        given:
        def id = addInvoice(invoiceAsJson(44))
        def updatedInvoice = invoice(123)
        updatedInvoice.id = id

        expect:
        mockMvc.perform(
                put("$ENDPOINT/$id")
                        .content(jsonService.toJson(updatedInvoice))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())

    }

    def "should invoice can be deleted"() {
        given:
        def invoices = addUniqueInvoices(69)

        expect:
        invoices.each { invoice -> deleteInvoice(invoice.getId()) }
        getAllInvoices().size() == 0
    }

    private ResultActions deleteInvoice(long id) {
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isOk())
    }

    private long addInvoice(String invoiceAsJson) {
        Long.valueOf(
                mockMvc.perform(
                        post(ENDPOINT)
                                .content(invoiceAsJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString
        )
    }

    private Invoice getInvoiceById(long id) {
        def invoiceAsString = mockMvc.perform(get("$ENDPOINT/$id"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(invoiceAsString, Invoice)
    }

    private List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toObject(response, Invoice[])
    }

    private List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoice(jsonService.toJson(invoice))
            return invoice
        }
    }

    private String invoiceAsJson(int id) {
        jsonService.toJson(invoice(id))
    }
}