package pl.futurecollars.invoice.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FilesService {

  public Path createFile(String idPath) throws IOException {
    File file = new File(idPath);
    file.getParentFile().mkdirs();
    file.createNewFile();
    return Path.of(file.getPath());

  }

  public void appendLineToFile(Path path, String line) throws IOException {
    Files.write(path, (line + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
  }

  public void writeToFile(Path path, String line) throws IOException {
    Files.write(path, line.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
  }

  public void writeLinesToFile(Path path, List<String> lines) throws IOException {
    Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
  }

  public List<String> readAllLines(Path path) throws IOException {
    return Files.readAllLines(path);
  }

  public String readLine(Path path) throws IOException {
    return Files.readString(path);
  }

}
