package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.FileSystemResourceIOException;
import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.backend.api.IdentifierToFileResourceUriResolver;
import de.digitalcollections.model.exception.ResourceIOException;
import de.digitalcollections.model.exception.ResourceNotFoundException;
import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.resource.ApplicationFileResource;
import de.digitalcollections.model.identifiable.resource.AudioFileResource;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import de.digitalcollections.model.identifiable.resource.TextFileResource;
import de.digitalcollections.model.identifiable.resource.VideoFileResource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
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
import org.springframework.core.io.FileSystemResource;
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
    } catch (ResourceNotFoundException e) {
      throw e;
    } catch (FileSystemException e) {
      throw new FileSystemResourceIOException(e);
    } catch (Exception e) {
      throw new ResourceIOException(
          "Cannot read " + resource.getFilename() + ": " + e.getMessage());
    }
  }

  @Override
  public FileResource create() {
    return new FileResource();
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
      return new ApplicationFileResource();
    }
    FileResource result;
    String primaryType = mimeType.getPrimaryType();
    switch (primaryType) {
      case "audio":
        result = new AudioFileResource();
        break;
      case "image":
        result = new ImageFileResource();
        break;
      case "text":
        result = new TextFileResource();
        break;
      case "video":
        result = new VideoFileResource();
        break;
      default:
        result = new ApplicationFileResource();
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
          } catch (FileSystemException e) {
            throw new FileSystemResourceIOException(e);
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

    if (overriddenDirectoryStream != null) {
      // for testability
      resource.setUri(expandedCandidates.get(0));
    } else {
      for (URI u : expandedCandidates) {
        if (u.getScheme().startsWith("http")) {
          resource.setUri(u);
          break;
        }
        Resource res = resourceLoader.getResource(u.toString());
        if (res.isReadable()) {
          resource.setUri(u);
          break;
        } else if (res instanceof FileSystemResource) {
          // For resources from the file system 'unreadable' can mean a whole number of things.
          // We'd like to differentiate between "there's a path with that name, but it's not
          // readable" and "we can't tell if that path is not readable due to an IO exception".
          // The former is something that should make us continue looking and the latter should
          // result in an error. Getting the size gives us an IOException if it's not possible,
          // contrary to `isReadable()` which just returns `false`.
          try {
            res.contentLength();
          } catch (FileSystemException fsExc) {
            // Can't determine file size due to low-level IO exception, exit with an error
            throw new FileSystemResourceIOException(fsExc);
          } catch (IOException e) {
            // Other reasons, keep looking for matching candidates
          }
        }
      }
      if (resource.getUri() == null) {
        throw new ResourceIOException(
            "Could not resolve identifier "
                + identifier
                + " with MIME type "
                + mimeType.getTypeName()
                + " to a readable Resource. Attempted URIs were "
                + candidates);
      }

      // test if resource exists
      Resource springResource = resourceLoader.getResource(resource.getUri().toString());
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
    } catch (FileSystemException e) {
      throw new FileSystemResourceIOException(e);
    } catch (IOException e) {
      throw new ResourceIOException(e);
    }
  }

  protected long getLastModified(Resource springResource) throws FileSystemResourceIOException {
    try {
      return springResource.lastModified();
    } catch (FileNotFoundException e) {
      LOGGER.warn("Resource " + springResource + " does not exist.");
    } catch (FileSystemException e) {
      LOGGER.error(
          String.format(
              "I/O error while trying to read timestamp from filesystem for %s", springResource),
          e);
      throw new FileSystemResourceIOException(e);
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

  private long getSize(Resource springResource) throws FileSystemResourceIOException {
    try {
      long length = springResource.contentLength();
      return length;
    } catch (FileSystemException e) {
      throw new FileSystemResourceIOException(e);
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
