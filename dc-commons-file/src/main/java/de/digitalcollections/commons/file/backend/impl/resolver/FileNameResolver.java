package de.digitalcollections.commons.file.backend.impl.resolver;

import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface FileNameResolver {

  Boolean isResolvable(String identifier);

  List<String> getStrings(String identifier) throws ResourceIOException;

  /**
   * Return resolved strings that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   * @throws ResourceIOException in case getStrings for key fails
   */
  default List<String> getStrings(String identifier, MimeType mimeType) throws ResourceIOException {
    return getStrings(identifier).stream()
            .filter(s -> mimeType.matches(MimeType.fromFilename(s)))
            .collect(Collectors.toList());
  }

  /**
   * Resolve the identifier to URI objects.
   *
   * @param identifier file identifier/resolving key
   * @return list of resolved file uris
   * @throws ResourceIOException in case getStrings for key fails
   */
  default List<URI> getUris(String identifier) throws ResourceIOException {
    return getStrings(identifier).stream()
            .map(URI::create).collect(Collectors.toList());
  }

  /**
   * Return resolved URIs that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   * @throws ResourceIOException in case getStrings for key fails
   */
  default List<URI> getUris(String identifier, MimeType mimeType) throws ResourceIOException {
    final List<URI> uris = getUris(identifier);
    return uris.stream()
            .filter(u -> (mimeType.matches(MimeType.fromURI(u)) || MimeType.fromURI(u) == null))
            .collect(Collectors.toList());
  }

  /**
   * Resolve the identifier to java.nio.Path objects.
   *
   * @param identifier file identifier/resolving key
   * @return list of resolved file uris
   * @throws ResourceIOException in case getStrings for key fails
   */
  default List<Path> getPaths(String identifier) throws ResourceIOException {
    return getStrings(identifier).stream()
            .map(Paths::get)
            .collect(Collectors.toList());
  }

  /**
   * Return resolved Paths that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   * @throws ResourceIOException in case getStrings for key fails
   */
  default List<Path> getPaths(String identifier, MimeType mimeType) throws ResourceIOException {
    return getPaths(identifier).stream()
            .filter(p -> mimeType.matches(MimeType.fromFilename(p.toString())))
            .collect(Collectors.toList());
  }

  /**
   * Return all paths for a given pattern
   * @param pattern the pattern for the paths
   * @return the paths
   * @throws ResourceIOException thrown if the path does not exist
   */
  Set<Path> getPathsForPattern(String pattern) throws ResourceIOException;
}
