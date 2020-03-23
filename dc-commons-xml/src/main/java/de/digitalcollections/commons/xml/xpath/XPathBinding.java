package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for retrieving content by XPath expressions
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathBinding {

  /**
   * @return a set of XPath expressions (optional). Use this instead of {@link variables}, if you
   *     don't use templating.
   */
  String[] value() default "";

  /*
   * A value template consists of a string with one or more placeholders ({@link XPathVariable
   * .name()}) in brackets, e.g.
   * <code>{author}</code> and additional characters inbetween. Parts, which are surrounded by
   * angle brackets are optional, which means, their contents are left out, when the
   * placeholder is not filled.
   * <p>
   * Example: <code>{title}lt;: {subtitle}&gt;&lt; [. {partNumber}&lt;, {partTitle}&gt;]&gt;</code>
   * @return the value template definition. (optional)
   */
  String valueTemplate() default "";

  /**
   * @return a set of {@link XPathVariable} annotations (optional). If you use a value template, you
   *     must define the variables.
   */
  XPathVariable[] variables() default {};
}
