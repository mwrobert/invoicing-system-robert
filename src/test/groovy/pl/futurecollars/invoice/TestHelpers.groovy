package pl.futurecollars.invoice

import pl.futurecollars.invoice.model.Car
import pl.futurecollars.invoice.model.Company
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.model.InvoiceEntry
import pl.futurecollars.invoice.model.Vat

import java.time.LocalDate

class TestHelpers {

    static company(long id) {
        Company.builder()
            .name(("iCode Trust $id Sp. z o.o"))
            .taxIdentificationNumber(("$id").repeat(10))
            .address("ul. Nowa 24d/$id 02-703 Warszawa, Polska")
            .healthInsurance(BigDecimal.valueOf(500).setScale(2))
            .pensionInsurance(BigDecimal.valueOf(1400).setScale(2))
            .build()
    }

    static product(long id) {
        new InvoiceEntry("Programming course $id",
                BigDecimal.valueOf(1).setScale(2),
                BigDecimal.valueOf(id * 1000).setScale(2),
                BigDecimal.valueOf(id * 1000 * 0.08).setScale(2),
                Vat.Vat_8, null)
    }

    static invoice(long id) {
        Invoice.builder()
            .date(LocalDate.now())
            .number("2022/10/12/0000$id")
            .seller(company(id))
            .buyer(company(id + 1))
            .invoiceEntries(List.of(product(id)))
            .build()
    }

    static Company firstCompany = Company.builder()
            .name("First")
            .taxIdentificationNumber("1111111111")
            .address("ul. Pierwsza 1, Warszawa, Polska")
            .healthInsurance(BigDecimal.valueOf(500).setScale(2))
            .pensionInsurance(BigDecimal.valueOf(1400).setScale(2))
            .build()

    static Company secondCompany = Company.builder()
            .name("Second")
            .taxIdentificationNumber("2222222222")
            .address("ul. Druga 2, Warszawa, Polska")
            .healthInsurance(BigDecimal.valueOf(500).setScale(2))
            .pensionInsurance(BigDecimal.valueOf(1400).setScale(2))
            .build()

    static Car firstCar = new Car("AAA 12 34", true)

    static Car secondCar = new Car("BBB 12 34", false)

    static InvoiceEntry firstEntry = new InvoiceEntry("First product", BigDecimal.valueOf(1).setScale(2), BigDecimal.valueOf(10000).setScale(2), BigDecimal.valueOf(10000 * 0.23).setScale(2), Vat.Vat_23, null)

    static InvoiceEntry secondEntry = new InvoiceEntry("Second product", BigDecimal.valueOf(10).setScale(2), BigDecimal.valueOf(5000).setScale(2), BigDecimal.valueOf(5000 * 0.05).setScale(2), Vat.Vat_5, null)

    static InvoiceEntry thirdEntry = new InvoiceEntry("Third product", BigDecimal.valueOf(1).setScale(2), BigDecimal.valueOf(10000).setScale(2), BigDecimal.valueOf(10000 * 0.23).setScale(2), Vat.Vat_23, firstCar)

    static InvoiceEntry fifthEntry = new InvoiceEntry("Third product", BigDecimal.valueOf(1).setScale(2), BigDecimal.valueOf(10000).setScale(2), BigDecimal.valueOf(10000 * 0.23).setScale(2), Vat.Vat_23, secondCar)

    static Invoice firstInvoice = Invoice.builder()
            .date(LocalDate.of(2022, 10, 1))
            .number("2022/10/12/00001")
            .seller(firstCompany)
            .buyer(secondCompany)
            .invoiceEntries(List.of(firstEntry))
            .build()

    static Invoice secondInvoice = Invoice.builder()
            .date(LocalDate.of(2022, 10, 1))
            .number("2022/10/12/00002")
            .seller(secondCompany)
            .buyer(firstCompany)
            .invoiceEntries(List.of(secondEntry))
            .build()

    static Invoice thirdInvoice = Invoice.builder()
            .date(LocalDate.of(2022, 10, 1))
            .number("2022/10/12/00003")
            .seller(firstCompany)
            .buyer(secondCompany)
            .invoiceEntries(List.of(firstEntry, secondEntry))
            .build()

    static Invoice fourthInvoice = Invoice.builder()
            .date(LocalDate.of(2022, 10, 1))
            .number("2022/10/12/00004")
            .seller(secondCompany)
            .buyer(firstCompany)
            .invoiceEntries(List.of(thirdEntry))
            .build()

    static Invoice fifthInvoice = Invoice.builder()
            .date(LocalDate.of(2022, 10, 1))
            .number("2022/10/12/00005")
            .seller(secondCompany)
            .buyer(firstCompany)
            .invoiceEntries(List.of(fifthEntry))
            .build()

}