package de.digitalcollections.commons.xml.xpath;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import de.digitalcollections.commons.xml.namespaces.MdzNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPathExpressionCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(XPathExpressionCache.class);

  private final ConcurrentHashMap<String, XPathExpression> cache;

  private final XPath xpath;

  public XPathExpressionCache() {
    cache = new ConcurrentHashMap();
    xpath = XPathFactory.newInstance().newXPath();
    xpath.setNamespaceContext(new MdzNamespaceContext());
  }

  public XPathExpression get(String expression) {
    return cache.computeIfAbsent(expression, (x) -> {
      XPathExpression result = null;
      try {
        result = xpath.compile(x);
      } catch (XPathExpressionException exception) {
        LOGGER.error("Could not compile XPathExpression.", exception);
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
      xpath.setNamespaceContext(new MdzNamespaceContext(namespaceUri));
    }
  }
}
