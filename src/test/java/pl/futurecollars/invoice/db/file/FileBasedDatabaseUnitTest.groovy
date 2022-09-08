package pl.futurecollars.invoice.db.file

import pl.futurecollars.invoice.TestHelpers
import pl.futurecollars.invoice.db.Database
import pl.futurecollars.invoice.model.Invoice
import pl.futurecollars.invoice.utils.FilesService
import pl.futurecollars.invoice.utils.IdService
import pl.futurecollars.invoice.utils.JsonService
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

import static pl.futurecollars.invoice.TestHelpers.invoice

class FileBasedDatabaseUnitTest extends Specification {

    private final Path databasePath = Paths.get(Configuration.DATABASE_FILE)
    private final Path idPath = Paths.get(Configuration.ID_FILE)
    private final FilesService filesServiceMock = Mock(FilesService)
    private final JsonService jsonServiceMock = Mock(JsonService)
    private final IdService idService = Mock(IdService)

    private Database database

    def setup() {
        database = new FileBasedDatabase(databasePath, idPath, filesServiceMock, jsonServiceMock, idService)

    }

    def "should throw exception when appendLineToFile() in save() fails"() {
        given:
        filesServiceMock.appendLineToFile(databasePath, jsonServiceMock.toJson(invoice(1L))) >> { throw new IOException() }
        when:
        database.save(invoice(1L))
        then:
        def exception = thrown(RuntimeException)
        exception.getMessage().equalsIgnoreCase("Problem save invoice to database repository")
    }

    def "should throw exception when readAllLines() in update() fails"() {
        given:
        def invoice = invoice(4)
        invoice.setId(1L)

        def json = new JsonService().toJson(invoice)
        filesServiceMock.readAllLines(databasePath) >> { throw new IOException() }
        jsonServiceMock.toObject(json, Invoice.class) >> invoice
        when:
        database.update(1L, TestHelpers.invoice(3))
        then:
        def exception = thrown(RuntimeException)
        exception.getMessage().equalsIgnoreCase("Failed to update invoice with id: 1")
    }

    def "should throw exception when readAllLine() in findById fails"() {
        given:
        database.save(invoice(1L))
        filesServiceMock.readAllLines(databasePath) >> { throw new IOException() }
        when:
        database.findById(1L)
        then:
        def exception = thrown(RuntimeException)
        exception.message == "Database failed to get invoice with id: 1"
    }

    def "should throw exception when readAllLine() in getAll() fails"() {
        given:
        database.save(invoice(1L))
        filesServiceMock.readAllLines(databasePath) >> { throw new IOException() }
        when:
        database.getAll()
        then:
        def exception = thrown(RuntimeException)
        exception.message == "Failed to load all invoices"
    }

}