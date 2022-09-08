package pl.futurecollars.invoice.util

import pl.futurecollars.invoice.TestHelpers
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.utils.JsonService
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
