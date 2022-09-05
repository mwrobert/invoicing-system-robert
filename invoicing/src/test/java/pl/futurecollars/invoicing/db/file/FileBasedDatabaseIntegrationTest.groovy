package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.db.AbstractDatabaseTest
import pl.futurecollars.invoicing.utils.FilesService
import pl.futurecollars.invoicing.utils.IdService
import pl.futurecollars.invoicing.utils.JsonService

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static pl.futurecollars.invoicing.TestHelpers.invoice

class FileBasedDatabaseIntegrationTest extends AbstractDatabaseTest {

    private final Path databasePath = Paths.get(Configuration.DATABASE_FILE)
    private final Path idPath = Paths.get(Configuration.ID_FILE)

    def setup() {
        database = new FileBasedDatabase(databasePath, new FilesService(), new JsonService(), new IdService(idPath, new FilesService()))
    }

    def "should file based database writes invoices to correct file"() {
        when:
        database.save(invoice(4L))

        then:
        1 == Files.readAllLines(databasePath).size()

        when:
        database.save(invoice(5L))

        then:
        2 == Files.readAllLines(databasePath).size()
    }

    def cleanup() {
        Files.deleteIfExists(idPath)
        Files.deleteIfExists(databasePath)
    }

}