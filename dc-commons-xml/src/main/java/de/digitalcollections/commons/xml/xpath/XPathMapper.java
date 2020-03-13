package de.digitalcollections.commons.xml.xpath;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
import java.util.stream.Stream;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


@SuppressWarnings("UnstableApiUsage")
public class XPathMapper implements InvocationHandler {

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_-]+?)\\}");

  private final XPathWrapper xpw;
  private final Set<String> rootPaths;
  private final String defaultRootNamespace;

  private static final TypeToken<String> SINGLEVALUED_RETURN_TYPE = new TypeToken<String>() {};
  private static final TypeToken<Map<Locale, ?>> LOCALIZED_RETURN_TYPE = new TypeToken<Map<Locale, ?>>() {};
  private static final TypeToken<List<String>> MULTIVALUED_RETURN_TYPE =
      new TypeToken<List<String>>() {};
  private static final TypeToken<Map<Locale, String>> LOCALIZED_SINGLEVALUED_RETURN_TYPE =
      new TypeToken<Map<Locale, String>>() {};
  private static final TypeToken<Map<Locale, List<String>>> LOCALIZED_MULTIVALUED_RETURN_TYPE =
      new TypeToken<Map<Locale, List<String>>>() {};

  @SuppressWarnings("unchecked")
  public static <T> T makeProxy(Document doc, Class<? extends T> iface, Class<?>... otherIfaces) {
    Class<?>[] allInterfaces = Stream
        .concat(Stream.of(iface), Stream.of(otherIfaces))
        .distinct()
        .toArray(Class<?>[]::new);
    XPathRoot xPathRootAnnotation = iface.getAnnotation(XPathRoot.class);
    if (xPathRootAnnotation != null) {
      // XPathRoot annotation on type level sets root paths and default namespace, when defined
      return (T) Proxy.newProxyInstance(iface.getClassLoader(), allInterfaces, new XPathMapper(doc,
          xPathRootAnnotation.value(), xPathRootAnnotation.defaultNamespace()));
    } else {
      return (T) Proxy.newProxyInstance(iface.getClassLoader(), allInterfaces, new XPathMapper(doc,
          new String[]{""}, ""));
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T makeProxy(Document doc, Class<? extends T> iface, Set<String>rootPaths,
      String defaultRootNamespace) {

    return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, new XPathMapper(doc,
        rootPaths.toArray(new String[rootPaths.size()]), defaultRootNamespace));
  }

  private XPathMapper(Document doc, String[] rootPaths, String defaultRootNamespace) {
    this.xpw = new XPathWrapper(doc);
    this.rootPaths = new HashSet<>(Arrays.asList(prependWithRootPaths(rootPaths)));
    this.defaultRootNamespace = defaultRootNamespace;
  }

  public Set<String> getVariables(String templateString) {
    Matcher matcher = VARIABLE_PATTERN.matcher(templateString);
    Set<String> variables = new HashSet<>();
    while (matcher.find()) {
      variables.add(matcher.group(1));
    }
    return variables;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    XPathBinding binding = method.getAnnotation(XPathBinding.class);
    XPathRoot root = method.getAnnotation(XPathRoot.class);
    if (binding == null && root == null) {
      throw new XPathMappingException("No @XPathBinding or @XPathRoot annotation was found on the "
          + "specified method!");
    }

    if (root != null) {
      // We have an XPathRoot binding on a method, so we have to return a proxy method
      return makeProxyForMethodMapper(method);
    }

    // Set of variable names from the template string
    Set<String> variables = getVariables(binding.valueTemplate());

    // Set of expressions for direct evaluation w/o templates
    Set<String> expressions = new HashSet<>(Arrays.asList(binding.value()));

    // Sanity checks
    verifyReturnType(method, binding);
    if (!isEmptyOrBlankStringSet(variables) && !binding.valueTemplate().isEmpty() && !isEmptyOrBlankStringSet(expressions)) {
      throw new XPathMappingException("Only one XPath evaluation type, either variables or expressions, is allowed, not both at the same time!");
    }
    if (isEmptyOrBlankStringSet(variables) && binding.valueTemplate().isEmpty() && isEmptyOrBlankStringSet(expressions)) {
      throw new XPathMappingException("Either variables or expressions must be used, not none of them!");
    }

    // Set default namespace, if applicable
    if (defaultRootNamespace != null && defaultRootNamespace.length() > 0) {
      xpw.setDefaultNamespace(defaultRootNamespace);
    }

    // Resolve variables, if variables and valueTemplate are set. Otherwise, evalute expressions
    boolean multiLanguage = LOCALIZED_RETURN_TYPE.isSupertypeOf(method.getGenericReturnType());
    if (!isEmptyOrBlankStringSet(variables) && !binding.valueTemplate().isEmpty()) {
      return evaluteVariablesAndTemplate(binding, variables, multiLanguage);
    } else {
      boolean multiValue;
      if (multiLanguage) {
        multiValue = LOCALIZED_MULTIVALUED_RETURN_TYPE.isSubtypeOf(method.getGenericReturnType());
      } else {
        multiValue = MULTIVALUED_RETURN_TYPE.isSubtypeOf(method.getGenericReturnType());
      }
      return evaluateExpressions(prependWithRootPaths(expressions), multiValue, multiLanguage);
    }
  }

  private Object makeProxyForMethodMapper(Method method)
      throws XPathMappingException {
    XPathRoot root = method.getAnnotation(XPathRoot.class);

    // Since XPathRoot was set on a method, we must ensure, that this method returns an interface
    // with at least one method, which is annotated with an XPathBinding
    verifyReturnTypeForHierarchy(method);

    // On methods, no DefaultNamespace must be set
    if (!root.defaultNamespace().isEmpty()) {
      throw new XPathMappingException("Default namespace can only be set on type level "
          + "@XPathRoot annotation, not on method level.");
    }

    Set<String> combinedMethodPaths;
    Set<String> methodPaths = new HashSet<>(Arrays.asList(root.value()));
    if (!rootPaths.isEmpty()) {
      combinedMethodPaths = rootPaths.stream()
          .flatMap(r -> methodPaths.stream()
              .map(m -> r + m))
              .collect(Collectors.toSet());
    } else {
      combinedMethodPaths = methodPaths;
    }

    return makeProxy(xpw.getDocument(), method.getReturnType(),
        combinedMethodPaths, defaultRootNamespace);
  }

  private String[] prependWithRootPaths(String[] paths) {
    Set<String> prependedPaths =
        prependWithRootPaths(new HashSet<>(Arrays.asList(paths)));
    return prependedPaths.toArray(new String[prependedPaths.size()]);
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

  private void verifyReturnTypeForHierarchy(Method method) throws XPathMappingException {
    Class childClass = method.getReturnType();
    List<Method> childMethods = new ArrayList<>(Arrays.asList(childClass.getDeclaredMethods()));
    for (Method childMethod : childMethods) {
      if (childMethod.isAnnotationPresent(XPathBinding.class)) {
        return;     // At least one child method contains an XPathBinding.
      }
    }
    throw new XPathMappingException("Childs must contain at least one method with @XPathBinding "
        + "annotation");
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
   * Verification of the return type.
   * <br/>
   * <ul>
   * <li>Multi language fields can return single and multi valued content, so the return types are
   * <code>Map&lt;Locale,String&gt;</code> and <code>Map&lt;Locale,List&lt;String&gt;&gt;</code>, but
   * multi valued content is only allowed on expressions, not in template evaluations.
   * <li>For a single valued, non localized field, it must be <code>String</code>
   * <li>For a multi valued, non localized field, it must be <code>List&lt;String&gt;</code>
   * </ul>
   * @param method the invoked method
   * @param binding the XPathBinding
   * @throws XPathMappingException if the validation did not pass
   */
  private void verifyReturnType(Method method, XPathBinding binding) throws XPathMappingException {
    // If we use a multiLanguage field, we ensure, that the return type is Map<Locale,String> or Map<Locale,List<String>>
    final Type returnType = method.getGenericReturnType();
    boolean isMultiLocalized = LOCALIZED_MULTIVALUED_RETURN_TYPE.isSubtypeOf(returnType);
    boolean isSingleLocalized = LOCALIZED_SINGLEVALUED_RETURN_TYPE.isSubtypeOf(returnType);
    boolean isMultiValued = MULTIVALUED_RETURN_TYPE.isSubtypeOf(returnType);
    boolean isSingleValued = SINGLEVALUED_RETURN_TYPE.isSubtypeOf(returnType);
    boolean isTemplated = binding.valueTemplate().length() > 0;

    if (isTemplated && !(isSingleValued || isSingleLocalized)) {
        // Templates are only allowed on single valued return fields
      throw new XPathMappingException(String.format(
          "Templated binding methods must have a %s or %s return type",
          SINGLEVALUED_RETURN_TYPE, LOCALIZED_SINGLEVALUED_RETURN_TYPE));
    }

    if (!isMultiLocalized && !isSingleLocalized && !isSingleValued && !isMultiValued) {
      throw new XPathMappingException(String.format(
          "Binding method has illegal return type, must be one of %s, %s, %s or %s",
          SINGLEVALUED_RETURN_TYPE, MULTIVALUED_RETURN_TYPE, LOCALIZED_SINGLEVALUED_RETURN_TYPE,
          LOCALIZED_MULTIVALUED_RETURN_TYPE));
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
