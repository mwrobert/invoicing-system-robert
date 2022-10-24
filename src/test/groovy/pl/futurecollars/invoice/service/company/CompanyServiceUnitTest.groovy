package pl.futurecollars.invoice.service.company

import pl.futurecollars.invoice.db.Database
import spock.lang.Specification

import static pl.futurecollars.invoice.TestHelpers.company

class CompanyServiceUnitTest extends Specification {

    private CompanyService service
    private Database database

    def setup() {
        database = Mock()
        service = new CompanyService(database)
    }

    def "calling saveCompany() should delegate to database save() method"() {
        given:
        def companyId = 1L
        def company = company(companyId)
        when:
        service.saveCompany(company)
        then:
        1 * database.save(company)
    }

    def "calling deleteCompany() should return false if company does not exist in database"() {
        given:
        def companyId = 1L
        database.findById(companyId) >> Optional.empty()
        when:
        def result = service.deleteCompany(companyId)
        then:
        !result
    }

    def "calling deleteCompany() should return true if company exists in database"() {
        given:
        def companyId = 1L
        def company = company(companyId)
        database.findById(companyId) >> Optional.of(company)
        when:
        def result = service.deleteCompany(companyId)
        then:
        result
    }

    def "calling findCompanyById() should delegate to database findById() method"() {
        given:
        def companyId = 21L
        when:
        service.findCompanyById(companyId)
        then:
        1 * database.findById(companyId)
    }

    def "calling getAllCompanies() should delegate to database getAll() method"() {
        when:
        service.getAllCompanies()
        then:
        1 * database.getAll()
    }

    def "calling updateCompany() should return true if company to update exists in database"() {
        given:
        def companyId = 1L
        def company = company(companyId)
        database.findById(companyId) >> Optional.of(company)
        when:
        def result = service.updateCompany(companyId, company)
        then:
        result
    }

    def "calling updateCompany() should return false if company to update does not exist in database"() {
        given:
        def companyId = 1L
        def company = company(companyId)
        database.findById(companyId) >> Optional.empty()
        when:
        def result = service.updateCompany(companyId, company)
        then:
        !result
    }

}

