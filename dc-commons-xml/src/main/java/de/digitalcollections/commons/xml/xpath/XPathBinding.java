package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathBinding {
  String valueTemplate() default "";
  String defaultNamespace() default "";

  /**
   * @deprecated
   * Specifying the return type is sufficient, this field will be removed in future and is simply ignored for now.
   */
  @Deprecated
  boolean multiLanguage() default false;

  XPathVariable[] variables() default {};
  String[] expressions() default "";
}
