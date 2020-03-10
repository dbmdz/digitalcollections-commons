package de.digitalcollections.commons.xml.xpath;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class XPathMapper implements InvocationHandler {

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_-]+?)\\}");


  private final XPathWrapper xpw;

  @SuppressWarnings("unchecked")
  public static <T> T makeProxy(Document doc, Class<? extends T> iface, Class<?>... otherIfaces) {
    Class<?>[] allInterfaces = Stream
        .concat(Stream.of(iface), Stream.of(otherIfaces))
        .distinct()
        .toArray(Class<?>[]::new);
    return (T) Proxy.newProxyInstance(iface.getClassLoader(), allInterfaces, new XPathMapper(doc));
  }

  private XPathMapper(Document doc) {
    this.xpw = new XPathWrapper(doc);
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
    if (binding == null) {
      throw new XPathMappingException("No @XPathBinding annotation was found on the specified method!");
    }

    // Set of variable names from the template string
    Set<String> variables = getVariables(binding.valueTemplate());

    // Set of expressions from the expression string for direct evaluation w/o templates
    Set<String> expressions = new HashSet<>(Arrays.asList(binding.expressions()));


    // Sanity checks
    if (binding.multiLanguage()) {
      if (!method.getReturnType().isAssignableFrom(Map.class)) {
        throw new XPathMappingException(
            "Method return type must be a Map<Locale, String> type if multiLanguage=true.");
      }
    } else {
      if (!method.getReturnType().isAssignableFrom(String.class)) {
        throw new XPathMappingException("Return type must be String if multiLanguage=false;");
      }
    }
    if (!isEmptyOrBlankStringSet(variables) && !binding.valueTemplate().isEmpty() && !isEmptyOrBlankStringSet(expressions)) {
      throw new XPathMappingException("Only one XPath evaluation type, either variables or expressions, is allowed, not both at the same time!");
    }
    if (isEmptyOrBlankStringSet(variables) && binding.valueTemplate().isEmpty() && isEmptyOrBlankStringSet(expressions)) {
      throw new XPathMappingException("Either variables or expressions must be used, not none of them!");
    }

    // Set default namespace, if applicable
    if (!binding.defaultNamespace().isEmpty()) {
      xpw.setDefaultNamespace(binding.defaultNamespace());
    }

    // Resolve variables, if variables and valueTemplate are set
    if (!isEmptyOrBlankStringSet(variables) && !binding.valueTemplate().isEmpty()) {
      return evaluteVariablesAndTemplate(binding, binding.valueTemplate(), variables);
    } else {
      return evaluateExpressions(binding, expressions);
    }
  }


  private Object evaluateExpressions(XPathBinding binding, Set<String> expressions) throws XPathExpressionException {
    Map<Locale, String> resolved = resolveVariable(expressions.toArray(new String[expressions.size()]));
    if (binding.multiLanguage()) {
      return resolved;
    } else if (!resolved.isEmpty()) {
      return resolved.entrySet().iterator().next().getValue();
    } else {
      return null;
    }
  }

  private Object evaluteVariablesAndTemplate(XPathBinding binding, String valueTemplate, Set<String> variables)
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
        .executeTemplate(valueTemplate, resolvedVariables);
    if (binding.multiLanguage()) {
      return resolved;
    } else if (!resolved.isEmpty()) {
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

  private Map<Locale, String> resolveVariable(String[] paths) throws XPathExpressionException {
    Map<Locale, String> result = new LinkedHashMap<>();
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
        // Only register value if we don't have one for the current locale
        if (!result.keySet().contains(locale)) {
          String value = node.getTextContent()
              .replace("<", "\\<")
              .replace(">", "\\>");
          result.put(locale, value);
        }
      }
      if (!result.isEmpty()) {
        break;
      }
    }
    return result;
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
