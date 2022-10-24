package pl.futurecollars.invoice.service.company;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Company;

@Service
public class CompanyService {

  private final Database<Company> companyDatabase;

  public CompanyService(Database<Company> companyDatabase) {
    this.companyDatabase = companyDatabase;
  }

  public long saveCompany(Company company) {
    return companyDatabase.save(company);
  }

  public Optional<Company> findCompanyById(long id) {
    return companyDatabase.findById(id);
  }

  public boolean updateCompany(long id, Company company) {
    var companyToUpdate = companyDatabase.findById(id);
    if (companyToUpdate.isPresent()) {
      companyDatabase.update(id, company);
    }
    return companyToUpdate.isPresent();
  }

  public boolean deleteCompany(long id) {
    var companyToDelete = companyDatabase.findById(id);
    if (companyToDelete.isPresent()) {
      companyDatabase.delete(id);
    }
    return companyToDelete.isPresent();
  }

  public List<Company> getAllCompanies() {
    return companyDatabase.getAll();
  }

}

