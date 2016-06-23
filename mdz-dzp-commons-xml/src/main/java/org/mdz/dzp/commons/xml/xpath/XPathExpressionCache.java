package org.mdz.dzp.commons.xml.xpath;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.xpath.XPathExpression;

class XPathExpressionCache extends LinkedHashMap<String, XPathExpression> {

  private static final Integer MAX_ENTRIES = 128;

  public XPathExpressionCache() {
    super(MAX_ENTRIES, 0.75f, true);
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<String, XPathExpression> eldest) {
    return size() > MAX_ENTRIES;
  }

}
