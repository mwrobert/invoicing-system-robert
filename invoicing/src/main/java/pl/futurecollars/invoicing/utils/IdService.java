package pl.futurecollars.invoicing.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IdService {

  private final FilesService filesService;
  private final Path idPath;
  private long currentId;

  public IdService(Path idPath, FilesService filesService) {
    this.filesService = filesService;
    this.idPath = idPath;
    this.currentId = 0L;
    initialize();
  }

  private void initialize() {
    try {
      if (Files.exists(idPath)) {
        currentId = Long.parseLong(filesService.readLine(idPath));
      } else {
        filesService.createFile(idPath.toString());
        filesService.writeToFile(idPath, String.valueOf(0L));
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize id path", e);
    }
  }

  public long getCurrentIdAndIncrement() {
    try {
      currentId++;
      filesService.writeToFile(idPath, String.valueOf(currentId));
    } catch (IOException e) {
      throw new RuntimeException("Unable to get current ID", e);
    }
    return currentId;
  }

}
