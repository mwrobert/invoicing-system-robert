package pl.futurecollars.invoice.db.file

import pl.futurecollars.invoice.db.AbstractDatabaseTest
import pl.futurecollars.invoice.db.Database
import pl.futurecollars.invoice.utils.FilesService
import pl.futurecollars.invoice.utils.IdService
import pl.futurecollars.invoice.utils.JsonService

import java.nio.file.Files
import java.nio.file.Path

import static pl.futurecollars.invoice.TestHelpers.invoice

class FileRepositoryIntegrationTest extends AbstractDatabaseTest {

    Path databasePath

    @Override
    Database getDatabaseInstance() {
        def filesService = new FilesService()
        def idPath = File.createTempFile("nextId",".txt").toPath()
        def idService = new IdService(idPath, filesService)
        def jsonService = new JsonService()
        databasePath = File.createTempFile("testInvoices",".json").toPath()
        new FileRepository(databasePath,filesService,jsonService,idService)
    }

    def "should file based database writes invoices to correct file"() {
        given:
        def database = getDatabaseInstance()

        when:
        database.save(invoice(4L))

        then:
        1 == Files.readAllLines(databasePath).size()

        when:
        database.save(invoice(5L))

        then:
        2 == Files.readAllLines(databasePath).size()
    }

}
