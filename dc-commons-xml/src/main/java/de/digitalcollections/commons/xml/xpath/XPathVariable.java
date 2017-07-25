package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XPathVariable {
  String name();
  String[] paths();
}
