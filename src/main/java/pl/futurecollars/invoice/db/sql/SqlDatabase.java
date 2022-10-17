package pl.futurecollars.invoice.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.Car;
import pl.futurecollars.invoice.model.Company;
import pl.futurecollars.invoice.model.Invoice;
import pl.futurecollars.invoice.model.InvoiceEntry;
import pl.futurecollars.invoice.model.Vat;

@AllArgsConstructor
public class SqlDatabase implements Database {

  private static final String SELECT_INVOICES_QUERY = "select i.id, i.date, i.number, "
      + "c1.id as seller_id, c1.name as seller_name, c1.tax_identification_number as seller_tax_id, c1.address as seller_address, "
      + "c1.pension_insurance as seller_pension_insurance, c1.health_insurance as seller_health_insurance, "
      + "c2.id as buyer_id, c2.name as buyer_name, c2.tax_identification_number as buyer_tax_id, c2.address as buyer_address, "
      + "c2.pension_insurance as buyer_pension_insurance, c2.health_insurance as buyer_health_insurance "
      + "from invoices i "
      + "inner join companies c1 on i.seller = c1.id "
      + "inner join companies c2 on i.buyer = c2.id";

  private JdbcTemplate jdbcTemplate;
  private GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

  public SqlDatabase(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  @Transactional
  public long save(Invoice invoice) {

    long buyerId = insertCompany(invoice.getBuyer());
    long sellerId = insertCompany(invoice.getSeller());
    long invoiceId = insertInvoice(invoice, buyerId, sellerId);
    addEntriesRelatedToInvoice(invoiceId, invoice);
    return invoiceId;
  }

  private long insertInvoiceEntries(InvoiceEntry entry) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection
          .prepareStatement(
              "insert into invoice_entries (description, quantity, net_price, vat_value, vat_rate, expense_related_to_car) "
                  + "values (?, ?, ?, ?, ?, ?);",
              new String[] {"id"});
      ps.setString(1, entry.getDescription());
      ps.setBigDecimal(2, entry.getQuantity());
      ps.setBigDecimal(3, entry.getNetPrice());
      ps.setBigDecimal(4, entry.getVatValue());
      ps.setString(5, entry.getVatRate().name());
      ps.setObject(6, insertCarAndGetItId(entry.getExpenseRelatedToCar()));
      return ps;
    }, keyHolder);
    return Objects.requireNonNull(keyHolder.getKey()).longValue();
  }

  private void insertInvoiceInvoiceEntry(long invoiceId, long invoiceEntryId) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into invoice_invoice_entry (invoice_id, invoice_entry_id) values (?, ?);");
      ps.setLong(1, invoiceId);
      ps.setLong(2, invoiceEntryId);
      return ps;
    });
  }

  private int insertInvoice(Invoice invoice, long buyerId, long sellerId) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps =
          connection.prepareStatement("insert into invoices (date, number, buyer, seller) values (?, ?, ?, ?);", new String[] {"id"});
      ps.setDate(1, Date.valueOf(invoice.getDate()));
      ps.setString(2, invoice.getNumber());
      ps.setLong(3, buyerId);
      ps.setLong(4, sellerId);
      return ps;
    }, keyHolder);
    return Objects.requireNonNull(keyHolder.getKey()).intValue();
  }

  private long insertCompany(Company buyer) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into companies (name, address, tax_identification_number, health_insurance, pension_insurance) values (?, ?, ?, ?, ?);",
          new String[] {"id"});
      ps.setString(1, buyer.getName());
      ps.setString(2, buyer.getAddress());
      ps.setString(3, buyer.getTaxIdentificationNumber());
      ps.setBigDecimal(4, buyer.getHealthInsurance());
      ps.setBigDecimal(5, buyer.getPensionInsurance());
      return ps;
    }, keyHolder);
    return Objects.requireNonNull(keyHolder.getKey()).longValue();
  }

  private Integer insertCarAndGetItId(Car car) {
    if (car == null) {
      return null;
    }
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection
          .prepareStatement(
              "insert into cars (registration_number, personal_user) values (?, ?);",
              new String[] {"id"});
      ps.setString(1, car.getRegistrationNumber());
      ps.setBoolean(2, car.isPersonalUse());
      return ps;
    }, keyHolder);
    return Objects.requireNonNull(keyHolder.getKey()).intValue();
  }

  @Override
  public Optional<Invoice> findById(long id) {
    List<Invoice> invoices = jdbcTemplate.query(SELECT_INVOICES_QUERY + " where i.id = " + id, invoiceRowMapper());
    return invoices.isEmpty() ? Optional.empty() : Optional.of(invoices.get(0));
  }

  @Override
  @Transactional
  public Optional<Invoice> update(long id, Invoice updatedInvoice) {
    Optional<Invoice> originalInvoice = findById(id);
    if (originalInvoice.isPresent()) {
      updateInvoiceData(updatedInvoice, id);
      updateCompany(updatedInvoice.getBuyer(), originalInvoice.get().getBuyer());
      updateCompany(updatedInvoice.getSeller(), originalInvoice.get().getSeller());
      deleteCarsRelatedToInvoice(id);
      deleteEntriesRelatedToInvoice(id);
      addEntriesRelatedToInvoice(id, updatedInvoice);
    }
    return originalInvoice;
  }

  private void updateInvoiceData(Invoice updatedInvoice, long originalInvoiceId) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "update invoices "
              + "set date=?, "
              + "number=? "
              + "where id=?"
      );
      ps.setDate(1, Date.valueOf(updatedInvoice.getDate()));
      ps.setString(2, updatedInvoice.getNumber());
      ps.setLong(3, originalInvoiceId);
      return ps;
    });
  }

  private void updateCompany(Company updatedCompany, Company originalCompany) {
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

  private void addEntriesRelatedToInvoice(long invoiceId, Invoice invoice) {
    for (InvoiceEntry entry : invoice.getInvoiceEntries()) {
      long invoiceEntryId = insertInvoiceEntries(entry);
      insertInvoiceInvoiceEntry(invoiceId, invoiceEntryId);
    }
  }

  @Override
  @Transactional
  public Optional<Invoice> delete(long id) {
    Optional<Invoice> invoiceOptional = findById(id);
    if (invoiceOptional.isPresent()) {
      deleteCarsRelatedToInvoice(id);
      deleteEntriesRelatedToInvoice(id);
      deleteInvoice(id);
      deleteCompaniesRelatedToInvoice(invoiceOptional.get());
    }
    return invoiceOptional;
  }

  private void deleteCarsRelatedToInvoice(long id) {
    jdbcTemplate.update(connection -> { // warn: makes use of delete cascade
      PreparedStatement ps = connection.prepareStatement("delete from cars where id in ("
          + "select expense_related_to_car from invoice_entries where id in ("
          + "select invoice_entry_id from invoice_invoice_entry where invoice_id=?));");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteEntriesRelatedToInvoice(long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from invoice_entries where id in (select invoice_entry_id from invoice_invoice_entry where invoice_id=?);");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteInvoice(long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from invoices where id = ?;");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteCompaniesRelatedToInvoice(Invoice invoice) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from companies where id in (?, ?);");
      ps.setLong(1, invoice.getBuyer().getId());
      ps.setLong(2, invoice.getSeller().getId());
      return ps;
    });
  }

  @Override
  public List<Invoice> getAll() {
    return jdbcTemplate.query(SELECT_INVOICES_QUERY, invoiceRowMapper());
  }

  private RowMapper<Invoice> invoiceRowMapper() {
    return (rs, rowNr) -> {
      int invoiceId = rs.getInt("id");

      List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
          "select * from invoice_invoice_entry iie "
              + "inner join invoice_entries e on iie.invoice_entry_id = e.id "
              + "left outer join cars c on e.expense_related_to_car = c.id "
              + "where invoice_id = " + invoiceId,
          (response, ignored) -> InvoiceEntry.builder()
              .description(response.getString("description"))
              .quantity(response.getBigDecimal("quantity"))
              .netPrice(response.getBigDecimal("net_price"))
              .vatValue(response.getBigDecimal("vat_value"))
              .vatRate(Vat.valueOf(response.getString("vat_rate")))
              .expenseRelatedToCar(response.getObject("registration_number") != null
                  ? Car.builder()
                  .registrationNumber(response.getString("registration_number"))
                  .personalUse(response.getBoolean("personal_user"))
                  .build()
                  : null)
              .build());

      return Invoice.builder()
          .id(rs.getInt("id"))
          .date(rs.getDate("date").toLocalDate())
          .number(rs.getString("number"))
          .buyer(Company.builder()
              .id(rs.getInt("buyer_id"))
              .taxIdentificationNumber(rs.getString("buyer_tax_id"))
              .name(rs.getString("buyer_name"))
              .address(rs.getString("buyer_address"))
              .pensionInsurance(rs.getBigDecimal("buyer_pension_insurance"))
              .healthInsurance(rs.getBigDecimal("buyer_health_insurance"))
              .build()
          )
          .seller(Company.builder()
              .id(rs.getInt("seller_id"))
              .taxIdentificationNumber(rs.getString("seller_tax_id"))
              .name(rs.getString("seller_name"))
              .address(rs.getString("seller_address"))
              .pensionInsurance(rs.getBigDecimal("seller_pension_insurance"))
              .healthInsurance(rs.getBigDecimal("seller_health_insurance"))
              .build()
          )
          .invoiceEntries(invoiceEntries)
          .build();
    };
  }

}
