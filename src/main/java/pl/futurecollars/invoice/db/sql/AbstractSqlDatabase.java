package pl.futurecollars.invoice.db.sql;

import java.sql.PreparedStatement;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import pl.futurecollars.invoice.model.Company;

@AllArgsConstructor
public class AbstractSqlDatabase {

  protected final JdbcTemplate jdbcTemplate;

  protected long insertCompany(Company company) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into companies (name, address, tax_identification_number, health_insurance, pension_insurance) values (?, ?, ?, ?, ?);",
          new String[] {"id"});
      ps.setString(1, company.getName());
      ps.setString(2, company.getAddress());
      ps.setString(3, company.getTaxIdentificationNumber());
      ps.setBigDecimal(4, company.getHealthInsurance());
      ps.setBigDecimal(5, company.getPensionInsurance());
      return ps;
    }, keyHolder);
    return Objects.requireNonNull(keyHolder.getKey()).longValue();
  }

  protected void updateCompany(Company updatedCompany, Company originalCompany) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "update companies "
              + "set tax_identification_number=?, "
              + "address=?, "
              + "name=?, "
              + "health_insurance=?, "
              + "pension_insurance=? "
              + "where id=?"
      );
      ps.setString(1, updatedCompany.getTaxIdentificationNumber());
      ps.setString(2, updatedCompany.getAddress());
      ps.setString(3, updatedCompany.getName());
      ps.setBigDecimal(4, updatedCompany.getHealthInsurance());
      ps.setBigDecimal(5, updatedCompany.getPensionInsurance());
      ps.setLong(6, originalCompany.getId());
      return ps;
    });
  }
}
