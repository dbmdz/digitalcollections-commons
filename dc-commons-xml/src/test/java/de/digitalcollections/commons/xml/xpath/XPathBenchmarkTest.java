package de.digitalcollections.commons.xml.xpath;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XPathBenchmarkTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(XPathBenchmarkTest.class);

  private static final String TEST_PATH =
      "/tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:title";

  public static XPathWrapper getWrapper()
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("bsbstruc.xml");
    Document doc = db.parse(is);
    return new XPathWrapper(doc);
  }

  public static Duration runBench() throws Exception {
    XPathWrapper xpw = getWrapper();
    // Once to fill the cache
    xpw.asString(TEST_PATH);

    Instant start = Instant.now();
    for (int i = 0; i < 100000; i++) {
      xpw.asString("/tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:title");
    }
    Instant end = Instant.now();
    return Duration.between(start, end);
  }

  @Test
  public void runBenchmark() throws Exception {
    LOGGER.info("Warming up the JIT compiler...");
    runBench();
    for (int i = 0; i < 10; i++) {
      LOGGER.info(runBench().toString());
    }
  }
}
