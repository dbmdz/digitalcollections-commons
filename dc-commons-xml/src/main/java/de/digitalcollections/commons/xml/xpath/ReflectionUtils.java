package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Some helper methods to deal with reflection
 */
class ReflectionUtils {

  /**
   * @param type The type to be evaluated
   * @param annotation the annotation class
   * @return a list of all methods (including inherited ones), which carry the given annotation
   */
  public static Set<Method> getSettersAnnotatedWith(Class<?> type, final Class<? extends Annotation> annotation) {
    Set<Method> methods = new HashSet<>();
    // Iterate through hierarchy to get inherited methods, too
    while (type != Object.class) {
      Arrays.stream(type.getDeclaredMethods())
          .filter(m -> m.getName().startsWith("set"))
          .filter(m -> m.getParameterTypes().length == 1)
          .filter(m -> m.isAnnotationPresent(annotation))
          .forEach(methods::add);
      type = type.getSuperclass();
    }
    return methods;
  }

  /**
   * @param type The type to be evaluated
   * @param annotation the annotation class
   * @return a list of all fields (including inherited ones), which carry the given annotation
   */
  public static Set<Field> getFieldsAnnotatedWith(Class<?> type, final Class<? extends Annotation> annotation) {
    Set<Field> set = new HashSet<>();
    // Iterate through hierarchy to get inherited fields, too
    while (type != null) {
      Arrays.stream(type.getDeclaredFields())
          .filter(f -> f.isAnnotationPresent(annotation))
          .forEach(set::add);
      type = type.getSuperclass();
    }
    return set;
  }
}
