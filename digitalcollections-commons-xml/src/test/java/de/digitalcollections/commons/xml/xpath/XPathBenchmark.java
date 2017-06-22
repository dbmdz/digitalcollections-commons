package de.digitalcollections.commons.xml.xpath;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XPathBenchmark {

  //private static final String DTM_MANAGER_PROP_NAME = "com.sun.org.apache.xml.internal.dtm.DTMManager";
  //private static final String DTM_MANAGER_CLASS_NAME = "com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault";
  //private static final String DTM_MANAGER_PROP_NAME = "org.apache.xml.dtm.DTMManager";
  //private static final String DTM_MANAGER_CLASS_NAME = "org.apache.xml.dtm.ref.DTMManagerDefault";
  private static final String TEST_PATH = "/tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:title";

  public static XPathWrapper getWrapper() throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bsbstruc.xml");
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

  public static void main(String[] args) throws Exception {
    System.out.println("Warming up the JIT compiler...");
    runBench();
    for (int i = 0; i < 10; i++) {
      System.out.println(runBench());
    }
  }
}
