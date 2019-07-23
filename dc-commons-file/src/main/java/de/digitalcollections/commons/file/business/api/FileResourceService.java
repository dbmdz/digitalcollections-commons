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

public interface FileResourceService {

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  FileResource create();

  default FileResource createByContentTypeAndFilename(String contentType, String filename) {
    return createByMimeTypeAndFilename(MimeType.fromTypename(contentType), filename);
  }

  default FileResource createByFilename(String filename) {
    MimeType mimeType = MimeType.fromFilename(filename);
    FileResource result = createByMimeType(mimeType);
    result.setFilename(filename);
    return result;
  }

  default FileResource createByFilenameExtension(String filenameExtension) {
    MimeType mimeType = MimeType.fromExtension(filenameExtension);
    FileResource result = createByMimeType(mimeType);
    return result;
  }

  FileResource createByMimeType(MimeType mimeType);

  default FileResource createByMimeTypeAndFilename(MimeType mimeType, String filename) {
    FileResource result = createByMimeType(mimeType);
    result.setFilename(filename);
    return result;
  }

  FileResource find(String identifier, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException;

  default FileResource find(String identifier, String fileExtension) throws ResourceIOException, ResourceNotFoundException {
    return find(identifier, MimeType.fromExtension(fileExtension));
  }

  byte[] getAsBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Document getAsDocument(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  String getAsString(FileResource fileResource, Charset charset) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
}
