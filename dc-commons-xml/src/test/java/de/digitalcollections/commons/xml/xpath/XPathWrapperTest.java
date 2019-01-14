package de.digitalcollections.commons.xml.xpath;

import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.assertj.core.api.Assertions.assertThat;

public class XPathWrapperTest {

  private XPathWrapper wrapper;

  @BeforeEach
  public void setUp() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bsbstruc.xml");
    Document doc = db.parse(is);
    this.wrapper = new XPathWrapper(doc);
  }

  @Test
  public void testAsNode() throws Exception {
    Node node = this.wrapper.asNode("//tei:idno[@type='oclc']");
    assertThat(node.getLocalName()).isEqualTo("idno");
    assertThat(node.getTextContent()).isEqualTo("165600186");
  }

  @Test
  public void testAsNodeFromIndex() throws Exception {
    Node node = this.wrapper.asNode("//tei:idno", 1);
    assertThat(node.getTextContent()).isEqualTo("1900440");
  }

  @Test
  public void testAsNodeList() throws Exception {
    NodeList nodes = this.wrapper.asNodeList("//tei:biblStruct/tei:idno");
    assertThat(nodes.getLength()).isEqualTo(7);
  }

  @Test
  public void testAsListOfNodes() throws Exception {
    List<Node> nodes = this.wrapper.asListOfNodes("//tei:biblStruct/tei:idno");
    assertThat(nodes.size()).isEqualTo(7);
  }

  @Test
  public void testAsString() throws Exception {
    assertThat(this.wrapper.asString("//tei:imprint/tei:pubPlace")).isEqualTo("Augsburg");
  }

  @Test
  public void testAsBooleanTrue() throws Exception {
    assertThat(this.wrapper.asBoolean("//tei:persName[@key='true']/text()")).isTrue();
  }

  @Test
  public void testAsBooleanFalse() throws Exception {
    assertThat(this.wrapper.asBoolean("//tei:persName[@key='false']/text()")).isFalse();
  }

  @Test
  public void testAsNumber() throws Exception {
    assertThat(this.wrapper.asNumber("//tei:classCode[@scheme='mdzProject']/tei:idno").intValue()).isEqualTo(1328176523);
  }

  @Test
  public void testGetDocument() throws Exception {
    assertThat(this.wrapper.getDocument().getDocumentElement().getTagName()).isEqualTo("TEI");
  }

}
