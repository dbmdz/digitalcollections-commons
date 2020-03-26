package de.digitalcollections.commons.xml.xpath;

@XPathRoot(
    value = "/outer",
    defaultNamespace = "http://www.tei-c.org/ns/1.0")
public class NestedOuterClass {

  @XPathRoot("/inner")
  NestedInnerClass nestedInnerClass;

  NestedInnerClass getNestedInnerClass() {
    return nestedInnerClass;
  }
}
