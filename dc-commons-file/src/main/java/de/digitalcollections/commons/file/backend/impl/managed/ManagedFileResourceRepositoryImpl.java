package de.digitalcollections.commons.file.backend.impl.managed;

import de.digitalcollections.commons.file.backend.impl.FileResourceRepositoryImpl;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * A binary repository using filesystem.
 * see http://docs.oracle.com/javase/tutorial/essential/io/fileio.html
 * see https://docs.oracle.com/javase/tutorial/essential/io/file.html
 * see https://medium.com/eonian-technologies/file-name-hashing-creating-a-hashed-directory-structure-eabb03aa4091
 */
@Repository
public class ManagedFileResourceRepositoryImpl extends FileResourceRepositoryImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(ManagedFileResourceRepositoryImpl.class);

  private final ManagedFileResourceRepositoryConfig managedFileResourcesConfig;

  /**
   * Convert "Thequickbrownfoxjumps" to String[] {"Theq","uick","brow","nfox","jump","s"}
   *
   * @param text text to split
   * @param partLength length of parts
   * @return array of text parts
   */
  protected static String[] splitEqually(String text, int partLength) {
    if (StringUtils.isEmpty(text) || partLength == 0) {
      return new String[] {text};
    }
    
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

  @Autowired
  public ManagedFileResourceRepositoryImpl(ManagedFileResourceRepositoryConfig managedFileResourcesConfig, ResourceLoader resourceLoader) {
    this.managedFileResourcesConfig = managedFileResourcesConfig;
    this.resourceLoader = resourceLoader;
  }

  @Override
  public FileResource create() {
    FileResource resource = new FileResourceImpl();
    return resource;
  }

  public FileResource create(String filename) {
    return create(MimeType.fromFilename(filename), filename);
  }

  public FileResource create(MimeType mimeType, String filename) {
    FileResource resource = create();
    if (mimeType == null) {
      mimeType = MimeType.fromFilename(filename);
    }
    if (mimeType == null) {
      mimeType = MimeType.MIME_APPLICATION_OCTET_STREAM;
    }
    resource.setMimeType(mimeType);
    resource.setReadonly(false);
    final UUID uuid = UUID.randomUUID();
    resource.setUuid(uuid);
    resource.setFilename(filename);

    URI uri = createUri(uuid, mimeType, filename);
    resource.setUri(uri);

    // TODO if filename == null extract uuid-filename from uri and set it
    return resource;
  }

  public FileResource create(UUID uuid) {
    FileResource resource = create();
    resource.setReadonly(false);
    resource.setUuid(uuid);
    return resource;
  }

  protected URI createUri(@Nonnull UUID uuid, MimeType mimeType, String filename) {
    Objects.requireNonNull(uuid, "uuid must not be null");

    final String uuidStr = uuid.toString();
    String uuidPath = getSplittedUuidPath(uuidStr);
    Path path = Paths.get(managedFileResourcesConfig.getFolderpath(), managedFileResourcesConfig.getNamespace(), uuidPath, uuidStr);
    String location = "file://" + path.toString();

    if (filename != null) {
      location = location + "_" + filename;
      // example location = file:///mnt/repository/dico/a30c/f362/5992/4f5a/8de0/6193/8134/e721/a30cf362-5992-4f5a-8de0-61938134e721_test.xml
    } else if (mimeType != null && !mimeType.getExtensions().isEmpty()) {
      location = location + "." + mimeType.getExtensions().get(0);
      // example location = file:///mnt/repository/dico/a30c/f362/5992/4f5a/8de0/6193/8134/e721/a30cf362-5992-4f5a-8de0-61938134e721.xml
    }
    return URI.create(location);
  }

  public FileResource find(String uuid) throws ResourceIOException, ResourceNotFoundException {
    URI uri = getUri(UUID.fromString(uuid));
    if (!resourceLoader.getResource(uri.toString()).isReadable()) {
      throw new ResourceIOException("File resource at uri " + uri + " is not readable");
    }
    FileResource resource = create(null, null);
    resource.setUri(uri);

    String filename = uri.toString().substring(uri.toString().lastIndexOf("/"));
    resource.setFilename(filename);

    resource.setMimeType(MimeType.fromFilename(filename));

    Resource springResource = resourceLoader.getResource(uri.toString());

    long lastModified = getLastModified(springResource);
    if (lastModified != 0) {
      // lastmodified by code in java.io.File#lastModified (is also used in Spring's core.io.Resource) is in milliseconds!
      resource.setLastModified(Instant.ofEpochMilli(lastModified).atOffset(ZoneOffset.UTC).toLocalDateTime());
    } else {
      resource.setLastModified(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
    }

    long length = getSize(springResource);
    if (length > -1) {
      resource.setSizeInBytes(length);
    }
    return resource;
  }

  @Override
  public FileResource find(String uuid, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException {
    FileResource fileResource = find(uuid);
    if (fileResource != null) {
      if (fileResource.getMimeType() == null) {
        return fileResource;
      }
      if (!fileResource.getMimeType().equals(mimeType)) {
        throw new ResourceNotFoundException("Mimetype " + fileResource.getMimeType() + " does not match requested mimetype " + mimeType);
      }
    }
    return fileResource;
  }

  protected String getSplittedUuidPath(String uuid) {
    String uuidWithoutDashes = uuid.replaceAll("-", "");
    String[] pathParts = splitEqually(uuidWithoutDashes, 4);
    String splittedUuidPath = String.join(File.separator, pathParts);
    return splittedUuidPath;
  }

  protected URI getUri(@Nonnull UUID uuid) throws ResourceNotFoundException {
    Objects.requireNonNull(uuid, "uuid must not be null");

    final String uuidStr = uuid.toString();
    String uuidPath = getSplittedUuidPath(uuidStr);
    Path path = Paths.get(managedFileResourcesConfig.getFolderpath(), managedFileResourcesConfig.getNamespace(), uuidPath);
    String location = "file://" + path.toString();

    File directory = path.toFile();
    if (!directory.isDirectory()) {
      throw new ResourceNotFoundException(path.toString() + " does not exist");
    }
    // create new filename filter
    FilenameFilter fileNameFilter = (File dir, String name) -> {
      return name.startsWith(uuidStr);
    };

    File[] matchingFiles = directory.listFiles(fileNameFilter);
    if (matchingFiles.length > 0) {
      File file = matchingFiles[0];
      String filename = file.getName();
      location = location + "_" + filename;
      return URI.create(location);
    }
    throw new ResourceNotFoundException("No matching file found in " + path.toString());
  }

}
