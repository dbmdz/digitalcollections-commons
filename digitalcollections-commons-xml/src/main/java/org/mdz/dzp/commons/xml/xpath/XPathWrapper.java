package org.mdz.dzp.commons.xml.xpath;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a lightweight wrapper around the Document class to make XPath queries less painful and verbose.
 */
public class XPathWrapper {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(XPathWrapper.class);
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

  public void setDefaultNamespace(String namespaceUrl) {
    this.expressionCache.setDefaultNamespace(namespaceUrl);
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
    NodeList nodeList = (NodeList) this.evaluateXpath(this.getDocument(), xpath, XPathConstants.NODESET);
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
    return (NodeList) this.evaluateXpath(this.getDocument(), xpath, XPathConstants.NODESET);
  }

  public NodeList asNodeList(Node node, String xpath) throws XPathExpressionException {
    return (NodeList) this.evaluateXpath(node, xpath, XPathConstants.NODESET);
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
    final String rawString = (String) evaluateXpath(this.getDocument(), xpath, XPathConstants.STRING);
    if (rawString == null) {
      return "";
    }
    return rawString.trim();
  }

  public String asString(Node node, String xpath) throws XPathExpressionException {
    final String rawString = (String) evaluateXpath(node, xpath, XPathConstants.STRING);
    if (rawString == null) {
      return "";
    }
    return rawString.trim();
  }

  private Object evaluateXpath(Node node, String xpath, QName returnType) throws XPathExpressionException {
    return this.getXpathExpression(xpath).evaluate(node, returnType);
  }

  /**
   * Gets a fraction of the document by a xPath-Expression xpath as boolean.
   *
   * @param xpath the xpath
   * @return the boolean
   * @throws XPathExpressionException the x path expression exception
   */
  public Boolean asBoolean(String xpath) throws XPathExpressionException {
    final String value = (String) evaluateXpath(this.getDocument(), xpath, XPathConstants.STRING);
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
    return (Number) this.evaluateXpath(this.getDocument(), xpath, XPathConstants.NUMBER);
  }
  
  /**
   * Returns a node, starting for a start node, identified by a relative path
   * @param node the start node
   * @param relativeXpath the relative xpath
   * @return
   * @throws XPathExpressionException 
   */
  public Node getRelativeNode(Node node, String relativeXpath) throws XPathExpressionException {
    if (!relativeXpath.startsWith(".")) {
      throw new XPathExpressionException("Relative node '" + relativeXpath + "' below '" + getFullXPath(node) + "' must start with a period! ");
    }

    List<Node> nodes = asListOfNodes(node, relativeXpath);

    if (nodes == null || nodes.isEmpty()) {
      LOGGER.info("No relative node found for " + getFullXPath(node) + " and relative path=" + relativeXpath);
      return null;
    }
    return nodes.get(0);
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
  
  
  /**
   * Helper method to dump the full xpath of a node
   * @param n The node
   * @return String represenation of the full xpath of the node
   */
  public String getFullXPath(Node n) {
    // abort early
    if (null == n)
      return null;

    // declarations
    Node parent = null;
    Stack<Node> hierarchy = new Stack<Node>();
    StringBuffer buffer = new StringBuffer();

    // push element on stack
    hierarchy.push(n);

    switch (n.getNodeType()) {
      case Node.ATTRIBUTE_NODE:
        parent = ((Attr) n).getOwnerElement();
        break;
      case Node.ELEMENT_NODE:
        parent = n.getParentNode();
        break;
      case Node.DOCUMENT_NODE:
        parent = n.getParentNode();
        break;
      default:
        throw new IllegalStateException("Unexpected Node type" + n.getNodeType());
    }

    while (null != parent && parent.getNodeType() != Node.DOCUMENT_NODE) {
      // push on stack
      hierarchy.push(parent);

      // get parent of parent
      parent = parent.getParentNode();
    }

    // construct xpath
    Object obj = null;
    while (!hierarchy.isEmpty() && null != (obj = hierarchy.pop())) {
      Node node = (Node) obj;
      boolean handled = false;

      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element e = (Element) node;

        // is this the root element?
        if (buffer.length() == 0) {
          // root element - simply append element name
          buffer.append(node.getNodeName());
        } else {
          // child element - append slash and element name
          buffer.append("/");
          buffer.append(node.getNodeName());

          if (node.hasAttributes()) {
            // see if the element has a name or id attribute
            if (e.hasAttribute("id")) {
              // id attribute found - use that
              buffer.append("[@id='" + e.getAttribute("id") + "']");
              handled = true;
            } else if (e.hasAttribute("name")) {
              // name attribute found - use that
              buffer.append("[@name='" + e.getAttribute("name") + "']");
              handled = true;
            }
          }

          if (!handled) {
            // no known attribute we could use - get sibling index
            int prev_siblings = 1;
            Node prev_sibling = node.getPreviousSibling();
            while (null != prev_sibling) {
              if (prev_sibling.getNodeType() == node.getNodeType()) {
                if (prev_sibling.getNodeName().equalsIgnoreCase(
                    node.getNodeName())) {
                  prev_siblings++;
                }
              }
              prev_sibling = prev_sibling.getPreviousSibling();
            }
            buffer.append("[" + prev_siblings + "]");
          }
        }
      } else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
        buffer.append("/@");
        buffer.append(node.getNodeName());
      }
    }
    // return buffer
    return buffer.toString();
  }



}
