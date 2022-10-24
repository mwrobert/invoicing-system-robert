package pl.futurecollars.invoice.db.sql;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Company;

public class CompanySqlDatabase extends AbstractSqlDatabase implements Database<Company> {

  private static final String SELECT_COMPANY_QUERY = "SELECT * FROM companies ";

  private GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

  public CompanySqlDatabase(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  @Override
  @Transactional
  public long save(Company company) {
    return insertCompany(company);
  }

  @Override
  public Optional<Company> findById(long id) {
    List<Company> companies = jdbcTemplate.query(SELECT_COMPANY_QUERY + "WHERE id = " + id, companyRowMapper());
    return companies.isEmpty() ? Optional.empty() : Optional.of(companies.get(0));
  }

  @Override
  public List<Company> getAll() {
    return jdbcTemplate.query(SELECT_COMPANY_QUERY, companyRowMapper());
  }

  @Override
  @Transactional
  public Optional<Company> update(long id, Company updatedCompany) {
    Optional<Company> originalCompany = findById(id);
    originalCompany.ifPresent(company -> updateCompany(updatedCompany, company));
    return originalCompany;
  }

  @Override
  @Transactional
  public Optional<Company> delete(long id) {
    Optional<Company> originalCompany = findById(id);
    if (originalCompany.isPresent()) {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
            "DELETE FROM companies WHERE id = ?;");
        ps.setLong(1, id);
        return ps;
      });
    }
    return originalCompany;
  }

  private RowMapper<Company> companyRowMapper() {
    return (rs, rowNr) ->
        Company.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .address(rs.getString("address"))
            .taxIdentificationNumber(rs.getString("tax_identification_number"))
            .healthInsurance(rs.getBigDecimal("health_insurance"))
            .pensionInsurance(rs.getBigDecimal("pension_insurance"))
            .build();
  }
}
