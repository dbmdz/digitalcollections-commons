package de.digitalcollections.commons.xml.xpath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Document;

/**
 * Namespace context that uses the document's declared namespaces, in addition to user-defined prefixes.
 */
public class UniversalNamespaceContext implements NamespaceContext {
  private final Document doc;
  private final HashMap<String, String> customPrefixes;

  public UniversalNamespaceContext(Document doc) {
    this.doc = doc;
    this.customPrefixes = new HashMap<>();
  }

  /**
   * Add a user-defined namespace. Takes precedence over the document's declared namespaces.
   * @param prefix the prefix
   * @param uri the uri
   */
  public void addNamespace(String prefix, String uri) {
    this.customPrefixes.put(prefix, uri);
  }

  @Override
  public String getNamespaceURI(String prefix) {
    if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
      return doc.lookupNamespaceURI(null);
    }
    String uri = customPrefixes.get(prefix);
    if (uri == null) {
      uri = doc.lookupNamespaceURI(prefix);
    }
    return uri;
  }

  @Override
  public String getPrefix(String namespaceURI) {
    return customPrefixes.entrySet().stream()
        .filter(e -> e.getValue().equals(namespaceURI))
        .map(Entry::getKey)
        .findFirst()
        .orElseGet(() -> doc.lookupPrefix(namespaceURI));
  }

  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    throw new UnsupportedOperationException();
  }
}
