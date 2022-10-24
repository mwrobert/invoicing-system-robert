package pl.futurecollars.invoice.controller.company;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.service.company.CompanyService;

@RestController
public class CompanyController implements CompanyApi {

  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @Override
  public ResponseEntity<Company> getCompany(@PathVariable long id) {
    return ResponseEntity.of(companyService.findCompanyById(id));
  }

  @Override
  public List<Company> getAll() {
    return companyService.getAllCompanies();
  }

  @Override
  public long saveCompany(@RequestBody Company company) {
    return companyService.saveCompany(company);
  }

  @Override
  public ResponseEntity<?> deleteCompany(@PathVariable long id) {
    if (this.companyService.deleteCompany(id)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<?> updateCompany(@RequestBody Company company, @PathVariable long id) {
    if (this.companyService.updateCompany(id, company)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
