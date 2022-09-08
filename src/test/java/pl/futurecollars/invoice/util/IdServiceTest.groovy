package pl.futurecollars.invoice.util

import pl.futurecollars.invoice.db.file.Configuration
import pl.futurecollars.invoice.utils.FilesService
import pl.futurecollars.invoice.utils.IdService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class IdServiceTest extends Specification {

    private final Path databasePath = Paths.get(Configuration.DATABASE_FILE)
    private final Path idPath = Paths.get(Configuration.ID_FILE)

    def "current id if file was empty"() {
        given:
        IdService idService = new IdService(databasePath,idPath,new FilesService())

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
        IdService idService = new IdService(databasePath,idPath, new FilesService())

        expect:
        17L == idService.getCurrentIdAndIncrement()

        and:
        18L == idService.getCurrentIdAndIncrement()

        and:
        19L == idService.getCurrentIdAndIncrement()
    }

    def "current id if it isn't file"() {
        given:
        IdService idService = new IdService(databasePath,idPath,new FilesService())

        expect:
        1L == idService.getCurrentIdAndIncrement()

        and:
        2L == idService.getCurrentIdAndIncrement()

        and:
        3L == idService.getCurrentIdAndIncrement()
    }

    def cleanup() {
        Files.deleteIfExists(idPath)
    }

}