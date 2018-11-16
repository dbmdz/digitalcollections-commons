package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.backend.impl.handler.ResolvedResourcePersistenceTypeHandler;
import de.digitalcollections.commons.file.backend.impl.handler.ResourcePersistenceTypeHandler;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A binary repository using filesystem. see http://docs.oracle.com/javase/tutorial/essential/io/fileio.html see https://docs.oracle.com/javase/tutorial/essential/io/file.html see
 * http://michaelandrews.typepad.com/the_technical_times/2009/10/creating-a-hashed-directory-structure.html
 */
@Repository
public class FileResourceRepositoryImpl implements FileResourceRepository<FileResource> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceRepositoryImpl.class);
  private List<ResourcePersistenceTypeHandler> resourcePersistenceTypeHandlers;
  private final ResourceLoader resourceLoader;
  private DirectoryStream<Path> overriddenDirectoryStream;      // only for testing purposes

  @Autowired
  public FileResourceRepositoryImpl(List<ResourcePersistenceTypeHandler> resourcePersistenceTypeHandlers, ResourceLoader resourceLoader) {
    this.resourcePersistenceTypeHandlers = resourcePersistenceTypeHandlers;
    this.resourceLoader = resourceLoader;
  }

  @Override
  public FileResource create(MimeType mimeType) throws ResourceIOException {
    FileResource resource = getResource(null, null, mimeType);
    return resource;
  }

  @Override
  public FileResource create(String key, FileResourcePersistenceType resourcePersistenceType, MimeType mimeType) throws ResourceIOException {
    FileResource resource = getResource(key, resourcePersistenceType, mimeType);
    if (key == null && FileResourcePersistenceType.MANAGED.equals(resourcePersistenceType)) {
      key = resource.getUuid().toString();
    }
    List<URI> uris = getUris(key, resourcePersistenceType, mimeType);
    resource.setUri(uris.get(0));
    return resource;
  }

  @Override
  public Document getDocument(FileResource resource) throws ResourceIOException {
    Document doc = null;
    try {
      // get InputStream on resource
      try (InputStream is = getInputStream(resource)) {
        // create Document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(is);
      }
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Got document: " + doc);
      }
    } catch (IOException | ParserConfigurationException | SAXException ex) {
      throw new ResourceIOException("Cannot read document from resolved resource '" + resource.getUri().toString() + "'", ex);
    }
    return doc;
  }

  private FileResource getResource(String key, FileResourcePersistenceType persistenceType, MimeType mimeType) {
    FileResource resource = new FileResourceImpl();
    if (mimeType != null) {
      resource.setMimeType(mimeType);
    }
    if (FileResourcePersistenceType.REFERENCED.equals(persistenceType)) {
      resource.setReadonly(true);
    }
    if (FileResourcePersistenceType.MANAGED.equals(persistenceType)) {
      if (key != null) {
        resource.setUuid(UUID.fromString(key));
      } else {
        resource.setUuid(UUID.randomUUID());
      }
    }
    return resource;
  }

  @Override
  public void delete(FileResource resource) throws ResourceIOException {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public FileResource find(String key, FileResourcePersistenceType resourcePersistenceType, MimeType mimeType) throws ResourceIOException {
    FileResource resource = getResource(key, resourcePersistenceType, mimeType);
    List<URI> candidates = getUris(key, resourcePersistenceType, mimeType);
    if (candidates.isEmpty()) {
      throw new ResourceIOException("Could not resolve key " + key + " with MIME type " + mimeType.getTypeName() + " to an URI");
    }
    URI uri = candidates.stream()
            .filter(u -> (resourceLoader.getResource(u.toString()).isReadable() || u.toString().startsWith("http")))
            .findFirst()
            .orElseThrow(() -> new ResourceIOException(
            "Could not resolve key " + key + " with MIME type " + mimeType.getTypeName()
            + " to a readable Resource. Attempted URIs were " + candidates));
    resource.setUri(uri);
    org.springframework.core.io.Resource springResource = resourceLoader.getResource(uri.toString());

    // TODO how to get lastModified for HTTP? (do a head-request?); for now it is 0
    long lastModified = getLastModified(springResource);
    if (lastModified != 0) {
      // lastmodified by code in java.io.File#lastModified (is also used in Spring's core.io.Resource) is in milliseconds!
      // TODO lastModified should be of type Instant? to be discussed...
      resource.setLastModified(Instant.ofEpochMilli(lastModified).atOffset(ZoneOffset.UTC).toLocalDateTime());
    } else {
      resource.setLastModified(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
    }

    // TODO how to get length for HTTP? (do a head-request?); for now it is -1
    long length = getSize(springResource);
    if (length > -1) {
      resource.setSizeInBytes(length);
    }
    return resource;
  }

  @Override
  public byte[] getBytes(FileResource resource) throws ResourceIOException {
    try {
      return IOUtils.toByteArray(this.getInputStream(resource));
    } catch (IOException ex) {
      String msg = "Could not read bytes from resource: " + resource;
      LOGGER.error(msg, ex);
      throw new ResourceIOException(msg, ex);
    }
  }

  @Override
  public InputStream getInputStream(URI resourceUri) throws ResourceIOException {
    try {
      String location = resourceUri.toString();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Getting inputstream for location '{}'.", location);
      }
      return resourceLoader.getResource(location).getInputStream();
    } catch (IOException e) {
      throw new ResourceIOException(e);
    }
  }

  @Override
  public InputStream getInputStream(FileResource resource) throws ResourceIOException {
    return getInputStream(resource.getUri());
  }

  private long getLastModified(org.springframework.core.io.Resource springResource) {
    try {
      return springResource.lastModified();
    } catch (FileNotFoundException e) {
      LOGGER.warn("Resource " + springResource.toString() + " does not exist.");
    } catch (IOException ex) {
      LOGGER.warn("Can not get lastModified for resource " + springResource.toString(), ex);
    }
    return -1;
  }

  @Override
  public Reader getReader(FileResource resource) throws ResourceIOException {
    return new InputStreamReader(this.getInputStream(resource));
  }

  public void setResourcePersistenceHandlers(List<ResourcePersistenceTypeHandler> resourcePersistenceTypeHandlers) {
    this.resourcePersistenceTypeHandlers = resourcePersistenceTypeHandlers;
  }

  public ResourcePersistenceTypeHandler getResourcePersistenceTypeHandler(FileResourcePersistenceType resourcePersistence)
          throws ResourceIOException {
    for (ResourcePersistenceTypeHandler resourcePersistenceTypeHandler : this.getResourcePersistenceTypeHandlers()) {
      if (resourcePersistence.equals(resourcePersistenceTypeHandler.getResourcePersistenceType())) {
        return resourcePersistenceTypeHandler;
      }
    }
    throw new ResourceIOException("No ResourcePersistenceHandler defined for " + resourcePersistence);
  }

  public List<ResourcePersistenceTypeHandler> getResourcePersistenceTypeHandlers() {
    if (this.resourcePersistenceTypeHandlers == null) {
      this.resourcePersistenceTypeHandlers = new LinkedList<>();
    }
    return resourcePersistenceTypeHandlers;
  }

  private long getSize(org.springframework.core.io.Resource springResource) {
    try {
      long length = springResource.contentLength();
      return length;
    } catch (IOException ex) {
      LOGGER.warn("Can not get size for resource " + springResource.toString(), ex);
    }
    return -1;
  }

  private List<URI> getUris(String key, FileResourcePersistenceType persistenceType, MimeType mimeType) throws ResourceIOException {
    ResourcePersistenceTypeHandler handler = getResourcePersistenceTypeHandler(persistenceType);
    return handler.getUris(key, mimeType);
  }

  public void assertReadability(FileResource resource) throws ResourceIOException {
    try (InputStream is = getInputStream(resource)) {
      if (is.available() <= 0) {
        throw new ResourceIOException("Cannot read " + resource.getFilename() + ": Empty file");
      }
    } catch (ResourceIOException e) {
      throw new ResourceIOException("Cannot read " + resource.getFilename() + ": Empty file");
    } catch (Exception e) {
      throw new ResourceIOException("Cannot read " + resource.getFilename() + ": " + e.getMessage());
    }
  }

  @Override
  public long write(FileResource resource, InputStream payload) throws ResourceIOException {

    Assert.notNull(payload, "payload must not be null");
    Assert.notNull(resource, "payload must not be null");

    if (resource.isReadonly()) {
      throw new ResourceIOException("Resource does not support write-operations.");
    }

    URI uri = resource.getUri();
    final String scheme = uri.getScheme();
    try {
      if ("http".equals(scheme) || "https".equals(scheme)) {
        throw new ResourceIOException("Scheme not supported for write-operations: " + scheme + " (" + uri + ")");
      }

      Files.createDirectories(Paths.get(uri).getParent());
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Writing: " + uri);
      }
      return IOUtils.copyLarge(payload, new FileOutputStream(Paths.get(uri).toFile()));
    } catch (IOException e) {
      String msg = "Could not write data to uri " + String.valueOf(uri);
      LOGGER.error(msg, e);
      throw new ResourceIOException(msg, e);
    }
  }

  @Override
  public long write(FileResource resource, String input) throws ResourceIOException {
    try (InputStream in = new ReaderInputStream(new StringReader(input), Charset.forName("UTF-8"))) {
      return write(resource, in);
    } catch (IOException ex) {
      String msg = "Could not write data to uri " + String.valueOf(resource.getUri());
      LOGGER.error(msg, ex);
      throw new ResourceIOException(msg, ex);
    }
  }

  @Override
  public Set<String> findKeys(String keyPattern, FileResourcePersistenceType resourcePersistenceType) throws ResourceIOException {
    if (resourcePersistenceType != FileResourcePersistenceType.RESOLVED) {
      throw new ResourceIOException("findKeys() is only available for RESOLVED resources");
    }

    // The pattern for valid keys is the original pattern without any brackets inside, but surrounded with one bracket.
    Pattern validKeysPattern = Pattern.compile(
            "("
            + keyPattern.replace("(", "").replace(")", "").replace("^", "").replace("$", "")
            + ")");

    Set<String> keys = new HashSet<>();
    ResolvedResourcePersistenceTypeHandler handler = (ResolvedResourcePersistenceTypeHandler) getResourcePersistenceTypeHandler(resourcePersistenceType);

    // For all handlers: Retrieve all substition paths for the given key pattern
    for (Path p : handler.getPathsForPattern(keyPattern)) {
      Path basePath = p.getParent();    // Only in this directory, we search for the matching keys

      // The pattern for valid filenames is the filename of the path, where all backreferences are replaced by a wildcard regexp.
      // The whole pattern is finally surrounded by start and end regexp characters.
      String filenameAsKeyWithWildcards = p.getFileName().toString().replaceAll("\\$\\d+", ".*");
      Pattern validFilenamesPattern = Pattern.compile("^" + filenameAsKeyWithWildcards + "$");

      if (basePath == null || "".equals(basePath.toString())) {
        basePath = Paths.get("/");
      }

      // We must stay within the given path. So, no path substitutions outside are allowed!
      if (basePath.normalize().toString().contains("$")) {
        throw new ResourceIOException("Cannot find keys for substitutions with references in paths");
      }

      // Retrieve all files in the substitution path and filter out the non matching ones.
      // "Matching" means, match the filename of the substitution and match the key pattern
      // Finally map them onto the keys
      try (Stream<Path> stream = getDirectoryStream(basePath)) {
        keys.addAll(stream.map(path -> path.getFileName().normalize().toString())
                .filter(filename -> matchesPattern(validFilenamesPattern, filename))
                .filter(filename -> matchesPattern(validKeysPattern, filename))
                .map(filename -> mapToPattern(validKeysPattern, filename))
                .collect(Collectors.toSet()));
      } catch (IOException e) {
        LOGGER.error("Cannot traverse directory " + basePath + ": " + e, e);
      }
    }

    return keys;
  }

  private boolean matchesPattern(Pattern pattern, String filename) {
    Matcher m = pattern.matcher(filename);
    return m.find();
  }

  private String mapToPattern(Pattern pattern, String filename) {
    Matcher m = pattern.matcher(filename);
    if (m.find()) {
      return m.group(1);
    } else {
      return null;
    }
  }

  protected void overrideDirectoryStream(DirectoryStream<Path> overriddenDirectoryStream) {
    this.overriddenDirectoryStream = overriddenDirectoryStream;
  }

  private Stream<Path> getDirectoryStream(Path basePath) throws IOException {
    if (overriddenDirectoryStream == null) {
      return StreamSupport.stream(Files.newDirectoryStream(basePath).spliterator(), false).filter(Files::isRegularFile);
    } else {
      return StreamSupport.stream(overriddenDirectoryStream.spliterator(), false);
    }
  }
}
