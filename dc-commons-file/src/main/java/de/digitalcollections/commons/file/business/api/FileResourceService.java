package de.digitalcollections.commons.file.business.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import org.w3c.dom.Document;

/**
 * Service providing methods for creating FileResource instances and reading binary data of a
 * FileResource.
 */
public interface FileResourceService {

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
   * Convenience method for creating an FileResource instance depending on content type.
   *
   * @param contentType content type of FileResource
   * @param filename filename of FileResource
   * @return newly created instance of the underlying MimeType specific FileResource implementation.
   * @see FileResourceService#createByMimeType(MimeType)
   */
  default FileResource createByContentTypeAndFilename(String contentType, String filename) {
    return createByMimeTypeAndFilename(MimeType.fromTypename(contentType), filename);
  }

  /**
   * Convenience method for creating an FileResource instance depending on mimetype derived from
   * filename.
   *
   * @param filename filename of FileResource
   * @return newly created instance of the underlying MimeType specific FileResource implementation.
   * @see FileResourceService#createByMimeType(MimeType)
   */
  default FileResource createByFilename(String filename) {
    MimeType mimeType = MimeType.fromFilename(filename);
    FileResource result = createByMimeType(mimeType);
    result.setFilename(filename);
    return result;
  }

  /**
   * Convenience method for creating an FileResource instance depending on mimetype derived from
   * filename extension.
   *
   * @param filenameExtension filename extension representing mimetype
   * @return newly created instance of the underlying MimeType specific FileResource implementation.
   * @see FileResourceService#createByMimeType(MimeType)
   */
  default FileResource createByFilenameExtension(String filenameExtension) {
    MimeType mimeType = MimeType.fromExtension(filenameExtension);
    FileResource result = createByMimeType(mimeType);
    return result;
  }

  /**
   * @param mimeType mimetype of the FileResource
   * @return newly created instance of the underlying MimeType specific FileResource implementation.
   */
  FileResource createByMimeType(MimeType mimeType);

  /**
   * Convenience method for creating an FileResource instance depending on given mimetype and
   * filename.
   *
   * @param mimeType mimetype of FileResource
   * @param filename filename of FileResource
   * @return newly created instance of the underlying MimeType specific FileResource implementation.
   * @see FileResourceService#createByMimeType(MimeType)
   */
  default FileResource createByMimeTypeAndFilename(MimeType mimeType, String filename) {
    FileResource result = createByMimeType(mimeType);
    result.setFilename(filename);
    return result;
  }

  /**
   * @param identifier identifier of FileResource, used to lookup URI for FileResource
   * @param mimeType mimetype of the FileResource
   * @return FileResource implementation matching mimetype and URI resolved using identifier.
   * @throws ResourceIOException thrown if no URI can be resolved for FileResource with given
   *     mimetype and identifier
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  FileResource find(String identifier, MimeType mimeType)
      throws ResourceIOException, ResourceNotFoundException;

  /**
   * @param identifier identifier of FileResource, used to lookup URI for FileResource
   * @param fileExtension file extension used to derive mimetype for FileResource
   * @return FileResource implementation matching mimetype and URI resolved using identifier.
   * @throws ResourceIOException thrown if no URI can be resolved for FileResource with given
   *     mimetype and identifier
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   * @see FileResourceService#find(String, MimeType)
   */
  default FileResource find(String identifier, String fileExtension)
      throws ResourceIOException, ResourceNotFoundException {
    return find(identifier, MimeType.fromExtension(fileExtension));
  }

  /**
   * @param identifier identifier of FileResource, used to lookup URI for FileResource
   * @param mimeType mimetype of the FileResource
   * @return FileResource implementation matching mimetype and URI resolved using identifier. If the
   *     file resource does not exist, create it
   * @throws ResourceIOException thrown if no URI can be resolved for FileResource with given
   *     mimetype and identifier
   */
  FileResource findOrCreate(String identifier, MimeType mimeType) throws ResourceIOException;

  /**
   * Convenience method for directly getting FileResource binary data as byte[].
   *
   * @param resource FileResource containing URI for accessing FileResource data
   * @return binary data of FileResource as byte[]
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  byte[] getAsBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  /**
   * Convenience method for directly getting FileResource binary data as XML-Document.
   *
   * @param resource FileResource containing URI for accessing FileResource data
   * @return binary data of FileResource parsed as XML-Document
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  Document getAsDocument(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException;

  /**
   * Convenience method for directly getting FileResource binary data as String.
   *
   * @param fileResource FileResource containing URI for accessing FileResource data
   * @param charset encoding of FileResource data
   * @return binary data of FileResource as String
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  String getAsString(FileResource fileResource, Charset charset)
      throws ResourceIOException, ResourceNotFoundException;

  /**
   * @param fileResource FileResource containing URI for accessing FileResource data
   * @return InputStream for reading FileResource data
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  InputStream getInputStream(FileResource fileResource)
      throws ResourceIOException, ResourceNotFoundException;

  /**
   * @param resourceUri URI for accessing FileResource data
   * @return InputStream for reading FileResource data
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  /**
   * @param resource FileResource containing URI for accessing FileResource data
   * @return Reader for InputStream of FileResource data
   * @throws ResourceIOException thrown if an IOExcpetion appears at reading FileResource data
   * @throws ResourceNotFoundException thrown if FileResource at resolved URI does not exist
   */
  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
}
