package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathRoot {
  String defaultNamespace() default "";
  String[] value() default {};
}
