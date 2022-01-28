package de.digitalcollections.commons.file.backend.api;

import de.digitalcollections.commons.file.backend.FileSystemResourceIOException;
import de.digitalcollections.model.exception.ResourceIOException;
import de.digitalcollections.model.exception.ResourceNotFoundException;
import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.resource.FileResource;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

/**
 * Repository providing methods for creating FileResource instances and reading binary data of a
 * FileResource.
 */
public interface FileResourceRepository {

  /**
   * Assert/check that FileResource is readable.
   *
   * @param resource FileResource to be checked for read accessibility
   * @throws ResourceIOException thrown if FileResource can not be accessed
   * @throws ResourceNotFoundException thrown if FileResource does not exist
   */
  void assertReadability(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException;

  /** @return newly created instance of the underlying FileResource implementation. */
  FileResource create();

  /**
   * @param identifier identifier of FileResource, used to lookup URI for FileResource
   * @param mimeType mimetype of the FileResource
   * @return newly created instance of the underlying MimeType specific FileResource implementation.
   * @throws ResourceIOException thrown if no URI can be resolved for FileResource with given
   *     mimetype and identifier
   */
  FileResource create(String identifier, MimeType mimeType) throws ResourceIOException;

  /**
   * @param mimeType mimetype of the FileResource
   * @return newly created instance of the underlying MimeType specific FileResource implementation.
   */
  FileResource createByMimeType(MimeType mimeType);

  /**
   * @param identifier identifier of FileResource, used to lookup URI for FileResource
   * @param mimeType mimetype of the FileResource
   * @return FileResource implementation matching mimetype and URI resolved using identifier.
   * @throws FileSystemResourceIOException if there was a raw disk I/O error while locating the
   *     resource
   * @throws ResourceIOException thrown if no URI can be resolved for FileResource with given
   *     mimetype and identifier
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  FileResource find(String identifier, MimeType mimeType)
      throws ResourceIOException, ResourceNotFoundException;

  /**
   * @param resourceUri URI for accessing FileResource data
   * @return InputStream for reading FileResource data
   * @throws FileSystemResourceIOException if there was a raw disk I/O error while locating the
   *     resource
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  /**
   * @param resource FileResource containing URI for accessing FileResource data
   * @return InputStream for reading FileResource data
   * @throws FileSystemResourceIOException if there was a raw disk I/O error while locating the
   *     resource
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  InputStream getInputStream(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException;

  /**
   * @param resource FileResource containing URI for accessing FileResource data
   * @return Reader for InputStream of FileResource data
   * @throws FileSystemResourceIOException if there was a raw disk I/O error while locating the
   *     resource
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
}
