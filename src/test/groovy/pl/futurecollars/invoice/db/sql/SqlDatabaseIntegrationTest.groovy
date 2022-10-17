package pl.futurecollars.invoice.db.sql

import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import pl.futurecollars.invoice.db.AbstractDatabaseTest
import pl.futurecollars.invoice.db.Database

class SqlDatabaseIntegrationTest extends AbstractDatabaseTest {
    @Override
    Database getDatabaseInstance() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource)

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .cleanDisabled(false)
                .load()

        flyway.clean()
        flyway.migrate()

        def database = new SqlDatabase(jdbcTemplate)
        database

    }
}
