package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define XPathVariables for templating.
 * <p>
 * An XPathVariable consists of its name (used for template placeholders)
 * and an array of associated XPath expressions.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathVariable {

  /**
   * @return the name of the XPathVariable
   */
  String name();

  /**
   * @return an Array of XPath expressions
   */
  String[] paths();
}
