package pl.futurecollars.invoice.util

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
        IdService idService = new IdService(idPath, filesService)

        expect:
        1L == idService.getCurrentIdAndIncrement()

        and:
        2L == idService.getCurrentIdAndIncrement()

        and:
        3L == idService.getCurrentIdAndIncrement()
    }

    def "current id from last number if file was not empty"() {
        given:
        createFile(idPath)
        Files.writeString(idPath, "16")

        var filesService = new FilesService()
        IdService idService = new IdService(idPath, filesService)

        expect:
        17L == idService.getCurrentIdAndIncrement()

        and:
        18L == idService.getCurrentIdAndIncrement()

        and:
        19L == idService.getCurrentIdAndIncrement()

        cleanup:
        deleteTestFile(idPath)
    }

    def "current id if it isn't file"() {
        given:
        IdService idService = new IdService(idPath, filesService)

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
        IdService idService = new IdService(idPath, filesService)

        then:
        idService != null
    }

    def "should throw exception when initializing Id file fails"() {
        given:
        filesService.createFile("test_db/nextId.txt") >> idPath
        filesService.writeToFile(idPath,"1") >> {throw new IOException()}

        when:
        new IdService(idPath, filesService).getCurrentIdAndIncrement()

        then:
        def exception = thrown(RuntimeException)
        exception.message == "Unable to initialize repository"
    }

    def createFile(Path path) {
        Files.createDirectories(path.getParent())
        Files.createFile(path)
    }

    def deleteTestFile(Path path) {
        Files.deleteIfExists(path)
        Files.deleteIfExists(path.getParent())

    }

    def cleanup() {
        deleteTestFile(idPath)
    }

}
