package pl.futurecollars.invoice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoice.model.Company
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.service.tax.TaxCalculatorResult
import pl.futurecollars.invoice.utils.JsonService
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files
import java.nio.file.Path

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoice.TestHelpers.*

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
@Unroll
class AbstractControllerTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService


    long addInvoiceAndReturnId(Invoice invoice) {
        addAndReturnId(invoice, INVOICES_ENDPOINT)
    }

    int addCompanyAndReturnId(Company company) {
        addAndReturnId(company, COMPANIES_ENDPOINT)
    }

    void deleteInvoice(long id) {
        mockMvc.perform(delete("$INVOICES_ENDPOINT/$id").with(csrf()))
                .andExpect(status().is2xxSuccessful())
    }

    void deleteCompany(long id) {
        mockMvc.perform(delete("$COMPANIES_ENDPOINT/$id").with(csrf()))
                .andExpect(status().is2xxSuccessful())
    }

    Invoice getInvoiceById(long id) {
        getById(id, Invoice, INVOICES_ENDPOINT)
    }

    Company getCompanyById(long id) {
        getById(id, Company, COMPANIES_ENDPOINT)
    }

    List<Invoice> getAllInvoices() {
        getAll(Invoice[], INVOICES_ENDPOINT)
    }

    List<Company> getAllCompanies() {
        getAll(Company[], COMPANIES_ENDPOINT)
    }

    List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoiceAndReturnId(invoice)
            return invoice
        }
    }

    List<Company> addUniqueCompanies(int count) {
        (1..count).collect { id ->
            def company = company(id)
            company.id = addCompanyAndReturnId(company)
            return company
        }
    }


    TaxCalculatorResult getTaxCalculatorResult(Company company) {
        def response =
                mockMvc.perform(
                        post(TAX_ENDPOINT).content(getAsJson(company))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString

        jsonService.toObject(response, TaxCalculatorResult)
    }

    def getAsJson(Object object) {
        jsonService.toJson(object)
    }

    def getAsObject(String objectAsJson, Class clazz) {
        jsonService.toObject(objectAsJson, clazz)
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

    protected <T> int addAndReturnId(T item, String endpoint) {
        Integer.valueOf(
                mockMvc.perform(
                        post(endpoint)
                                .content(jsonService.toJson(item))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString
        )
    }

    protected <T> T getAll(Class<T> clazz, String endpoint) {
        def response = mockMvc.perform(get(endpoint).with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(response, clazz)
    }


    protected <T> T getById(long id, Class<T> clazz, String endpoint) {
        def invoiceAsString = mockMvc.perform(get("$endpoint/$id").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(invoiceAsString, clazz)
    }
}
