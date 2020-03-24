package de.digitalcollections.commons.xml.xpath;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Some helper methods to deal with reflection
 */
public class ReflectionUtils {

  /**
   * @param type The type to be evaluated
   * @param annotation the annotation class
   * @return a list of all methods (including inherited ones), which carry the given annotation
   */
  public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
    final List<Method> methods = new ArrayList<Method>();
    Class<?> c = type;
    // Iterate through hierarchy to get inherited methods, too
    while (c != Object.class) {
      final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(c.getDeclaredMethods()));
      Arrays.stream(c.getDeclaredMethods())
          .filter(m -> m.isAnnotationPresent(annotation))
          .forEach(m -> {
            methods.add(m);
          });
      c = c.getSuperclass();
    }
    return methods;
  }

  /**
   * @param type The type to be evaluated
   * @param annotation the annotation class
   * @return a list of all fields (including inherited ones), which carry the given annotation
   */
  public static Set<Field> getFieldsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
    Set<Field> set = new HashSet<>();
    Class<?> c = type;
    // Iterate through hierarchy to get inherited fields, too
    while (c != null) {
      Arrays.stream(c.getDeclaredFields())
          .filter(f -> f.isAnnotationPresent(annotation))
          .forEach(f -> set.add(f));
      c = c.getSuperclass();
    }
    return set;
  }

  /**
   * Ensure access for the given field. It the modifier is unset or private, access will be given.
   * @param f the field
   */
  public static void ensureAccess(Field f) {
    if (f.getModifiers() == Modifier.PRIVATE || f.getModifiers() == 0) {
      f.setAccessible(true);
    }
  }

  /**
   * Ensure access for the given method
   * @param m the method
   */
  public static void ensureAccess(Method m) {
    if (m.getModifiers() == Modifier.PRIVATE || m.getModifiers() == 0) {
      m.setAccessible(true);
    }
  }

}
