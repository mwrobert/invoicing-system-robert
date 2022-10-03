package pl.futurecollars.invoice.controller

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoice.TestHelpers

@AutoConfigureMockMvc
@SpringBootTest
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "should return response with zeros when there are no invoices in database"() {
        when:
        def taxCalculatorResponse = getTaxCalculatorResult("0")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "should return response with correct values when there is invoice in database"() {
        given:
        addInvoice(TestHelpers.firstInvoice)

        when:
        def taxCalculatorResponse = getTaxCalculatorResult("1111111111")

        then:
        taxCalculatorResponse.income == 10000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 10000
        taxCalculatorResponse.incomingVat == 2300
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToReturn == 2300
    }

    def "should return response with correct values when there are invoices in database"() {
        given:
        addInvoice(TestHelpers.firstInvoice)
        addInvoice(TestHelpers.secondInvoice)
        addInvoice(TestHelpers.thirdInvoice)

        when:
        def taxCalculatorResponse = getTaxCalculatorResult("1111111111")

        then:
        taxCalculatorResponse.income == 70000
        taxCalculatorResponse.costs == 50000
        taxCalculatorResponse.earnings == 20000
        taxCalculatorResponse.incomingVat == 7100
        taxCalculatorResponse.outgoingVat == 2500
        taxCalculatorResponse.vatToReturn == 4600
    }

}
