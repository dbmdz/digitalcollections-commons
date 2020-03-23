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
   * @return a list of all methods, which carry the given annotation
   */
  public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
    final List<Method> methods = new ArrayList<Method>();
    Class<?> klass = type;
    while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
      // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
      final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
      for (final Method method : allMethods) {
        if (method.isAnnotationPresent(annotation)) {
          Annotation annotInstance = method.getAnnotation(annotation);
          // TODO process annotInstance
          methods.add(method);
        }
      }
      // move to the upper class in the hierarchy in search for more methods
      klass = klass.getSuperclass();
    }
    return methods;
  }

  /**
   * @param type The type to be evaluated
   * @param annotation the annotation class
   * @return a list of all fields, which carry the given annotation
   */
  public static Set<Field> findFields(final Class<?> type, final Class<? extends Annotation> annotation) {
    Set<Field> set = new HashSet<>();
    Class<?> c = type;
    while (c != null) {
      for (Field field : c.getDeclaredFields()) {
        if (field.isAnnotationPresent(annotation)) {
          set.add(field);
        }
      }
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
