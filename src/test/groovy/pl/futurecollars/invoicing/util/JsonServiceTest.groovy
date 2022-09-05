package pl.futurecollars.invoicing.util

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

class JsonServiceTest extends Specification {

    def "should can convert object to json and read it back"() {
        given:
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(12)

        when:
        def invoiceAsString = jsonService.toJson(invoice)

        and:
        def invoiceFromJson = jsonService.toObject(invoiceAsString, Invoice)

        then:
        invoice.getDate() == invoiceFromJson.getDate()
        invoice.getFromCompany().toString() == invoiceFromJson.getFromCompany().toString()
        invoice.getToCompany().toString() == invoiceFromJson.getToCompany().toString()
        invoice.getInvoiceEntries().toString() == invoiceFromJson.getInvoiceEntries().toString()
        invoice.getId() == invoiceFromJson.getId()
    }

}
