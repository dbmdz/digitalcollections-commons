package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.backend.api.IdentifierToFileResourceUriResolver;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.ApplicationFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.AudioFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.ImageFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.TextFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.VideoFileResourceImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

/**
 * Repository for reading files by using an unique identifier and a mimetype specifying the file
 * resource. Identifier and mimetype are the source for determining the access-uri to the file
 * resource via (in this order):
 *
 * <ul>
 *   <li>configurable identifier to uri resolving using regex-patterns
 *   <li>custom @see IdentifierToFileResourceUriResolver beans put onto the spring application
 *       context
 * </ul>
 */
@Repository
public class FileResourceRepositoryImpl implements FileResourceRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceRepositoryImpl.class);

  private final List<IdentifierToFileResourceUriResolver> identifierToFileresourceUriResolvers;
  private final IdentifierPatternToFileResourceUriResolvingConfig resolvedFileResourcesConfig;
  private final ResourceLoader resourceLoader;
  private DirectoryStream<Path> overriddenDirectoryStream; // only for testing purposes

  @Autowired
  public FileResourceRepositoryImpl(
      IdentifierPatternToFileResourceUriResolvingConfig resolvedFileResourcesConfig,
      List<IdentifierToFileResourceUriResolver> identifierToFileresourceUriResolvers,
      ResourceLoader resourceLoader) {
    this.resolvedFileResourcesConfig = resolvedFileResourcesConfig;
    this.identifierToFileresourceUriResolvers = identifierToFileresourceUriResolvers;
    this.resourceLoader = resourceLoader;
  }

  public void addIdentifierToFileresourceUriResolver(IdentifierToFileResourceUriResolver resolver) {
    identifierToFileresourceUriResolvers.add(resolver);
  }

  @Override
  public void assertReadability(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException {
    try (InputStream is = getInputStream(resource)) {
      if (is.available() <= 0) {
        throw new ResourceIOException("Cannot read " + resource.getFilename() + ": Empty file");
      }
    } catch (ResourceIOException e) {
      throw new ResourceIOException(
          "Cannot read " + resource.getFilename() + ": " + e.getMessage());
    } catch (ResourceNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new ResourceIOException(
          "Cannot read " + resource.getFilename() + ": " + e.getMessage());
    }
  }

  @Override
  public FileResource create() {
    return new FileResourceImpl();
  }

  @Override
  public FileResource create(String identifier, MimeType mimeType) throws ResourceIOException {
    if (mimeType == null) {
      throw new ResourceIOException("missing mimetype");
    }
    FileResource resource = createByMimeType(mimeType);
    resource.setReadonly(false);
    resource.setUuid(UUID.randomUUID());

    List<URI> uris = getUris(identifier, mimeType);
    if (uris.isEmpty()) {
      throw new ResourceIOException(
          String.format(
              "FileResource with identifier %s and MimeType %s is not resolvable.",
              identifier, mimeType));
    }
    URI uri = uris.get(0);
    resource.setUri(uri);

    return resource;
  }

  @Override
  public FileResource createByMimeType(MimeType mimeType) {
    if (mimeType == null) {
      return new ApplicationFileResourceImpl();
    }
    FileResource result;
    String primaryType = mimeType.getPrimaryType();
    switch (primaryType) {
      case "audio":
        result = new AudioFileResourceImpl();
        break;
      case "image":
        result = new ImageFileResourceImpl();
        break;
      case "text":
        result = new TextFileResourceImpl();
        break;
      case "video":
        result = new VideoFileResourceImpl();
        break;
      default:
        result = new ApplicationFileResourceImpl();
    }
    result.setMimeType(mimeType);
    return result;
  }

  private List<URI> expandWildcardFilenames(List<URI> candidates) throws ResourceIOException {
    List<URI> result = new ArrayList<>();
    for (URI candidate : candidates) {
      if (candidate.getScheme().startsWith("file")) {
        String absolutePath = candidate.toString();
        String filenamePattern = absolutePath.substring(absolutePath.lastIndexOf("/"));
        if (filenamePattern.contains("*")) {
          try {
            DirectoryStream<Path> directoryStream = overriddenDirectoryStream;
            if (directoryStream == null) {
              Path directory = Paths.get(absolutePath.substring(0, absolutePath.lastIndexOf("/")));
              final PathMatcher matcher =
                  FileSystems.getDefault().getPathMatcher("glob:" + filenamePattern);
              DirectoryStream.Filter<Path> filter = matcher::matches;
              directoryStream = Files.newDirectoryStream(directory, filter);
            }
            List<URI> uris =
                StreamSupport.stream(directoryStream.spliterator(), false)
                    .map(Path::toUri)
                    .collect(Collectors.toList());
            result.addAll(uris);
            continue;
          } catch (MalformedURLException ex) {
            throw new ResourceIOException("Invalid URL " + candidate.toString(), ex);
          } catch (IOException ex) {
            throw new ResourceIOException(
                "Invalid file system access for " + candidate.toString(), ex);
          }
        }
      }
      result.add(candidate);
    }
    return result;
  }

  @Override
  public FileResource find(String identifier, MimeType mimeType)
      throws ResourceIOException, ResourceNotFoundException {
    if (mimeType == null) {
      throw new ResourceIOException("missing mimetype");
    }
    FileResource resource = createByMimeType(mimeType);
    resource.setReadonly(false);
    resource.setUuid(UUID.randomUUID());

    List<URI> candidates = getUris(identifier, mimeType);
    if (candidates.isEmpty()) {
      throw new ResourceIOException(
          "Could not resolve identifier "
              + identifier
              + " with MIME type "
              + mimeType.getTypeName()
              + " to an URI");
    }
    final List<URI> expandedCandidates = expandWildcardFilenames(candidates);

    URI uriCandidate;
    if (overriddenDirectoryStream != null) {
      // for testability
      uriCandidate = expandedCandidates.get(0);
      resource.setUri(uriCandidate);
    } else {
      uriCandidate =
          expandedCandidates.stream()
              .filter(
                  u ->
                      (resourceLoader.getResource(u.toString()).isReadable()
                          || u.toString().startsWith("http")))
              .findFirst()
              .orElseThrow(
                  () ->
                      new ResourceIOException(
                          "Could not resolve identifier "
                              + identifier
                              + " with MIME type "
                              + mimeType.getTypeName()
                              + " to a readable Resource. Attempted URIs were "
                              + candidates));
      resource.setUri(uriCandidate);

      // test if resource exists
      Resource springResource = resourceLoader.getResource(uriCandidate.toString());
      if (!uriCandidate.getScheme().startsWith("http") && !springResource.exists()) {
        throw new ResourceNotFoundException(
            "Resource not found at location '" + uriCandidate.toString() + "'");
      }
      long lastModified = getLastModified(springResource);
      if (lastModified != 0) {
        // lastmodified by code in java.io.File#lastModified (is also used in Spring's
        // core.io.Resource) is in milliseconds!
        resource.setLastModified(
            Instant.ofEpochMilli(lastModified).atOffset(ZoneOffset.UTC).toLocalDateTime());
      } else {
        resource.setLastModified(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
      }
      long length = getSize(springResource);
      if (length > -1) {
        resource.setSizeInBytes(length);
      }
    }
    // filename
    String filename = FilenameUtils.getName(resource.getUri().getPath());
    resource.setFilename(filename);
    return resource;
  }

  @Override
  public InputStream getInputStream(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException {
    return getInputStream(resource.getUri());
  }

  @Override
  public InputStream getInputStream(URI resourceUri)
      throws ResourceIOException, ResourceNotFoundException {
    try {
      String location = resourceUri.toString();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Getting inputstream for location '{}'.", location);
      }
      final Resource resource = resourceLoader.getResource(location);
      if (!resourceUri.getScheme().startsWith("http") && !resource.exists()) {
        throw new ResourceNotFoundException("Resource not found at location '" + location + "'");
      }
      return resource.getInputStream();
    } catch (IOException e) {
      throw new ResourceIOException(e);
    }
  }

  protected long getLastModified(Resource springResource) {
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
  public Reader getReader(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException {
    return new InputStreamReader(this.getInputStream(resource));
  }

  private long getSize(Resource springResource) {
    try {
      long length = springResource.contentLength();
      return length;
    } catch (IOException ex) {
      LOGGER.warn("Can not get size for resource " + springResource.toString(), ex);
    }
    return -1;
  }

  private List<URI> getUris(String identifier, MimeType mimeType) throws ResourceIOException {
    // first: try to resolve by patterns (if configured)
    List<IdentifierPatternToFileResourceUriResolverImpl> patterns =
        resolvedFileResourcesConfig.getPatterns();
    Optional<IdentifierPatternToFileResourceUriResolverImpl> patternFileNameResolverImpl =
        patterns.stream()
            .filter(r -> r.isResolvable(identifier))
            .findFirst(); // TODO: why only the first? See below method collectiong from all
    // resolvers...
    if (patternFileNameResolverImpl.isPresent()) {
      return patternFileNameResolverImpl.get().getUris(identifier, mimeType);
    }

    // second: try to resolve by custom resolvers:
    for (IdentifierToFileResourceUriResolver resolver : identifierToFileresourceUriResolvers) {
      if (resolver.isResolvable(identifier)) {
        return resolver.getUris(identifier, mimeType);
      }
    }
    throw new ResourceIOException(
        String.format(
            "identifier %s with mimetype %s is not resolvable!",
            identifier, mimeType.getTypeName()));
  }

  public List<String> getUrisAsString(String identifier) throws ResourceIOException {
    List<String> uris =
        resolvedFileResourcesConfig.getPatterns().stream()
            .filter(r -> r.isResolvable(identifier))
            .map(r -> r.getUrisAsStrings(identifier))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    identifierToFileresourceUriResolvers.stream()
        .filter(r -> r.isResolvable(identifier))
        .forEachOrdered(
            r -> {
              uris.addAll(r.getUrisAsStrings(identifier));
            });
    return uris;
  }

  protected void overrideDirectoryStream(DirectoryStream<Path> overriddenDirectoryStream) {
    this.overriddenDirectoryStream = overriddenDirectoryStream;
  }
}
