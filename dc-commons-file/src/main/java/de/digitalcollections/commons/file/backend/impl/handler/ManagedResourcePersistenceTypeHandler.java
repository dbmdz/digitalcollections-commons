package de.digitalcollections.commons.file.backend.impl.handler;

import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ManagedResourcePersistenceTypeHandler implements ResourcePersistenceTypeHandler {

  @Value("${resourceRepository.managedPathFactory.namespace}")
  private String namespace;

  private String repositoryFolderPath;

  @Value("${resourceRepository.managedPathFactory.folderpath}")
  public void setRepositoryFolderPath(String path) {
    this.repositoryFolderPath = path.replace("~", System.getProperty("user.home"));
  }

  /**
   * minimum ":"-separators is one, because of namespace in id is required
   *
   * @param key the resource key (manged resource key is an uuid)
   * @param mimeType the MIME type of the resource (e.g. "application/xml")
   * @return All URIs that could be resolved from the parameters
   * @throws ResourceIOException
   */
  // key = c30cf362-5992-4f5a-8de0-61938134e721, mimetype = image/jpeg
  // /local/repository/bsb/c30c/f362/5992/4f5a/8de0/6193/8134/e721/c30cf362-5992-4f5a-8de0-61938134e721.jpg
  @Override
  public List<URI> getUris(String key, MimeType mimeType) throws ResourceIOException {
    String uuidPath = getSplittedUuidPath(key);
    Path path = Paths.get(this.getRepositoryFolderPath(), this.getNamespace(), uuidPath, key);
    String location = path.toString();
    location = location + "." + mimeType.getExtensions().get(0);
    return Collections.singletonList(URI.create(location));
  }

  private String getNamespace() {
    return namespace;
  }

  private String getRepositoryFolderPath() {
    return repositoryFolderPath;
  }

  @Override
  public FileResourcePersistenceType getResourcePersistenceType() {
    return FileResourcePersistenceType.MANAGED;
  }

  protected String getSplittedUuidPath(String uuid) {
    String uuidWithoutDashes = uuid.replaceAll("-", "");
    String[] pathParts = splitEqually(uuidWithoutDashes, 4);
    String splittedUuidPath = String.join(File.separator, pathParts);
    return splittedUuidPath;
  }

  /**
   * Convert "Thequickbrownfoxjumps" to String[] {"Theq","uick","brow","nfox","jump","s"}
   *
   * @param text text to split
   * @param partLength length of parts
   * @return array of text parts
   */
  protected static String[] splitEqually(String text, int partLength) {
    int textLength = text.length();

    // Number of parts
    int numberOfParts = (textLength + partLength - 1) / partLength;
    String[] parts = new String[numberOfParts];

    // Break into parts
    int offset = 0;
    int i = 0;
    while (i < numberOfParts) {
      parts[i] = text.substring(offset, Math.min(offset + partLength, textLength));
      offset += partLength;
      i++;
    }

    return parts;
  }
}
