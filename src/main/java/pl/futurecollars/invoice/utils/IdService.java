package pl.futurecollars.invoice.utils;

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
    try {
      if (Files.exists(idPath)) {
        String idLine = filesService.readLine(idPath);
        if (!idLine.isEmpty()) {
          currentId = Long.parseLong(idLine);
        } else {
          filesService.writeToFile(idPath, String.valueOf(currentId));
        }
      } else {
        filesService.createFile(idPath.toString());
        filesService.writeToFile(idPath, String.valueOf(currentId));
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize IdService", e);
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
