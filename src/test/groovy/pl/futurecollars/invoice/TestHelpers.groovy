package pl.futurecollars.invoice

import pl.futurecollars.invoice.model.Company
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.model.InvoiceEntry
import pl.futurecollars.invoice.model.Vat

import java.time.LocalDate

class TestHelpers {

    static company(long id) {
        new Company(("iCode Trust $id Sp. z o.o"),
                ("$id").repeat(10),
                "ul. Nowa 24d/$id 02-703 Warszawa, Polska")
    }

    static product(long id) {
        new InvoiceEntry("Programming course $id", BigDecimal.ONE, BigDecimal.valueOf(id * 1000), BigDecimal.valueOf(id * 1000 * 0.08), Vat.Vat_8)
    }

    static invoice(long id) {
        new Invoice(LocalDate.now(), company(id), company(id), List.of(product(id)))
    }

    static Company firstCompany = new Company("First","1111111111","ul. Pierwsza 1, Warszawa, Polska")

    static Company secondCompany = new Company("Second","2222222222","ul. Druga 2, Warszawa, Polska")

    static InvoiceEntry firstEntry = new InvoiceEntry("First product", BigDecimal.ONE, BigDecimal.valueOf(10000),BigDecimal.valueOf(10000 * 0.23), Vat.Vat_23)

    static InvoiceEntry secondEntry = new InvoiceEntry("Second product", BigDecimal.TEN, BigDecimal.valueOf(5000),BigDecimal.valueOf(5000 * 0.05), Vat.Vat_5)

    static Invoice firstInvoice = new Invoice(LocalDate.of(2022,10,1),firstCompany,secondCompany,List.of(firstEntry))

    static Invoice secondInvoice = new Invoice(LocalDate.of(2022,10,1),secondCompany,firstCompany,List.of(secondEntry))

    static Invoice thirdInvoice = new Invoice(LocalDate.of(2022,10,1),firstCompany,secondCompany,List.of(firstEntry,secondEntry))
}