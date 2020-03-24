package de.digitalcollections.commons.xml.xpath;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


@SuppressWarnings("UnstableApiUsage")
public class XPathMapper<T> {
  // FIXME: Ideally the XPathMapper instance  should be independent of a Document so that it can be re-used for many
  //        documents. This means we have to either use a thread-local XPathWrapper or introduce a second class that
  //        performs the actual retrieval of the values from XML. This class should have no clue of reflection or
  //        other kinds of type wizardry, but expose simple methods like `readTemplated`, `readValue`,
  //        `readLocalizedValue`, etc.
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_-]+?)}");

  private final Class<T> type;
  private final XPathWrapper xpw;
  private final List<String> rootPaths;
  private final String defaultRootNamespace;

  private static final TypeToken<String> SINGLEVALUED_TYPE = new TypeToken<String>() {};
  private static final TypeToken<Map<Locale, ?>> LOCALIZED_TYPE = new TypeToken<Map<Locale, ?>>() {};
  private static final TypeToken<List<String>> MULTIVALUED_TYPE =
      new TypeToken<List<String>>() {};
  private static final TypeToken<Map<Locale, String>> LOCALIZED_SINGLEVALUED_TYPE =
      new TypeToken<Map<Locale, String>>() {};
  private static final TypeToken<Map<Locale, List<String>>> LOCALIZED_MULTIVALUED_TYPE =
      new TypeToken<Map<Locale, List<String>>>() {};

  /**
   * Convenience method to construct a temporary mapper and read a single document with it.
   *
   * @param doc the document for evaluation
   * @param targetType the target type, e.g. a mapper class
   * @param rootPaths optional array of root paths
   * @return The bind mapper with filled setters and fields, ready for getter consumption
   */
  public static <T> T readDocument(Document doc, Class<T> targetType, String... rootPaths) throws XPathMappingException {
    return new XPathMapper<>(targetType, doc, rootPaths, null).buildObject();
  }

  public XPathMapper(Class<T> type, Document doc, String[] rootPaths, String defaultRootNamespace) {
    this(type, new XPathWrapper(doc), rootPaths, defaultRootNamespace);
  }

  XPathMapper(Class<T> type, XPathWrapper xpw, String[] rootPaths, String defaultRootNamespace) {
    this.xpw = xpw;
    this.type = type;

    // Set default namespace
    if (defaultRootNamespace != null) {
      xpw.setDefaultNamespace(defaultRootNamespace);
    }

    XPathRoot rootAnnotation = type.getAnnotation(XPathRoot.class);
    // Determine the default namespace
    if (defaultRootNamespace != null) {
      this.defaultRootNamespace = defaultRootNamespace;
    } else if (rootAnnotation != null) {
      this.defaultRootNamespace = rootAnnotation.defaultNamespace();
    } else {
      this.defaultRootNamespace = "";
    }

    // If there are no user-suppplied root paths, we use those annotated on the type itself
    if (rootPaths.length > 0) {
      this.rootPaths = Arrays.asList(rootPaths);
    } else if (rootAnnotation != null) {
      this.rootPaths = Arrays.asList(rootAnnotation.value());
    } else {
      this.rootPaths = new ArrayList<>();
    }
  }


  public T buildObject() throws XPathMappingException {
    // Instantiate an empty target object
    T val;
    try {
      val = type.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new XPathMappingException(
          String.format("Cannot create an instance of %s, does the type have a public default constructor?",
              type.getName()), e);
    }

    // Evaluate all setters and fields annotated with @XPathBinding
    for (Method m : ReflectionUtils.getSettersAnnotatedWith(type, XPathBinding.class)) {
      XPathBinding binding = m.getDeclaredAnnotation(XPathBinding.class);
      Type paramType = m.getGenericParameterTypes()[0];
      try {
        m.setAccessible(true);
        m.invoke(val, readSimpleValue(binding, paramType));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new XPathMappingException(
            String.format("Cannot evaluate field=%s on %s: %s", m.getName(), paramType.getTypeName(), e));
      } catch (XPathExpressionException e) {
        throw new XPathMappingException(
            String.format("Invalid XPathExpression for field=%s on %s: %s", paramType.getTypeName(), type.getName(), e));
      }
    }
    for (Field fl : ReflectionUtils.getFieldsAnnotatedWith(type, XPathBinding.class)) {
      XPathBinding binding = fl.getDeclaredAnnotation(XPathBinding.class);
      Type fieldType = fl.getGenericType();
      try {
        fl.setAccessible(true);
        fl.set(val, readSimpleValue(binding, fieldType));
      } catch (IllegalAccessException e) {
        throw new XPathMappingException(
            String.format("Cannot evaluate field=%s on %s: %s", fl.getName(), fieldType.getTypeName(), e));
      } catch (XPathExpressionException e) {
        throw new XPathMappingException(
            String.format("Invalid XPathExpression for field=%s on %s: %s", fieldType.getTypeName(), type.getName(), e));
      }
    }

  // Evaluate all setters and fields that map nested types, i.e. are annotated with @XPathRoot
    for (Method m : ReflectionUtils.getSettersAnnotatedWith(type, XPathRoot.class)) {
      XPathRoot nestedRoot = m.getDeclaredAnnotation(XPathRoot.class);
      Class<?> argType = m.getParameterTypes()[0];
      try {
        m.setAccessible(true);
        m.invoke(val, readNestedValue(nestedRoot, argType));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new XPathMappingException("Failed to map " + m, e);
      }
    }
    for (Field fl : ReflectionUtils.getFieldsAnnotatedWith(type, XPathRoot.class)) {
      XPathRoot nestedRoot = fl.getDeclaredAnnotation(XPathRoot.class);
      Class<?> fieldType = fl.getType();
      try {
        fl.setAccessible(true);
        fl.set(val, readNestedValue(nestedRoot, fieldType));
      } catch (IllegalAccessException e) {
        throw new XPathMappingException("Failed to map " + fl, e);
      }
    }
    return val;
  }

  private Object readSimpleValue(XPathBinding binding, Type targetType)
      throws XPathMappingException, XPathExpressionException {
    verifyTargetType(targetType, binding);

    // Set of variable names from the template string
    Set<String> variables = getVariables(binding.valueTemplate());

    // Set of expressions for direct evaluation w/o templates
    Set<String> expressions = new HashSet<>(Arrays.asList(binding.value()));

    if (!isEmptyOrBlankStringSet(variables) && !binding.valueTemplate().isEmpty()
        && !isEmptyOrBlankStringSet(expressions)) {
      throw new XPathMappingException(
          "An @XPathBinding must have one of `variables` or `expressions`, but both were set!");
    }

    if (isEmptyOrBlankStringSet(variables) && binding.valueTemplate().isEmpty() && isEmptyOrBlankStringSet(expressions)) {
      throw new XPathMappingException(
          "An @XPathBinding must have one of `variables` or `expressions`, but neither were set!");
    }

    boolean multiLanguage = LOCALIZED_TYPE.isSupertypeOf(targetType);
    if (!isEmptyOrBlankStringSet(variables) && !binding.valueTemplate().isEmpty()) {
      // Complex template
      return evaluteVariablesAndTemplate(binding, variables, multiLanguage);
    } else {
      // Simple xpath -> value binding, possibly multi-valued
      boolean multiValue;
      if (multiLanguage) {
        multiValue = LOCALIZED_MULTIVALUED_TYPE.isSubtypeOf(targetType);
      } else {
        multiValue = MULTIVALUED_TYPE.isSubtypeOf(targetType);
      }
      return evaluateExpressions(prependWithRootPaths(expressions), multiValue, multiLanguage);
    }
  }

  private <S> S readNestedValue(XPathRoot root, Class<S> targetType)
      throws XPathMappingException {
    // For the embedded mappers, we have to concatenate the root paths
    String[] fullRoots = Arrays.stream(root.value())
        .flatMap(r -> rootPaths.stream().map(pr -> pr + r))
        .distinct()
        .toArray(String[]::new);
    String namespace = root.defaultNamespace();
    if (namespace.isEmpty() && !this.defaultRootNamespace.isEmpty()) {
      namespace = defaultRootNamespace;
    }
    try {
      XPathMapper<S> subMapper = new XPathMapper<>(targetType, xpw, fullRoots, namespace);
      return subMapper.buildObject();
    } catch (XPathMappingException e) {
      throw new XPathMappingException("Failed to map nested type " + targetType, e);
    }
  }

  private Set<String> getVariables(String templateString) {
    Matcher matcher = VARIABLE_PATTERN.matcher(templateString);
    Set<String> variables = new HashSet<>();
    while (matcher.find()) {
      variables.add(matcher.group(1));
    }
    return variables;
  }

  private Set<String> prependWithRootPaths(Set<String> expressions) {
    if (rootPaths == null || rootPaths.isEmpty()) {
      return expressions;
    }

    return expressions.stream()
        .flatMap(e -> rootPaths.stream()
            .map(r -> r + e))
        .collect(Collectors.toSet());
  }

  private Object evaluateExpressions(Set<String> expressions, boolean multiValueReturnType, boolean multiLanguage)  {
    Map<Locale, List<String>> resolved = resolveVariables(expressions.toArray(new String[]{}), multiValueReturnType);
    if (multiLanguage) {
      if (multiValueReturnType) {
        Map<Locale, List<String>> out = new HashMap<>();
        for (Entry<Locale, List<String>> resolvedVariable : resolved.entrySet()) {
          out.put(resolvedVariable.getKey(), resolvedVariable.getValue());
        }
        return out;
      } else {
        Map<Locale, String> out = new HashMap<>();
        for (Entry<Locale, List<String>> resolvedVariable : resolved.entrySet()) {
          // We only use the first match, since we want to return a single value
          out.put(resolvedVariable.getKey(), resolvedVariable.getValue().get(0));
        }
        return out;
      }
    } else if (!resolved.isEmpty()) {
      List<String> allValues = resolved.values().stream().flatMap(List::stream).collect(Collectors.toList());
      if (multiValueReturnType) {
        return allValues;
      } else {
        return allValues.get(0);
      }
    } else {
      return null;
    }
  }

  private Object evaluteVariablesAndTemplate(XPathBinding binding, Set<String> variables, boolean multiLanguage)
      throws XPathMappingException, XPathExpressionException {
    Map<String, Map<Locale, String>> resolvedVariables = new LinkedHashMap<>();
    for (String variableName : variables) {
      XPathVariable var = Arrays.stream(binding.variables())
          .filter(v -> v.name().equals(variableName))
          .findFirst()
          .orElseThrow(() -> new XPathMappingException(
              String.format("Could not resolve variable `%s`", variableName)));
      resolvedVariables.put(variableName, this.resolveVariable(var.paths()));
    }

    Map<Locale, String> resolved = this
        .executeTemplate(binding.valueTemplate(), resolvedVariables);
    if (multiLanguage) {
      return resolved;
    } else if (resolved != null && !resolved.isEmpty()) {
      return resolved.entrySet().iterator().next().getValue();
    } else {
      return null;
    }

  }

  private Map<Locale, String> executeTemplate(String templateString, Map<String, Map<Locale, String>> resolvedVariables) throws XPathExpressionException {
    Set<Locale> langs = resolvedVariables.values()
        .stream()
        .map(Map::keySet)  // Get set of languages for each resolved variable
        .flatMap(Collection::stream)  // Flatten these sets into a single stream
        .collect(Collectors.toCollection(LinkedHashSet::new));  // Store the stream in a set (thereby pruning duplicates)

    Map<Locale, String> out = new LinkedHashMap<>();
    // Resolve the <...> contexts
    for (Locale lang : langs) {
      String stringRepresentation = templateString;
      String context = extractContext(stringRepresentation);
      while (context != null) {
        stringRepresentation = stringRepresentation.replace(
            "<" + context + ">",
            resolveVariableContext(lang, context, resolvedVariables));
        context = extractContext(stringRepresentation);
      }

      // Now we just need to resolve top-level variables
      Matcher matcher = VARIABLE_PATTERN.matcher(stringRepresentation);
      while (matcher.find()) {
        String varName = matcher.group(1);
        if (resolvedVariables.get(varName).isEmpty()) {
          return null;
        }
        Locale langToResolve;
        if (resolvedVariables.get(varName).containsKey(lang)) {
          langToResolve = lang;
        } else {
          langToResolve = resolvedVariables.get(varName).entrySet().iterator().next().getKey();
        }
        stringRepresentation = stringRepresentation.replace(matcher.group(), resolvedVariables.get(varName).get(langToResolve));
        matcher = VARIABLE_PATTERN.matcher(stringRepresentation);
      }

      // And un-escape the pointy brackets
      out.put(lang, stringRepresentation.replace("\\<", "<").replace("\\>", ">"));
    }
    return out;
  }

  private String extractContext(String template) throws XPathExpressionException {
    StringBuilder ctx = new StringBuilder();
    boolean isEscaped = false;
    boolean wasOpened = false;
    int numOpen = 0;
    for (char c : template.toCharArray()) {
      if (c == '\\') {
        isEscaped = true;
        if (numOpen > 0) {
          ctx.append(c);
        }
      } else if (c == '<') {
        if (numOpen > 0) {
          ctx.append(c);
        }
        if (!isEscaped) {
          numOpen++;
          if (!wasOpened) {
            wasOpened = true;
          }
        } else {
          isEscaped = false;
        }
      } else if (c == '>') {
        if ((numOpen > 0 && isEscaped) || numOpen > 1) {
          ctx.append(c);
        }
        if (!isEscaped) {
          numOpen--;
          if (numOpen == 0) {
            return ctx.toString();
          }
        } else {
          isEscaped = false;
        }
      } else if (wasOpened) {
        ctx.append(c);
      }
    }
    if (wasOpened) {
      throw new XPathExpressionException(String.format(
          "Mismatched context delimiters, %s were unclosed at the end of parsing.", numOpen));
    } else {
      return null;
    }
  }

  private String resolveVariableContext(Locale language, String variableContext, Map<String, Map<Locale, String>> resolvedVariables) {
    Matcher varMatcher = VARIABLE_PATTERN.matcher(variableContext);
    varMatcher.find();
    String variableName = varMatcher.group(1);
    Map<Locale, String> resolvedValues = resolvedVariables.get(variableName);
    if (resolvedValues == null || resolvedValues.isEmpty()) {
      return "";
    } else if (resolvedValues.containsKey(language)) {
      return variableContext.replace(varMatcher.group(), resolvedValues.get(language));
    } else {
      return variableContext.replace(varMatcher.group(), resolvedValues.entrySet().iterator().next().getValue());
    }
  }

  private Map<Locale,String> resolveVariable(String[] paths) {
    Map<Locale,String> out = new HashMap<>();
    for (Entry<Locale,List<String>> resolvedVariable : resolveVariables(paths, false).entrySet()) {
      out.put(resolvedVariable.getKey(), resolvedVariable.getValue().get(0));
    }
    return out;
  }

  private Map<Locale, List<String>> resolveVariables(String[] paths, boolean multiValued) {
    Map<Locale, List<String>> result = new LinkedHashMap<>();
    for (String path : paths) {
      List<Node> nodes = xpw.asListOfNodes(path);
      for (Node node : nodes) {
        Locale locale = null;
        if (node.hasAttributes()) {
          Node langCode = node.getAttributes().getNamedItem("xml:lang");
          if (langCode != null) {
            locale = Locale.forLanguageTag(langCode.getNodeValue());
          }
        }
        if (locale == null || locale.getLanguage().isEmpty()) {
          locale = Locale.forLanguageTag("");
        }
        // For single valued: Only register value if we don't have one for the current locale
        String value = node.getTextContent()
            .replace("<", "\\<")
            .replace(">", "\\>");
        if (!multiValued) {
          if (!result.containsKey(locale)) {
            result.put(locale, Arrays.asList(value));
          }
        } else {
          List<String> valuesForLocale = result.get(locale);
          if (valuesForLocale == null) {
            valuesForLocale = new ArrayList<>();
          }
          if (!valuesForLocale.contains(value)) {
            valuesForLocale.add(value);
          }
          result.put(locale, valuesForLocale);
        }
      }
      if (!result.isEmpty()) {
        break;
      }
    }
    return result;
  }

  /**
   * Verification of the mapping target type.
   * <br/>
   * <ul>
   * <li>Multi language fields can be single- and multi-valued, so the valid types are
   * <code>Map&lt;Locale,String&gt;</code> and <code>Map&lt;Locale,List&lt;String&gt;&gt;</code>, but
   * multi valued content is only allowed on expressions, not in template evaluations.
   * <li>For a single valued, non localized field, it must be <code>String</code>
   * <li>For a multi valued, non localized field, it must be <code>List&lt;String&gt;</code>
   * </ul>
   * @param targetType the type that the binding should map to
   * @param binding the XPathBinding
   * @throws XPathMappingException if the validation did not pass
   */
  private void verifyTargetType(Type targetType, XPathBinding binding) throws XPathMappingException {
    boolean isMultiLocalized = LOCALIZED_MULTIVALUED_TYPE.isSubtypeOf(targetType);
    boolean isSingleLocalized = LOCALIZED_SINGLEVALUED_TYPE.isSubtypeOf(targetType);
    boolean isMultiValued = MULTIVALUED_TYPE.isSubtypeOf(targetType);
    boolean isSingleValued = SINGLEVALUED_TYPE.isSubtypeOf(targetType);
    boolean isTemplated = binding.valueTemplate().length() > 0;

    if (isTemplated && !(isSingleValued || isSingleLocalized)) {
      // Templates are only allowed on single valued return fields
      throw new XPathMappingException(String.format(
          "Templated binding methods must have a single %s or %s target type",
          SINGLEVALUED_TYPE, LOCALIZED_SINGLEVALUED_TYPE));
    }

    if (!isMultiLocalized && !isSingleLocalized && !isSingleValued && !isMultiValued) {
      throw new XPathMappingException(String.format(
          "Binding method has illegal target type, must be one of %s, %s, %s or %s",
          SINGLEVALUED_TYPE, MULTIVALUED_TYPE, LOCALIZED_SINGLEVALUED_TYPE,
          LOCALIZED_MULTIVALUED_TYPE));
    }
  }

  /**
   * @param set A set with string elements
   * @return true, when the set is empty or contains just a single empty string
   */
  private boolean isEmptyOrBlankStringSet(Set<String> set) {
    return set.stream()
        .filter(Objects::nonNull)
        .allMatch(String::isEmpty);
  }
}
