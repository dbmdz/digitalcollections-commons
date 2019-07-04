package de.digitalcollections.commons.file.business.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

//  @Override
//  public void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
//    repository.assertReadability(resource);
//  }
//  @Override
//  public FileResource create() {
//    return repository.create();
//  }
//
//  @Override
//  public FileResource create(MimeType mimeType) {
//    return repository.createByMimetype(mimeType);
//  }
//
//  @Override
//  public void delete(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
//    repository.delete(resource);
//  }
//  public FileResource create(String filename) {
//    return getRepo().create(filename);
//  }
//
//  public FileResource create(MimeType mimeType, String filename) {
//    return getRepo().create(mimeType, filename);
//  }
//  public FileResource create(String contentType, String filename) {
//    return create(MimeType.fromTypename(contentType), filename);
//  }
//
//  public FileResource create(UUID uuid) {
//    return getRepo().create(uuid);
//  }
//
//  public FileResource find(String uuid) throws ResourceIOException, ResourceNotFoundException {
//    return getRepo().find(uuid);
//  }
  @Override
  public FileResource find(String identifier, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException {
    return repository.find(identifier, mimeType);
  }

//  @Override
//  public byte[] getBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
//    return repository.getBytes(resource);
//  }
//
  @Override
  public Document getAsDocument(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
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
  public String getAsString(FileResource fileResource, Charset charset) throws ResourceIOException, ResourceNotFoundException {
    try (InputStream is = getInputStream(fileResource)) {
      return IOUtils.toString(is, charset);
    } catch (IOException e) {
      throw new ResourceIOException(e);
    }
  }

  @Override
  public InputStream getInputStream(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException {
    return repository.getInputStream(fileResource);
  }

//  @Override
//  public InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException {
//    return repository.getInputStream(resourceUri);
//  }
//
//  @Override
//  public Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
//    return repository.getReader(resource);
//  }
//
//  @Override
//  public long write(FileResource fileResource, String input) throws ResourceIOException {
//    return repository.write(fileResource, input);
//  }
//
//  @Override
//  public long write(FileResource fileResource, InputStream inputStream) throws ResourceIOException {
//    return repository.write(fileResource, inputStream);
//  }
  //  public FileResource create(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException {
//    return getRepo().create(identifier, mimeType, readOnly);
//  }
//
//  public FileResource create(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException {
//    return getRepo().create(identifier, filenameExtension, readOnly);
//  }
//
//  public FileResource find(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
//    return getRepo().find(identifier, mimeType, readOnly);
//  }
//  public FileResource find(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
//    return getRepo().find(identifier, filenameExtension, readOnly);
//  }
  //
//  public List<URI> getUris(String identifier, MimeType mimeType) throws ResourceIOException {
//    return getRepo().getUris(identifier, mimeType);
//  }
//
//  public List<String> getUrisAsString(String identifier) throws ResourceIOException {
//    return getRepo().getUrisAsString(identifier);
//  }
}
