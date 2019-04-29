package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.ApplicationFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.AudioFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.ImageFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.TextFileResourceImpl;
import de.digitalcollections.model.impl.identifiable.resource.VideoFileResourceImpl;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class FileResourceRepositoryImpl implements FileResourceRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceRepositoryImpl.class);

  protected ResourceLoader resourceLoader;

  @Override
  public void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    try (InputStream is = getInputStream(resource)) {
      if (is.available() <= 0) {
        throw new ResourceIOException("Cannot read " + resource.getFilename() + ": Empty file");
      }
    } catch (ResourceIOException e) {
      throw new ResourceIOException("Cannot read " + resource.getFilename() + ": Empty file");
    } catch (ResourceNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new ResourceIOException("Cannot read " + resource.getFilename() + ": " + e.getMessage());
    }
  }

  @Override
  public FileResource createByMimetype(MimeType mimeType) {
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

  @Override
  public void delete(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public byte[] getBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    try {
      assertReadability(resource);
      return IOUtils.toByteArray(this.getInputStream(resource));
    } catch (IOException ex) {
      String msg = "Could not read bytes from resource: " + resource;
      LOGGER.error(msg, ex);
      throw new ResourceIOException(msg, ex);
    }
  }

  @Override
  public Document getDocument(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
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

  @Override
  public InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException {
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

  @Override
  public InputStream getInputStream(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    return getInputStream(resource.getUri());
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
  public Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    return new InputStreamReader(this.getInputStream(resource));
  }

  protected long getSize(Resource springResource) {
    try {
      long length = springResource.contentLength();
      return length;
    } catch (IOException ex) {
      LOGGER.warn("Can not get size for resource " + springResource.toString(), ex);
    }
    return -1;
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
}
