package de.digitalcollections.commons.xml.xpath;

import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;

public class XPathWrapperTest {

  private XPathWrapper wrapper;

  @Before
  public void setUp() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bsbstruc.xml");
    Document doc = db.parse(is);
    this.wrapper = new XPathWrapper(doc);
  }

//  @Test
//  public void testFromNode() throws Exception {
//    Node srcNode = wrapper.asNode("//tei:sourceDesc");
//    XPathWrapper mWrapper = new XPathWrapper(srcNode);
//    assertEquals(mWrapper.getDocument().getDocumentElement().getTagName(), "sourceDesc");
//  }
//  @Test
//  public void testAsDocument() throws Exception {
//    Document doc = this.wrapper.asDocument("//tei:sourceDesc");
//    assertEquals(doc.getDocumentElement().getTagName(), "sourceDesc");
//  }
//  @Test
//  public void testAsXPathWrapper() throws Exception {
//    XPathWrapper mWrapper = this.wrapper.asXPathWrapper("//tei:facsimile[@ana='#facsScan']");
//    assertEquals(mWrapper.asListOfNodes("./tei:facsimile/tei:surface").size(), 467);
//  }
//  @Test
//  public void testAsListOfXPathWrappers() throws Exception {
//    List<XPathWrapper> wrappers = this.wrapper.asListOfXPathWrappers("//tei:facsimile[@ana='#facsMeta']/tei:surface");
//    assertEquals(wrappers.size(), 5);
//  }
//  @Test
//  public void testCreateSubset() throws Exception {
//    this.wrapper.createSubset("//tei:sourceDesc");
//    assertEquals(this.wrapper.getDocument().getDocumentElement().getTagName(), "sourceDesc");
//  }
  @Test
  public void testAsNode() throws Exception {
    Node node = this.wrapper.asNode("//tei:idno[@type='oclc']");
    assertEquals(node.getLocalName(), "idno");
    assertEquals(node.getTextContent(), "165600186");
  }

  @Test
  public void testAsNodeFromIndex() throws Exception {
    Node node = this.wrapper.asNode("//tei:idno", 1);
    assertEquals(node.getTextContent(), "1900440");
  }

  @Test
  public void testAsNodeList() throws Exception {
    NodeList nodes = this.wrapper.asNodeList("//tei:biblStruct/tei:idno");
    assertEquals(nodes.getLength(), 7);
  }

  @Test
  public void testAsListOfNodes() throws Exception {
    List<Node> nodes = this.wrapper.asListOfNodes("//tei:biblStruct/tei:idno");
    assertEquals(nodes.size(), 7);
  }

  @Test
  public void testAsString() throws Exception {
    assertEquals(this.wrapper.asString("//tei:imprint/tei:pubPlace"), "Augsburg");
  }

  @Test
  public void testAsBooleanTrue() throws Exception {
    assertEquals(true, this.wrapper.asBoolean("//tei:persName[@key='true']/text()"));
  }

  @Test
  public void testAsBooleanFalse() throws Exception {
    assertEquals(false, this.wrapper.asBoolean("//tei:persName[@key='false']/text()"));
  }

  @Test
  public void testAsNumber() throws Exception {
    assertEquals(this.wrapper.asNumber("//tei:classCode[@scheme='mdzProject']/tei:idno").intValue(), 1328176523);
  }

  @Test
  public void testGetDocument() throws Exception {
    assertEquals(this.wrapper.getDocument().getDocumentElement().getTagName(), "TEI");
  }

}
