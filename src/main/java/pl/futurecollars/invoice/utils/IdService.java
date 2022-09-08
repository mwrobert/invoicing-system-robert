package pl.futurecollars.invoice.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IdService {

  private final FilesService filesService;
  private final Path idPath;
  private final Path databasePath;

  private long currentId;

  public IdService(Path databasePath, Path idPath, FilesService filesService) {
    this.filesService = filesService;
    this.databasePath = databasePath;
    this.idPath = idPath;
    this.currentId = 0L;
    try {
      if (Files.exists(idPath)) {
        currentId = Long.parseLong(filesService.readLine(idPath));
      } else {
        filesService.createFile(databasePath.toString());
        filesService.createFile(idPath.toString());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public long getCurrentIdAndIncrement() {
    try {
      currentId++;
      filesService.writeToFile(idPath, String.valueOf(currentId));
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize repository", e);
    }
    return currentId;
  }

}
