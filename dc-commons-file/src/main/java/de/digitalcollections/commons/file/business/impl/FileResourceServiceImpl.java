package de.digitalcollections.commons.file.business.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.model.exception.ResourceIOException;
import de.digitalcollections.model.exception.ResourceNotFoundException;
import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.resource.FileResource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Service
public class FileResourceServiceImpl implements FileResourceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceServiceImpl.class);

  protected FileResourceRepository repository;

  @Autowired
  public FileResourceServiceImpl(FileResourceRepository repository) {
    this.repository = repository;
  }

  @Override
  public void assertReadability(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException {
    repository.assertReadability(resource);
  }

  @Override
  public FileResource create() {
    return repository.create();
  }

  @Override
  public FileResource createByMimeType(MimeType mimeType) {
    return repository.createByMimeType(mimeType);
  }

  @Override
  public FileResource find(String identifier, MimeType mimeType)
      throws ResourceIOException, ResourceNotFoundException {
    return repository.find(identifier, mimeType);
  }

  @Override
  public FileResource findOrCreate(String identifier, MimeType mimeType)
      throws ResourceIOException {
    try {
      return find(identifier, mimeType);
      // find() returns a ResourceIOException, if a resource does not exist!
    } catch (ResourceIOException | ResourceNotFoundException e) {
      return repository.create(identifier, mimeType);
    }
  }

  @Override
  public byte[] getAsBytes(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException {
    try {
      return IOUtils.toByteArray(getInputStream(resource));
    } catch (IOException ex) {
      String msg = "Could not read bytes from resource: " + resource;
      LOGGER.error(msg, ex);
      throw new ResourceIOException(msg, ex);
    }
  }

  @Override
  public Document getAsDocument(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException {
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
      throw new ResourceIOException(
          "Cannot read document from resolved resource '" + resource.getUri().toString() + "'", ex);
    }
    return doc;
  }

  @Override
  public String getAsString(FileResource fileResource, Charset charset)
      throws ResourceIOException, ResourceNotFoundException {
    try (InputStream is = getInputStream(fileResource)) {
      return IOUtils.toString(is, charset);
    } catch (IOException e) {
      throw new ResourceIOException(e);
    }
  }

  @Override
  public InputStream getInputStream(FileResource fileResource)
      throws ResourceIOException, ResourceNotFoundException {
    return repository.getInputStream(fileResource);
  }

  @Override
  public InputStream getInputStream(URI resourceUri)
      throws ResourceIOException, ResourceNotFoundException {
    return repository.getInputStream(resourceUri);
  }

  @Override
  public Reader getReader(FileResource resource)
      throws ResourceIOException, ResourceNotFoundException {
    return repository.getReader(resource);
  }
}
