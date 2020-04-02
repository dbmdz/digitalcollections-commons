package de.digitalcollections.commons.xml.xpath;

public class NestedInnerClass {

  @XPathBinding("/author")
  String author;

  String getAuthor() {
    return author;
  }
}
