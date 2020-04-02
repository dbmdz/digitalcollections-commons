package de.digitalcollections.commons.file.backend.api;

import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface / contract for classes that resolve a given file resource identifier and an optional
 * target mimetype to one or more system specific URIs or Paths for accessing the file resource.
 */
public interface IdentifierToFileResourceUriResolver {

  Boolean isResolvable(String identifier);

  /**
   * Return resolved file uris as strings
   *
   * @param identifier file identifier/resolving key
   * @return list of resolved file uris converted to string
   */
  List<String> getUrisAsStrings(String identifier);

  /**
   * Return resolved strings that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   */
  default List<String> getUrisAsStrings(String identifier, MimeType mimeType) {
    return getUrisAsStrings(identifier).stream()
        .filter(s -> mimeType.matches(MimeType.fromFilename(s)))
        .collect(Collectors.toList());
  }

  /**
   * Resolve the identifier to URI objects.
   *
   * @param identifier file identifier/resolving key
   * @return list of resolved file uris
   * @throws ResourceIOException in case getUrisAsStrings for key fails
   */
  default List<URI> getUris(String identifier) throws ResourceIOException {
    return getUrisAsStrings(identifier).stream().map(URI::create).collect(Collectors.toList());
  }

  /**
   * Return resolved URIs that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   * @throws ResourceIOException in case getUrisAsStrings for key fails
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
   * @return list of resolved file paths
   * @throws ResourceIOException in case getUrisAsStrings for key fails
   */
  default List<Path> getPaths(String identifier) throws ResourceIOException {
    return getUrisAsStrings(identifier).stream().map(Paths::get).collect(Collectors.toList());
  }

  /**
   * Return resolved Paths that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file paths
   * @throws ResourceIOException in case getUrisAsStrings for key fails
   */
  default List<Path> getPaths(String identifier, MimeType mimeType) throws ResourceIOException {
    return getPaths(identifier).stream()
        .filter(p -> mimeType.matches(MimeType.fromFilename(p.toString())))
        .collect(Collectors.toList());
  }
}
