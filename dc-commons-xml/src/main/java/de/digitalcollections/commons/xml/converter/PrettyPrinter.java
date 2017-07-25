package de.digitalcollections.commons.xml.converter;

import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class PrettyPrinter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrettyPrinter.class);

  public static String toString(Node node) {
    try {
      Transformer tf = TransformerFactory.newInstance().newTransformer();
      tf.setOutputProperty(OutputKeys.INDENT, "yes");
      tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      Writer out = new StringWriter();
      tf.transform(new DOMSource(node), new StreamResult(out));
      return out.toString();
    } catch (TransformerException ex) {
      LOGGER.error("ERROR converting Document to String", ex);
      return null;
    }
  }
}
