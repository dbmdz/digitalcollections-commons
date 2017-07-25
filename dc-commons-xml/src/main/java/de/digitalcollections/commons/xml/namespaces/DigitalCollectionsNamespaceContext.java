package de.digitalcollections.commons.xml.namespaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class DigitalCollectionsNamespaceContext implements NamespaceContext {

  public static final String TEI_NS_PREFIX = "tei";
  public static final String TEI_NS_URI = "http://www.tei-c.org/ns/1.0";

  public static final String OAI_NS_PREFIX = "oai";
  public static final String OAI_NS_URI = "http://www.openarchives.org/OAI/2.0/";

  public static final String XSI_NS_PREFIX = "xsi";
  public static final String XSI_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";

  public static final String OAI_DC_NS_PREFIX = "oai_dc";
  public static final String OAI_DC_NS_URI = "http://www.openarchives.org/OAI/2.0/oai_dc/";

  public static final String DC_NS_PREFIX = "dc";
  public static final String DC_NS_URI = "http://purl.org/dc/elements/1.1/";

  public static final String MDZ_NS_PREFIX = "mdz";
  public static final String MDZ_NS_URI = "http://www.digitale-sammlungen.de";

  public static final String MDZ2_NS_PREFIX = "mdz";
  public static final String MDZ2_NS_URI = "http://www.digitale-sammlungen.de/";

  public static final String ATOM_NS_PREFIX = "atom";
  public static final String ATOM_NS_URI = "http://www.w3.org/2005/Atom";

  public static final String BAV_NS_PREFIX = "bav";
  public static final String BAV_NS_URI = "http://rest.digitale-sammlungen.de/kpb/schema/bavEDM";

  public static final String FOXML_NS_PREFIX = "foxml";
  public static final String FOXML_NS_URI = "http://www.fedora.info/definitions/1/0/foxml1-1.xsd";

  public static final String SVG_NS_PREFIX = "svg";
  public static final String SVG_NS_URI = "http://www.w3.org/2000/svg";

  public static final String XLINK_NS_PREFIX = "xlink";
  public static final String XLINK_NS_URI = "http://www.w3.org/1999/xlink";

  public static final String RDF_NS_PREFIX = "rdf";
  public static final String RDF_NS_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

  private final Map<String, String> namespaces;

  public DigitalCollectionsNamespaceContext() {
    this(XMLConstants.NULL_NS_URI);
  }

  public DigitalCollectionsNamespaceContext(String defaultURI) {

    Map<String, String> map = new HashMap<>();

    map.put(XMLConstants.DEFAULT_NS_PREFIX, defaultURI);
    map.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    map.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.TEI_NS_PREFIX, DigitalCollectionsNamespaceContext.TEI_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.DC_NS_PREFIX, DigitalCollectionsNamespaceContext.DC_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.OAI_DC_NS_PREFIX, DigitalCollectionsNamespaceContext.OAI_DC_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.OAI_NS_PREFIX, DigitalCollectionsNamespaceContext.OAI_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.XSI_NS_PREFIX, DigitalCollectionsNamespaceContext.XSI_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.ATOM_NS_PREFIX, DigitalCollectionsNamespaceContext.ATOM_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.FOXML_NS_PREFIX, DigitalCollectionsNamespaceContext.FOXML_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.SVG_NS_PREFIX, DigitalCollectionsNamespaceContext.SVG_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.XLINK_NS_PREFIX, DigitalCollectionsNamespaceContext.XLINK_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.MDZ2_NS_PREFIX, DigitalCollectionsNamespaceContext.MDZ2_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.BAV_NS_PREFIX, DigitalCollectionsNamespaceContext.BAV_NS_URI);
    map.put(DigitalCollectionsNamespaceContext.RDF_NS_PREFIX, DigitalCollectionsNamespaceContext.RDF_NS_URI);

    this.namespaces = Collections.unmodifiableMap(map);
  }

  /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
   */
  @Override
  public String getNamespaceURI(String prefix) {
    if (prefix == null) {
      throw new IllegalArgumentException("String prefix must not be null.");
    }
    if (!namespaces.containsKey(prefix.toLowerCase())) {
      return XMLConstants.NULL_NS_URI;
    } else {
      return namespaces.get(prefix.toLowerCase());
    }
  }

  /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
   */
  @Override
  public String getPrefix(String namespaceURI) {
    if (namespaceURI == null) {
      throw new IllegalArgumentException();
    }
    for (Entry<String, String> e : this.namespaces.entrySet()) {
      if (namespaceURI.equals(e.getValue())) {
        return e.getKey();
      }
    }
    return null;
  }

  /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
   */
  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    if (namespaceURI == null) {
      throw new IllegalArgumentException();
    }
    List<String> prefixes = new ArrayList<>();
    for (Entry<String, String> e : this.namespaces.entrySet()) {
      if (namespaceURI.equals(e.getValue())) {
        prefixes.add(e.getKey());
      }
    }
    return Collections.unmodifiableList(prefixes).iterator();
  }

}
