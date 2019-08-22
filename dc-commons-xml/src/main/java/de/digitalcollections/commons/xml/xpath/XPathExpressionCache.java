package de.digitalcollections.commons.xml.xpath;

import de.digitalcollections.commons.xml.namespaces.DigitalCollectionsNamespaceContext;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.sf.saxon.xpath.XPathFactoryImpl;

public class XPathExpressionCache {
  private final ConcurrentHashMap<String, XPathExpression> cache;

  private final XPathFactory xPathFactory = new XPathFactoryImpl();
  private final XPath xpath;

  public XPathExpressionCache() {
    this(new DigitalCollectionsNamespaceContext());
  }

  public XPathExpressionCache(NamespaceContext namespaceCtx) {
    cache = new ConcurrentHashMap<>();
    xpath = xPathFactory.newXPath();
    xpath.setNamespaceContext(namespaceCtx);

  }

  public XPathExpression get(String expression) {
    return cache.computeIfAbsent(expression, (x) -> {
      XPathExpression result = null;
      try {
        result = xpath.compile(x);
      } catch (XPathExpressionException exception) {
        throw new IllegalArgumentException(exception);
      }
      return result;
    });
  }

  public int getSize() {
    return cache.size();
  }

  public List<String> getExpressions() {
    return Collections.list(cache.keys());
  }

  /**
   * Change the default namespace for all XPath expressions.
   *
   * <strong>CAREFUL:</strong> If the new default namespace is different from the current one, the complete cache will be
   * reset, so use with caution in performance-sensitive areas.
   *
   * @param namespaceUri The new default namespace URI
   */
  public void setDefaultNamespace(String namespaceUri) {
    if (!xpath.getNamespaceContext().getNamespaceURI("").equals(namespaceUri)) {
      this.cache.clear();
      xpath.setNamespaceContext(new DigitalCollectionsNamespaceContext(namespaceUri));
    }
  }
}
