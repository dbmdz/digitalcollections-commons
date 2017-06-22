package de.digitalcollections.commons.xml.sax;

import de.digitalcollections.commons.xml.namespaces.DigitalCollectionsNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class AbstractContentHandler implements ContentHandler {

  /** The handler. */
  private TransformerHandler handler;

  /** The namespace context. */
  private NamespaceContext namespaceContext;

  /**
   * Instantiates a new abstract content handler.
   */
  public AbstractContentHandler() {
    this.namespaceContext = new DigitalCollectionsNamespaceContext();
  }

  /**
   * Instantiates a new abstract content handler.
   *
   * @param handler the handler
   */
  public AbstractContentHandler(TransformerHandler handler) {
    this();
    this.handler = handler;
  }

  /**
   * Gets the namespace context.
   *
   * @return the namespace context
   */
  public NamespaceContext getNamespaceContext() {
    return namespaceContext;
  }

  /**
   * Gets the handler.
   *
   * @return the handler
   */
  public TransformerHandler getHandler() {
    return handler;
  }

  /**
   * Sets the namespace context.
   *
   * @param namespaceContext the new namespace context
   */
  public void setNamespaceContext(NamespaceContext namespaceContext) {
    this.namespaceContext = namespaceContext;
  }

  /**
   * Sets the handler.
   *
   * @param handler the new handler
   */
  public void setHandler(TransformerHandler handler) {
    this.handler = handler;
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
   */
  @Override
  public void setDocumentLocator(Locator locator) {
    this.handler.setDocumentLocator(locator);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startDocument()
   */
  @Override
  public void startDocument() throws SAXException {
    this.handler.startDocument();
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  @Override
  public void endDocument() throws SAXException {
    this.handler.endDocument();
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
   */
  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    this.handler.startPrefixMapping(prefix, uri);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
   */
  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    this.handler.endPrefixMapping(prefix);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    this.handler.startElement(uri, localName, qName, attributes);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    this.handler.endElement(uri, localName, qName);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    this.handler.characters(ch, start, length);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
   */
  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    this.handler.ignorableWhitespace(ch, start, length);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
   */
  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    this.handler.processingInstruction(target, data);
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
   */
  @Override
  public void skippedEntity(String name) throws SAXException {
    this.handler.skippedEntity(name);
  }

}
