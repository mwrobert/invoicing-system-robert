package pl.futurecollars.invoice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.service.TaxCalculatorResult
import pl.futurecollars.invoice.utils.JsonService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoice.TestHelpers.invoice

class AbstractControllerTest extends Specification {

    static final String INVOICES_ENDPOINT = "/invoices/"
    private static final String TAX_ENDPOINT = "/tax/"

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }

    long addInvoice(Invoice invoice) {
        Long.valueOf(
                mockMvc.perform(
                        post(INVOICES_ENDPOINT)
                                .content(getInvoiceAsJson(invoice))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString
        )
    }

    void deleteInvoice(long id) {
        mockMvc.perform(
                delete("$INVOICES_ENDPOINT$id"))
                .andExpect(status().isOk())
    }

    Invoice getInvoiceById(long id) {
        def invoiceAsString = mockMvc.perform(get("$INVOICES_ENDPOINT$id"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return getInvoiceAsObject(invoiceAsString)
    }

    List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get(INVOICES_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toObject(response, Invoice[])
    }

    List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoice(invoice)
            return invoice
        }
    }

    TaxCalculatorResult getTaxCalculatorResult(String taxIdentificationNumber) {
        def response = mockMvc.perform(
                get("$TAX_ENDPOINT$taxIdentificationNumber"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(response, TaxCalculatorResult)
    }

    def getInvoiceAsJson(Invoice invoice) {
        jsonService.toJson(invoice)
    }

    def getInvoiceAsObject(String invoiceAsJson) {
        jsonService.toObject(invoiceAsJson, Invoice)
    }

    def setupSpec() {
        resetFile("test_db/invoices.json")
        resetFile("test_db/nextId.txt")
    }

    def cleanupSpec() {
        Files.deleteIfExists(Path.of("test_db/invoices.json"))
        Files.deleteIfExists(Path.of("test_db/nextId.txt"))
    }

    void resetFile(String filePath) {
        def path = Path.of(filePath)
        if (Files.exists(path)) {
            Files.deleteIfExists(path)
            Files.createFile(path)
        } else {
            Files.createDirectories(path.getParent())
            Files.createFile(path)
        }
    }

}
