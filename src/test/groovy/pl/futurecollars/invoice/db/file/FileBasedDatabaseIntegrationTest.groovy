package pl.futurecollars.invoice.db.file

import pl.futurecollars.invoice.db.AbstractDatabaseTest
import pl.futurecollars.invoice.utils.FilesService
import pl.futurecollars.invoice.utils.IdService
import pl.futurecollars.invoice.utils.JsonService

import java.nio.file.Files
import java.nio.file.Path

import static pl.futurecollars.invoice.TestHelpers.invoice

class FileBasedDatabaseIntegrationTest extends AbstractDatabaseTest {

    private final databasePath = Path.of("test_db/invoices.json")
    private final idPath = Path.of("test_db/nextId.txt")

    def setup() {
        def filesService = new FilesService()
        def idService = new IdService(idPath, filesService)
        def jsonService = new JsonService()

        filesService.createFile(databasePath.toString())
        filesService.createFile(idPath.toString())
        database = new FileRepository(databasePath, filesService, jsonService, idService)
    }

    def "should file based database writes invoices to correct file"() {
        when:
        database.save(invoice(4L))

        then:
        1 == Files.readAllLines(databasePath as Path).size()

        when:
        database.save(invoice(5L))

        then:
        2 == Files.readAllLines(databasePath).size()
    }

    def cleanup() {
        Files.deleteIfExists(idPath)
        Files.deleteIfExists(databasePath)
        Files.deleteIfExists(idPath.getParent())
        Files.deleteIfExists(databasePath.getParent())
    }

}
