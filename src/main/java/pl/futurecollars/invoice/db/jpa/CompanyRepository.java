package pl.futurecollars.invoice.db.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoice.model.Company;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {
}
