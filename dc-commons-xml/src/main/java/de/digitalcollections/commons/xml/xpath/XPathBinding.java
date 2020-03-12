package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for retrieving content by XPath expressions
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathBinding {

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
   * @return the default namespace, e.g. <code>http://www.tei-c.org/ns/1.0"</code> (optional)
   */
  String defaultNamespace() default "";

  /**
   * @deprecated
   * Specifying the return type is sufficient, this field will be removed in future and is simply ignored for now.
   *
   * @return optional flag, if multi language evaluation is requested.
   */
  @Deprecated
  boolean multiLanguage() default false;

  /**
   * @return a set of {@link XPathVariable} annotations (optional). If you use a value template, you
   *     must define the variables.
   */
  XPathVariable[] variables() default {};

  /**
   * @return a set of XPath expressions (optional). Use this instead of {@link variables}, if you
   *     don't use templating.
   */
  String[] expressions() default "";
}
