package org.mdz.dzp.commons.xml.xpath;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.mdz.dzp.commons.xml.namespaces.MdzNamespaceContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a lightweight wrapper around the Document class to make XPath queries less painful and verbose.
 */
public class XPathWrapper {

  private Document document;

//  private DocumentBuilder parser;
//  private DocumentBuilderFactory domFactory;
  private XPath xpath;

  private static Map<String, XPathExpression> xpathExpressions = Collections.synchronizedMap(new LruCache());

  public Map<String, XPathExpression> getXpathExpressions() {
    return xpathExpressions;
  }

  public void setXpathExpressions(Map<String, XPathExpression> map) {
    synchronized (xpathExpressions) {
      xpathExpressions = map;
    }
  }

//  public DocumentBuilder getParser() throws ParserConfigurationException {
//    if (this.parser == null) {
//      this.parser = this.getDomFactory().newDocumentBuilder();
//    }
//    return parser;
//  }
//
//  public DocumentBuilderFactory getDomFactory() {
//    if (this.domFactory == null) {
//      this.domFactory = DocumentBuilderFactory.newInstance();
//      this.domFactory.setNamespaceAware(true);
//    }
//    return domFactory;
//  }
  public XPath getXpath() {
    if (this.xpath == null) {
      this.xpath = XPathFactory.newInstance().newXPath();
      this.xpath.setNamespaceContext(new MdzNamespaceContext());
    }
    return xpath;
  }

  public void setXpath(XPath xpath) {
    this.xpath = xpath;
  }

//  public XPathWrapper(XPathWrapper xPathWrapper) throws ParserConfigurationException {
//    this.setXpathExpressions(xPathWrapper.getXpathExpressions());
//    this.setXpath(xPathWrapper.getXpath());
//    this.setDomFactory(xPathWrapper.getDomFactory());
//  }
  public XPathWrapper() {
  }

  public XPathWrapper(Document document) {
    this();
    this.document = document;
  }

//  public XPathWrapper(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
//    this();
//    this.loadDocument(inputStream);
//  }
//  public XPathWrapper(Node node) throws ParserConfigurationException {
//    this();
//    this.document = this.getNodeAsDocument(node);
//  }
//  public void loadDocument(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
//    this.document = this.getParser().parse(inputStream);
//  }
  /**
   * Get the result of the XPath query xpath as a new Document object.
   *
   * @param xpath the xpath
   * @return the document
   * @throws XPathExpressionException the x path expression exception
   * @throws ParserConfigurationException the parser configuration exception
   */
//  public Document asDocument(String xpath) throws XPathExpressionException, ParserConfigurationException {
//    Node node = (Node) this.getXpathExpression(xpath).evaluate(this.getDocument(), XPathConstants.NODE);
//    return this.getNodeAsDocument(node);
//  }
  /**
   * Converts a Node into a Document.
   *
   * @param node node to be converted to Document
   * @return node as Document
   * @throws ParserConfigurationException the parser configuration exception
   */
//  public Document getNodeAsDocument(Node node) throws ParserConfigurationException {
//    Document newDocument = this.getDomFactory().newDocumentBuilder().newDocument();
//    // To avoid a NullPointerException when the XPath-expression returns no nodes.
//    if (node != null) {
//      Node importedNode = newDocument.importNode(node, true);
//      newDocument.appendChild(importedNode);
//    }
//    return newDocument;
//  }
  /**
   * Get the result of the XPath query as a new XPathWrapper object.
   *
   * @param xpath the xpath
   * @return xpath result as new XPathWrapper
   * @throws XPathExpressionException the x path expression exception
   * @throws ParserConfigurationException the parser configuration exception
   */
//  public XPathWrapper asXPathWrapper(String xpath) throws XPathExpressionException, ParserConfigurationException {
//    XPathWrapper xPathWrapper = new XPathWrapper(this.asDocument(xpath));
//    xPathWrapper.setXpath(this.getXpath());
//    xPathWrapper.setDomFactory(this.getDomFactory());
//    return xPathWrapper;
//  }
  /**
   * Get the results of the XPath query as a List of new XPathWrapper objects.
   *
   * @param xpath the xpath
   * @return spath result as XPathWrapper list
   * @throws XPathExpressionException the x path expression exception
   * @throws ParserConfigurationException the parser configuration exception
   */
//  public List<XPathWrapper> asListOfXPathWrappers(String xpath) throws XPathExpressionException, ParserConfigurationException {
//    NodeList nodeList = this.asNodeList(xpath);
//    List<XPathWrapper> list = new ArrayList<>(nodeList.getLength());
//    for (int i = 0, l = nodeList.getLength(); i < l; i++) {
//      list.add(new XPathWrapper(this.getNodeAsDocument(nodeList.item(i))));
//    }
//    return list;
//  }
  /**
   * Like {@link #asDocument(String xpath)} but overwrites the original document.
   *
   * @param xpath the xpath
   * @throws XPathExpressionException the x path expression exception
   * @throws ParserConfigurationException the parser configuration exception
   */
//  public void createSubset(String xpath) throws XPathExpressionException, ParserConfigurationException {
//    this.document = this.asDocument(xpath);
//  }
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
    synchronized (xpathExpressions) {
      if (!xpathExpressions.containsKey(xpath)) {
        xpathExpressions.put(xpath, this.getXpath().compile(xpath));
      }
    }
    return xpathExpressions.get(xpath);
  }

  public void setNamespaceContext(NamespaceContext namespaceContext) {
    this.getXpath().setNamespaceContext(namespaceContext);
  }

//  public void setNamespaceAware(boolean namespaceAware) {
//    this.getDomFactory().setNamespaceAware(namespaceAware);
//  }
//  public void setDomFactory(DocumentBuilderFactory domFactory) throws ParserConfigurationException {
//    this.domFactory = domFactory;
//    this.domFactory.setNamespaceAware(true);
//    this.parser = this.domFactory.newDocumentBuilder();
//  }
  public void setXpathFactory(XPathFactory factory) {
    this.xpath = factory.newXPath();
  }

  private static class LruCache extends LinkedHashMap<String, XPathExpression> {

    private static final Integer MAX_ENTRIES = 128;

    public LruCache() {
      super(MAX_ENTRIES, 0.75f, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, XPathExpression> eldest) {
      return size() > MAX_ENTRIES;
    }
  }
}
