package pl.futurecollars.invoice.util

import pl.futurecollars.invoice.db.file.FileDatabaseConfiguration
import pl.futurecollars.invoice.utils.FilesService
import pl.futurecollars.invoice.utils.IdService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class IdServiceTest extends Specification {

    private final idPath = Path.of("test_db/nextId.txt")
    private final FilesService filesService = Mock(FilesService)

    def "current id if file was empty"() {
        given:
        IdService idService = new FileDatabaseConfiguration().idService(new FilesService())

        expect:
        1L == idService.getCurrentIdAndIncrement()

        and:
        2L == idService.getCurrentIdAndIncrement()

        and:
        3L == idService.getCurrentIdAndIncrement()
    }

    def "current id from last number if file was not empty"() {
        given:
        Files.writeString(idPath, "16")
        IdService idService = new IdService(idPath, new FilesService())

        expect:
        17L == idService.getCurrentIdAndIncrement()

        and:
        18L == idService.getCurrentIdAndIncrement()

        and:
        19L == idService.getCurrentIdAndIncrement()
    }

    def "current id if it isn't file"() {
        given:
        IdService idService = new FileDatabaseConfiguration().idService(new FilesService())

        expect:
        1L == idService.getCurrentIdAndIncrement()

        and:
        2L == idService.getCurrentIdAndIncrement()

        and:
        3L == idService.getCurrentIdAndIncrement()
    }

    def "should initialize IdService"() {
        given:
        filesService.createFile("test_db/nextId.txt") >> idPath

        when:
        IdService idService = new FileDatabaseConfiguration().idService(filesService)

        then:
        idService != null
    }

    def "should throw exception when initializing Id file fails"() {
        given:
        filesService.createFile("test_db/nextId.txt") >> idPath
        filesService.writeToFile(idPath,"1") >> {throw new IOException()}

        when:
        new FileDatabaseConfiguration().idService(filesService).getCurrentIdAndIncrement()

        then:
        def exception = thrown(RuntimeException)
        exception.message == "Unable to initialize repository"
    }

    def cleanup() {
        Files.deleteIfExists(idPath)
    }

}