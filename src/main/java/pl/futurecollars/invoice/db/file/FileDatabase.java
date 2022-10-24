package pl.futurecollars.invoice.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoice.db.Database;
import pl.futurecollars.invoice.model.WithId;
import pl.futurecollars.invoice.utils.FilesService;
import pl.futurecollars.invoice.utils.IdService;
import pl.futurecollars.invoice.utils.JsonService;

@AllArgsConstructor
public class FileDatabase<T extends WithId> implements Database<T> {

  private final Path databasePath;
  private final FilesService filesService;
  private final JsonService jsonService;
  private final IdService idService;
  private final Class<T> clazz;

  @Override
  public long save(T item) {
    long currentId = idService.getCurrentIdAndIncrement();
    item.setId(currentId);
    try {
      filesService.appendLineToFile(databasePath, jsonService.toJson(item));
    } catch (IOException e) {
      throw new RuntimeException("Problem save item to database repository", e);
    }
    return currentId;
  }

  @Override
  public Optional<T> findById(long id) {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .filter(line -> containsId(line, id))
          .map(line -> jsonService.toObject(line, clazz))
          .findFirst();
    } catch (IOException ex) {
      throw new RuntimeException("Database failed to get item with id: " + id, ex);
    }
  }

  @Override
  public Optional<T> update(long id, T updatedItem) {
    try {
      List<String> allItems = filesService.readAllLines(databasePath);
      final String itemAsJson = allItems.stream().filter(line -> containsId(line, id)).findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Id " + id + " does not exist"));

      allItems.remove(itemAsJson);
      updatedItem.setId(id);
      allItems.add(jsonService.toJson(updatedItem));
      filesService.writeLinesToFile(databasePath, allItems);

      return Optional.of(updatedItem);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to update item with id: " + id);
    }
  }

  @Override
  public Optional<T> delete(long id) {
    Optional<T> itemOptional = findById(id);
    try {
      var itemExceptDeleted = filesService.readAllLines(databasePath)
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());

      filesService.writeLinesToFile(databasePath, itemExceptDeleted);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to delete item with id: " + id, ex);
    }
    return itemOptional;
  }

  private boolean containsId(String line, long id) {
    return line.contains("\"id\":" + id + ",");
  }

  @Override
  public List<T> getAll() {
    try {
      return filesService.readAllLines(databasePath).stream()
          .map(line -> jsonService.toObject(line, clazz))
          .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new RuntimeException("Failed to load all items", ex);
    }
  }

}
