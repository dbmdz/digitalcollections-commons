package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to set the default namespace and path prefixes for all subsequent {@link XPathBinding}
 * annotations.
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathRoot {
  /**
   * Definition of path prefixes, e.g. <code>/tei:TEI/tei:teiHeader/tei:fileDesc/tei
   *     :sourceDesc/tei:listBibl/tei:biblStruct</code>, which will be prepended to all paths and
   * expressions of the subsequent {@link XPathBinding} annotations.
   *
   * <p>Each path prefix will be prepended to each path of the bindings.
   *
   * <p>Parent root paths are prepended.
   *
   * @return an array of path prefixes (optional; if unset, a blank root path prefix is used)
   */
  String[] value() default {};

  /**
   * The default namespace is only allowed on type level, not on methods.
   *
   * @return the default namespace, e.g. <code>http://www.tei-c.org/ns/1.0"</code> (optional)
   */
  String defaultNamespace() default "";
}
