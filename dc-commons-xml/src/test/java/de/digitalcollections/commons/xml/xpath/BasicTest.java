package de.digitalcollections.commons.xml.xpath;

import de.digitalcollections.commons.xml.namespaces.DigitalCollectionsNamespaceContext;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.sf.saxon.xpath.XPathEvaluator;
import net.sf.saxon.xpath.XPathFactoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class BasicTest {

  @DisplayName("shall test basic XPath framework")
  @Test
  public void testXPath() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("bsbstruc.xml");
    Document doc = db.parse(is);

    XPathFactory xPathFactory = new XPathFactoryImpl();
    XPath xpath = xPathFactory.newXPath();
    ((XPathEvaluator) xpath)
        .getStaticContext()
        .setDefaultElementNamespace(DigitalCollectionsNamespaceContext.TEI_NS_URI);
    XPathExpression expr = xpath.compile("/TEI/teiHeader/fileDesc/titleStmt/title");
    try {
      String title = (String) expr.evaluate(doc, XPathConstants.STRING);
      Assertions.assertEquals(
          "Kugelmann, Hans: CONCENTVS NOVI, TRIVM VOCVM, Ecclesiarum usui in Prussia pręcipue accomodati. IOANNE\n"
              + "          KVGELMANNO, Tubicinae Symphoniarũ authore. News Gesanng mit Dreyen stymmen Den Kirchen vñ Schůlen zu nutz\n"
              + "          newlich in Preüssen durch Ioannem Kugelman Gesetzt. Item Etliche Stuck mit Acht Sechs Fünf vnd Vier Stym̃en\n"
              + "          hinzu̇ gethan",
          title.trim());
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
