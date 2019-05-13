package de.digitalcollections.commons.file.backend.impl.resolved;

import de.digitalcollections.commons.file.backend.impl.FileResourceRepositoryImpl;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

@Repository
public class ResolvedFileResourceRepositoryImpl extends FileResourceRepositoryImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolvedFileResourceRepositoryImpl.class);

  private DirectoryStream<Path> overriddenDirectoryStream;      // only for testing purposes
  private final ResolvedFileResourceRepositoryConfig resolvedFileResourcesConfig;

  @Autowired
  public ResolvedFileResourceRepositoryImpl(ResolvedFileResourceRepositoryConfig resolvedFileResourcesConfig, ResourceLoader resourceLoader) {
    this.resolvedFileResourcesConfig = resolvedFileResourcesConfig;
    this.resourceLoader = resourceLoader;
  }

  public FileResource create(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException {
    if (mimeType == null) {
      throw new ResourceIOException("missing mimetype");
    }
    FileResource resource = createByMimetype(mimeType);
    resource.setReadonly(readOnly);
    resource.setUuid(UUID.randomUUID());

    List<URI> uris = getUris(identifier, mimeType);
    if (uris.isEmpty()) {
      throw new ResourceIOException(String.format("FileResource with identifier %s and MimeType %s is not resolvable.", identifier, mimeType));
    }
    URI uri = uris.get(0);
    resource.setUri(uri);

    return resource;
  }

  public FileResource create(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException {
    return create(identifier, MimeType.fromExtension(filenameExtension), readOnly);
  }

  @Override
  public FileResource create() {
    FileResource resource = new FileResourceImpl();
    return resource;
  }

  @Override
  public FileResource find(String identifier, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException {
    return find(identifier, mimeType, true);
  }

  public FileResource find(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
    FileResource resource = create(identifier, mimeType, readOnly);
    List<URI> candidates = getUris(identifier, mimeType);
    if (candidates.isEmpty()) {
      throw new ResourceIOException("Could not resolve identifier " + identifier + " with MIME type " + mimeType.getTypeName() + " to an URI");
    }
    URI uri = candidates.stream()
        .filter(u -> (resourceLoader.getResource(u.toString()).isReadable() || u.toString().startsWith("http")))
        .findFirst()
        .orElseThrow(() -> new ResourceIOException("Could not resolve identifier " + identifier + " with MIME type " + mimeType.getTypeName() + " to a readable Resource. Attempted URIs were " + candidates));
    resource.setUri(uri);
    Resource springResource = resourceLoader.getResource(uri.toString());

    if (!uri.getScheme().startsWith("http") && !springResource.exists()) {
      throw new ResourceNotFoundException("Resource not found at location '" + uri.toString() + "'");
    }
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

  public FileResource find(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
    return ResolvedFileResourceRepositoryImpl.this.find(identifier, MimeType.fromExtension(filenameExtension), readOnly);
  }

  public Set<String> findKeys(String keyPattern) throws ResourceIOException {
    // The pattern for valid keys is the original pattern without any brackets inside, but surrounded with one bracket.
    // news_(\\d{8}) -> (news_\\d{8})
    Pattern validKeysPattern = Pattern.compile("(" + keyPattern.replace("(", "").replace(")", "").replace("^", "").replace("$", "") + ")");

    Set<String> keys = new HashSet<>();

    // For all handlers: Retrieve all substition paths for the given key pattern
    Set<Path> paths = getPathsByPattern(keyPattern);

    for (Path p : paths) {
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

      // Ensure, the basePath does not start with "file:"
      if (basePath.toString().startsWith("file:"))  {
        basePath = Paths.get(basePath.toString().substring(5));
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

  private Stream<Path> getDirectoryStream(Path basePath) throws IOException {
    if (overriddenDirectoryStream == null) {
      return StreamSupport.stream(Files.newDirectoryStream(basePath).spliterator(), false).filter(Files::isRegularFile);
    } else {
      return StreamSupport.stream(overriddenDirectoryStream.spliterator(), false);
    }
  }

  public Set<Path> getPathsByPattern(String pattern) throws ResourceIOException {
    Set<Path> paths = new HashSet<>();
    for (PatternFileNameResolverImpl resolver : resolvedFileResourcesConfig.getPatterns()) {
      if (resolver.getPattern().equals(pattern)) {
        paths.addAll(resolver.getPaths());
      }
    }
    return paths;
  }

  public List<URI> getUris(String identifier, MimeType mimeType) throws ResourceIOException {
    List<PatternFileNameResolverImpl> patterns = resolvedFileResourcesConfig.getPatterns();
    PatternFileNameResolverImpl patternFileNameResolverImpl = patterns.stream()
        .filter(r -> r.isResolvable(identifier))
        .findFirst() // TODO: why only the first? See below method collectiong from all resolvers...
        .orElseThrow(() -> new ResourceIOException(identifier + " not resolvable!"));
    List<URI> uris = patternFileNameResolverImpl.getUris(identifier, mimeType);
    return uris;
  }

  public List<String> getUrisAsString(String identifier) throws ResourceIOException {
    return resolvedFileResourcesConfig.getPatterns().stream()
        .filter(r -> r.isResolvable(identifier))
        .map(r -> r.getUrisAsString(identifier))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private String mapToPattern(Pattern pattern, String filename) {
    Matcher m = pattern.matcher(filename);
    if (m.find()) {
      return m.group(1);
    } else {
      return null;
    }
  }

  private boolean matchesPattern(Pattern pattern, String filename) {
    Matcher m = pattern.matcher(filename);
    return m.find();
  }

  protected void overrideDirectoryStream(DirectoryStream<Path> overriddenDirectoryStream) {
    this.overriddenDirectoryStream = overriddenDirectoryStream;
  }
}
