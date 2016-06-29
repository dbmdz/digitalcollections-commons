package org.mdz.dzp.commons.xml.xpath;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a lightweight wrapper around the Document class to make XPath queries less painful and verbose.
 */
public class XPathWrapper {

  private Document document;

  private XPathExpressionCache expressionCache;

  private XPathWrapper() {}

  public XPathWrapper(Document document, XPathExpressionCache expressionCache) {
    this.document = document;
    this.expressionCache = expressionCache;
  }

  public XPathWrapper(Document document) {
    this(document, new XPathExpressionCache());
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as Node. If the xPath results in more than one Node,
   * the first one is returned.
   *
   * @param xpath the xpath
   * @return the node
   * @throws XPathExpressionException the x path expression exception
   */
  public Node asNode(String xpath) throws XPathExpressionException {
    return this.asNode(xpath, 0);
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as Node.
   *
   * @param xpath the xpath
   * @param index the index
   * @return the node
   * @throws XPathExpressionException the x path expression exception
   */
  public Node asNode(String xpath, int index) throws XPathExpressionException {
    NodeList nodeList = (NodeList) this.getXpathExpression(xpath).evaluate(this.getDocument(), XPathConstants.NODESET);
    return nodeList.item(index);
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as NodeList.
   *
   * @param xpath the xpath
   * @return the node list
   * @throws XPathExpressionException the x path expression exception
   */
  public NodeList asNodeList(String xpath) throws XPathExpressionException {
    return (NodeList) this.getXpathExpression(xpath).evaluate(this.getDocument(), XPathConstants.NODESET);
  }

  public NodeList asNodeList(Node node, String xpath) throws XPathExpressionException {
    return (NodeList) this.getXpathExpression(xpath).evaluate(node, XPathConstants.NODESET);
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as a List of Nodes.
   *
   * @param xpath the xpath
   * @return the list
   * @throws XPathExpressionException the x path expression exception
   */
  public List<Node> asListOfNodes(String xpath) throws XPathExpressionException {
    NodeList nodeList = this.asNodeList(xpath);
    List<Node> list = new ArrayList<>(nodeList.getLength());
    for (int i = 0, l = nodeList.getLength(); i < l; i++) {
      list.add(nodeList.item(i));
    }
    return list;
  }
  
  /**
   * Gets a fraction of the document by a xPath-Expression xpath as a List of Nodes of a subnode
   *
   * @param node the subnode
   * @param xpath the xpath
   * @return the list
   * @throws XPathExpressionException the x path expression exception
   */
  public List<Node> asListOfNodes(Node node, String xpath) throws XPathExpressionException {
    NodeList nodeList = this.asNodeList(node, xpath);
    List<Node> list = new ArrayList<>(nodeList.getLength());
    for (int i = 0, l = nodeList.getLength(); i < l; i++) {
      list.add(nodeList.item(i));
    }
    return list;
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as a List of Strings.
   *
   * @param xpath the xpath
   * @return the list
   * @throws XPathExpressionException the x path expression exception
   */
  public List<String> asListOfStrings(String xpath) throws XPathExpressionException {
    NodeList nodeList = this.asNodeList(xpath);
    List<String> list = nodeListContentsToListOfStrings(nodeList);
    return list;
  }

  public List<String> asListOfStrings(Node node, String xpath) throws XPathExpressionException {
    NodeList nodeList = this.asNodeList(node, xpath);
    List<String> list = nodeListContentsToListOfStrings(nodeList);
    return list;
  }

  private List<String> nodeListContentsToListOfStrings(NodeList nodeList) throws DOMException {
    List<String> list = new ArrayList<>(nodeList.getLength());
    for (int i = 0, l = nodeList.getLength(); i < l; i++) {
      String textContent = nodeList.item(i).getTextContent();
      if (textContent == null) {
        textContent = "";
      }
      textContent = textContent.trim();
      list.add(textContent);
    }
    return list;
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as String.
   *
   * @param xpath the xpath
   * @return the string
   * @throws XPathExpressionException the x path expression exception
   */
  public String asString(String xpath) throws XPathExpressionException {
    final String rawString = (String) this.getXpathExpression(xpath).evaluate(this.getDocument(), XPathConstants.STRING);
    if (rawString == null) {
      return "";
    }
    return rawString.trim();
  }

  public String asString(Node node, String xpath) throws XPathExpressionException {
    final String rawString = (String) this.getXpathExpression(xpath).evaluate(node, XPathConstants.STRING);
    if (rawString == null) {
      return "";
    }
    return rawString.trim();
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as boolean.
   *
   * @param xpath the xpath
   * @return the boolean
   * @throws XPathExpressionException the x path expression exception
   */
  public Boolean asBoolean(String xpath) throws XPathExpressionException {
    final String value = (String) this.getXpathExpression(xpath).evaluate(this.getDocument(), XPathConstants.STRING);
    return Boolean.parseBoolean(value);
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as Number.
   *
   * @param xpath the xpath
   * @return the number
   * @throws XPathExpressionException the x path expression exception
   */
  public Number asNumber(String xpath) throws XPathExpressionException {
    return (Number) this.getXpathExpression(xpath).evaluate(this.getDocument(), XPathConstants.NUMBER);
  }

  /**
   * Serialize to string.
   *
   * @return getDocument as XML-formated string
   * @throws UnsupportedEncodingException the unsupported encoding exception
   * @throws TransformerException the transformer exception
   */
  public String serializeToString() throws UnsupportedEncodingException, TransformerException {
    DOMSource source = new DOMSource(this.getDocument());
    StreamResult result = new StreamResult(new StringWriter());
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(source, result);
    return result.getWriter().toString();
  }

  public Document getDocument() {
    return document;
  }

  private XPathExpression getXpathExpression(String xpath) throws XPathExpressionException {
    return expressionCache.get(xpath);
  }

}
