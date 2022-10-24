package pl.futurecollars.invoice.service.company

import pl.futurecollars.invoice.db.Database
import pl.futurecollars.invoice.db.memory.MemoryDatabase
import pl.futurecollars.invoice.model.Company
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.company

class CompanyServiceIntegrationTest extends Specification {

    private CompanyService service
    private List<Company> companies

    def setup() {
        Database db = new MemoryDatabase()
        service = new CompanyService(db)

        companies = (1..12).collect { company(it) }
    }

    def "should returns sequential id and company should have id set to correct value using saveCompany, findCompanyById returns saved company"() {
        when:
        def ids = companies.collect({ service.saveCompany(it) })

        then:
        ids.forEach({ assert service.findCompanyById(it) != null })
        ids.forEach({ assert service.findCompanyById(it).get().getId() == it })
    }

    def "should returns empty Optional<Company> when there is no company with given id using findCompanyById"() {
        expect:
        service.findCompanyById(1).isEmpty()
    }

    def "should returns empty collection using getAllCompanies if there were no companies"() {
        expect:
        service.getAllCompanies().isEmpty()
    }

    def "should returns all companies in the database using getAllCompanies, deleted company is not returned"() {
        given:
        companies.forEach({ service.saveCompany(it) })

        expect:
        service.getAllCompanies().size() == companies.size()

        when:
        service.deleteCompany(1)

        then:
        service.getAllCompanies().size() == companies.size() - 1
    }

    def "should delete all companies using deleteCompany"() {
        given:
        companies.forEach({ service.saveCompany(it) })

        when:
        companies.forEach({ service.deleteCompany(it.getId()) })

        then:
        service.getAllCompanies().isEmpty()
    }

    def "should not cause any error when deleting not existing company"() {
        expect:
        !service.deleteCompany(123)
    }

    def "should update the company using updateCompany"() {
        given:
        long id = service.saveCompany(companies.get(0))
        when:
        service.updateCompany(id, companies.get(1))
        then:
        service.findCompanyById(id).get() == companies.get(1)
    }

    def "should get false when updating not existing company using updateCompany"() {
        expect:
        !service.updateCompany(213, companies.get(1))
    }

    def "should returns company using findCompanyById"() {
        given:
        long id = service.saveCompany(companies.get(0))

        when:
        Company company = service.findCompanyById(id).get()

        then:
        company == companies.get(0)
    }

}
