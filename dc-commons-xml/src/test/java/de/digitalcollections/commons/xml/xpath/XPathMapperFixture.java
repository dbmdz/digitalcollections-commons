package de.digitalcollections.commons.xml.xpath;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XPathMapperFixture<T> {

  final Class<T> xPathMapperClass;

  public XPathMapperFixture(Class<T> xPathMapperClass) {
    this.xPathMapperClass = xPathMapperClass;
  }

  private Document readDocumentFromResource(String resourceName) {
    try (InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.parse(is);
    } catch (IOException | ParserConfigurationException | SAXException ex) {
      throw new RuntimeException("Cannot read test resource from " + resourceName + ": " + ex);
    }
  }

  public T setUpMapperWithResource(String resourceName) throws XPathMappingException {
    return XPathMapper.readDocument(readDocumentFromResource(resourceName), xPathMapperClass);
  }
}
