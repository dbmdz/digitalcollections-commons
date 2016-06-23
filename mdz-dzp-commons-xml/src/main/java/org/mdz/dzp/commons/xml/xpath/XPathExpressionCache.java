package org.mdz.dzp.commons.xml.xpath;

import java.util.concurrent.ConcurrentHashMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.mdz.dzp.commons.xml.namespaces.MdzNamespaceContext;
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

}
