package pl.futurecollars.invoice.controller

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoice.TestHelpers

import static pl.futurecollars.invoice.TestHelpers.firstCompany

@AutoConfigureMockMvc
@SpringBootTest
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "should return response with correct values when there are no invoices in database"() {

        when:
        def taxCalculatorResponse = getTaxCalculatorResult(firstCompany)

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 0
        taxCalculatorResponse.pensionInsurance == 1400
        taxCalculatorResponse.incomeMinusCostsAndPensionInsurance == -1400
        taxCalculatorResponse.taxCalculationBase == -1400
        taxCalculatorResponse.incomeTax == -266
        taxCalculatorResponse.healthInsurance == 45.00
        taxCalculatorResponse.healthInsuranceDeductible == 38.75
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == -304.75
        taxCalculatorResponse.finalIncomeTax == -305
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToReturn == 0

    }


    def "should return response with correct values when there is invoice in database"() {
        given:
        addInvoice(TestHelpers.firstInvoice)

        when:
        def taxCalculatorResponse = getTaxCalculatorResult(firstCompany)

        then:
        taxCalculatorResponse.income == 10000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 10000
        taxCalculatorResponse.pensionInsurance == 1400
        taxCalculatorResponse.incomeMinusCostsAndPensionInsurance == 8600
        taxCalculatorResponse.taxCalculationBase == 8600
        taxCalculatorResponse.incomeTax == 1634.00
        taxCalculatorResponse.healthInsurance == 45.00
        taxCalculatorResponse.healthInsuranceDeductible == 38.75
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == 1595.25
        taxCalculatorResponse.finalIncomeTax == 1595.00
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
        def taxCalculatorResponse = getTaxCalculatorResult(firstCompany)

        then:
        taxCalculatorResponse.income == 70000
        taxCalculatorResponse.costs == 50000
        taxCalculatorResponse.incomeMinusCosts == 20000
        taxCalculatorResponse.pensionInsurance == 1400
        taxCalculatorResponse.incomeMinusCostsAndPensionInsurance == 18600
        taxCalculatorResponse.taxCalculationBase == 18600
        taxCalculatorResponse.incomeTax == 3534.00
        taxCalculatorResponse.healthInsurance == 45.00
        taxCalculatorResponse.healthInsuranceDeductible == 38.75
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == 3495.25
        taxCalculatorResponse.finalIncomeTax == 3495
        taxCalculatorResponse.incomingVat == 7100.0
        taxCalculatorResponse.outgoingVat == 2500.0
        taxCalculatorResponse.vatToReturn == 4600.0

    }

    def "should return response with correct values when there are invoice with payment for car, which is used also for personal reasons"() {
        given:
        addInvoice(TestHelpers.fourthInvoice)

        when:
        def taxCalculatorResponse = getTaxCalculatorResult(firstCompany)

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 11150
        taxCalculatorResponse.incomeMinusCosts == -11150
        taxCalculatorResponse.pensionInsurance == 1400
        taxCalculatorResponse.incomeMinusCostsAndPensionInsurance == -12550
        taxCalculatorResponse.taxCalculationBase == -12550
        taxCalculatorResponse.incomeTax == -2384.50
        taxCalculatorResponse.healthInsurance == 45.00
        taxCalculatorResponse.healthInsuranceDeductible == 38.75
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == -2423.25
        taxCalculatorResponse.finalIncomeTax == -2423
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 1150
        taxCalculatorResponse.vatToReturn == -1150

    }

    def "should return response with correct values when there are invoice with payment for car, which is used for business purposes only"() {
        given:
        addInvoice(TestHelpers.fifthInvoice)

        when:
        def taxCalculatorResponse = getTaxCalculatorResult(firstCompany)

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 10000
        taxCalculatorResponse.incomeMinusCosts == -10000
        taxCalculatorResponse.pensionInsurance == 1400
        taxCalculatorResponse.incomeMinusCostsAndPensionInsurance == -11400
        taxCalculatorResponse.taxCalculationBase == -11400
        taxCalculatorResponse.incomeTax == -2166
        taxCalculatorResponse.healthInsurance == 45.00
        taxCalculatorResponse.healthInsuranceDeductible == 38.75
        taxCalculatorResponse.incomeTaxMinusHealthInsurance == -2204.75
        taxCalculatorResponse.finalIncomeTax == -2205
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 2300
        taxCalculatorResponse.vatToReturn == -2300

    }
}
