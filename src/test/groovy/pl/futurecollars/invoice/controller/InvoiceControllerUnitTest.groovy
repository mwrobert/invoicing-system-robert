package pl.futurecollars.invoice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.futurecollars.invoice.TestHelpers
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.utils.JsonService
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Stepwise
@AutoConfigureMockMvc
@SpringBootTest
class InvoiceControllerUnitTest extends Specification {
    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    def "should get status 404 when you try to get invoice from empty database"() {
        given:
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }

        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices/1")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .response
                .contentAsString.isEmpty()
    }

    def "should get status 404 when you try to update invoice on empty database"() {
        given:
        def originalInvoice = TestHelpers.invoice(1)
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1
        expectedInvoice.date = LocalDate.now()
        def expectedInvoiceAsJson = jsonService.toJson(expectedInvoice)
        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.put("/invoices/1")
                        .content(expectedInvoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .response
                .contentAsString.isEmpty()
    }

    def "should get status 404 when you try to delete invoice from empty database"() {
        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/invoices/1")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .response
                .contentAsString.isEmpty()
    }

    def "should get status 200 when you try to get all invoices from empty database"() {
        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString == "[]"
    }

    def "should save invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        def invoiceAsJson = jsonService.toJson(invoice)

        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.post("/invoices").content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
    }

    def "should get invoice"() {

        def invoice = TestHelpers.invoice(1)
        def invoiceAsJson = jsonService.toJson(invoice)

        def savedInvoiceId = mockMvc.perform(
                MockMvcRequestBuilders.post("/invoices").content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .contentAsString

        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices/" + savedInvoiceId)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString
    }

    def "should return status 200 and properly update existing invoice"() {
        given:
        def id = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/invoices/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonService.toJson(TestHelpers.firstInvoice)))
                .andReturn()
                .getResponse()
                .contentAsString

        def invoiceToUpdate = TestHelpers.secondInvoice

        when:
        def responseUpdate = mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/invoices/" + id)
                        .content(jsonService.toJson(invoiceToUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
        )

        invoiceToUpdate.setId(Long.valueOf(id))
        def responseFindById = mockMvc.perform(MockMvcRequestBuilders.get("/invoices/" + id))


        then:
        responseUpdate.andExpect(MockMvcResultMatchers.status().isOk())
        responseFindById.andReturn().getResponse().contentAsString == jsonService.toJson(invoiceToUpdate)

    }

    def "should delete invoice"() {

        def invoice = TestHelpers.invoice(1)
        def invoiceAsJson = jsonService.toJson(invoice)

        def savedInvoiceId = mockMvc.perform(
                MockMvcRequestBuilders.post("/invoices").content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .contentAsString

        expect:
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/invoices/" + savedInvoiceId)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString.isEmpty()
        and:
        mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices/" + savedInvoiceId)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .response
                .contentAsString.isEmpty()
    }

    private List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(
                MockMvcRequestBuilders.get("/invoices")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toObject(response, Invoice[])
    }

    private ResultActions deleteInvoice(long id) {
        mockMvc.perform(MockMvcRequestBuilders.delete("/invoices/$id"))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    def setupSpec() {
        resetFile("test_db/invoices.json")
        resetFile("test_db/nextId.txt")
    }

    def cleanupSpec() {
        Files.deleteIfExists(Path.of("test_db/invoices.json"))
        Files.deleteIfExists(Path.of("test_db/nextId.txt"))
    }

    private void resetFile(String filePath) {
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

