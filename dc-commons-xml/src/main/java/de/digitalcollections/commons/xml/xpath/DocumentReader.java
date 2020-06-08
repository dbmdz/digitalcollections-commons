package de.digitalcollections.commons.xml.xpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.xpath.XPathExpressionException;
import net.sf.saxon.dom.DOMNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Helper class to read simple or templated values from an XML document. */
class DocumentReader {
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_-]+?)}");
  private final List<String> rootPaths;
  private final XPathWrapper xpw;
  private static final Locale EMPTY_LOCALE = determineLocaleFromCode("");

  public DocumentReader(Document doc, List<String> rootPaths, String namespace) {
    this.rootPaths = rootPaths;
    this.xpw = new XPathWrapper(doc);
    if (namespace != null) {
      xpw.setDefaultNamespace(namespace);
    }
  }

  public Document getDocument() {
    return xpw.getDocument();
  }

  public String readValue(List<String> expressions) throws XPathMappingException {
    return resolveVariablesAsStrings(expressions.toArray(new String[] {}), false).stream()
        .findFirst()
        .orElse(null);
  }

  public List<String> readValues(List<String> expressions) throws XPathMappingException {
    return resolveVariablesAsStrings(expressions.toArray(new String[] {}), true);
  }

  public Map<Locale, String> readLocalizedValue(List<String> expressions)
      throws XPathMappingException {
    return resolveLocalizedVariable(expressions.toArray(new String[] {}));
  }

  public Map<Locale, List<String>> readLocalizedValues(List<String> expressions)
      throws XPathMappingException {
    return resolveLocalizedVariable(expressions.toArray(new String[] {}), true);
  }

  public List<Element> readElementList(List<String> expressions) throws XPathMappingException {
    return resolveVariableAsElements(expressions.toArray(new String[] {}));
  }

  public String readTemplateValue(String template, List<XPathVariable> variables)
      throws XPathMappingException, XPathExpressionException {
    return readLocalizedTemplateValue(template, variables).values().stream()
        .findFirst()
        .orElse(null);
  }

  public Map<Locale, String> readLocalizedTemplateValue(
      String template, List<XPathVariable> givenVariables)
      throws XPathMappingException, XPathExpressionException {
    Set<String> requiredVariables = getVariables(template);
    Map<String, XPathVariable> givenVariablesByName =
        givenVariables.stream().collect(Collectors.toMap(XPathVariable::name, v -> v));
    if (!givenVariablesByName.keySet().containsAll(requiredVariables)) {
      requiredVariables.removeAll(givenVariablesByName.keySet());
      throw new XPathMappingException(
          "Could not resolve template due to missing variables: "
              + String.join(", ", requiredVariables));
    }
    Map<String, Map<Locale, String>> resolvedVariables = new HashMap<>();
    for (String requiredVariable : requiredVariables) {
      XPathVariable var = givenVariablesByName.get(requiredVariable);
      resolvedVariables.put(var.name(), this.resolveLocalizedVariable(var.paths()));
    }
    return this.executeTemplate(template, resolvedVariables);
  }

  private Set<String> getVariables(String templateString) {
    Matcher matcher = VARIABLE_PATTERN.matcher(templateString);
    Set<String> variables = new HashSet<>();
    while (matcher.find()) {
      variables.add(matcher.group(1));
    }
    return variables;
  }

  private Map<Locale, String> executeTemplate(
      String templateString, Map<String, Map<Locale, String>> resolvedVariables)
      throws XPathExpressionException {
    Set<Locale> langs =
        resolvedVariables.values().stream()
            .map(Map::keySet) // Get set of languages for each resolved variable
            .flatMap(Collection::stream) // Flatten these sets into a single stream
            .collect(
                Collectors.toCollection(
                    LinkedHashSet::new)); // Store the stream in a set (thereby pruning duplicates)

    Map<Locale, String> out = new LinkedHashMap<>();
    // Resolve the <...> contexts
    for (Locale lang : langs) {
      String stringRepresentation = templateString;
      String context = extractContext(stringRepresentation);
      while (context != null) {
        stringRepresentation =
            stringRepresentation.replace(
                "<" + context + ">", resolveVariableContext(lang, context, resolvedVariables));
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
        stringRepresentation =
            stringRepresentation.replace(
                matcher.group(), resolvedVariables.get(varName).get(langToResolve));
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
      throw new XPathExpressionException(
          String.format(
              "Mismatched context delimiters, %s were unclosed at the end of parsing.", numOpen));
    } else {
      return null;
    }
  }

  private String resolveVariableContext(
      Locale language, String variableContext, Map<String, Map<Locale, String>> resolvedVariables) {
    Matcher varMatcher = VARIABLE_PATTERN.matcher(variableContext);
    varMatcher.find();
    String variableName = varMatcher.group(1);
    Map<Locale, String> resolvedValues = resolvedVariables.get(variableName);
    if (resolvedValues == null || resolvedValues.isEmpty()) {
      return "";
    } else if (resolvedValues.containsKey(language)) {
      return variableContext.replace(varMatcher.group(), resolvedValues.get(language));
    } else {
      return variableContext.replace(
          varMatcher.group(), resolvedValues.entrySet().iterator().next().getValue());
    }
  }

  private Map<Locale, String> resolveLocalizedVariable(String[] paths)
      throws XPathMappingException {
    return resolveLocalizedVariable(paths, false).entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, var -> var.getValue().get(0)));
  }

  private Map<Locale, List<String>> resolveLocalizedVariable(String[] paths, boolean multiValued)
      throws XPathMappingException {
    paths = prependWithRootPaths(paths);
    Map<Locale, LinkedHashSet<String>> result = new LinkedHashMap<>();

    LinkedHashSet<String> noLocaleResult = new LinkedHashSet<>();

    for (String path : paths) {
      List<Node> nodes;
      try {
        nodes = xpw.asListOfNodes(path);
      } catch (IllegalArgumentException e) {
        throw new XPathMappingException("Failed to resolve XPath: " + path, e);
      }
      for (Node node : nodes) {
        Locale locale = null;
        if (node.hasAttributes()) {
          Node langCode = node.getAttributes().getNamedItem("xml:lang");
          if (langCode != null) {
            locale = determineLocaleFromCode(langCode.getNodeValue());
          }
        }
        if (locale == null || locale.getLanguage().isEmpty()) {
          locale = EMPTY_LOCALE;
        }
        // For single valued: Only register value if we don't have one for the current locale
        String value = node.getTextContent().replace("<", "\\<").replace(">", "\\>");
        if (!multiValued) {
          if (!result.containsKey(locale)) {
            LinkedHashSet<String> valSet = new LinkedHashSet<>();
            valSet.add(value);
            result.put(locale, valSet);
          }
        } else {
          if (locale == EMPTY_LOCALE) {
            noLocaleResult.add(value);
          } else {
            LinkedHashSet<String> valuesForLocale = new LinkedHashSet<>();
            if (result.get(locale) != null) {
              valuesForLocale.addAll(result.get(locale));
            }
            valuesForLocale.add(value);
            result.put(locale, valuesForLocale);
          }
        }
      }
      // If we only want a single value and the first path yielded a value, no need to look at the
      // other paths
      if (!multiValued && !result.isEmpty()) {
        break;
      }
    }

    LinkedHashSet emptyLocaleSet = result.get(EMPTY_LOCALE);
    if (emptyLocaleSet != null) {
      emptyLocaleSet.addAll(noLocaleResult);
    } else {
      emptyLocaleSet = new LinkedHashSet(noLocaleResult);
    }

    if (!emptyLocaleSet.isEmpty()) {
      result.put(EMPTY_LOCALE, emptyLocaleSet);
    }

    return result.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList(e.getValue())));
  }

  private List<Element> resolveVariableAsElements(String[] paths) throws XPathMappingException {
    paths = prependWithRootPaths(paths);

    try {
      return Arrays.stream(paths)
          .flatMap(path -> xpw.asListOfNodes(path).stream())
          .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
          .map(Element.class::cast)
          .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
      throw new XPathMappingException("Failed to resolve XPath", e);
    }
  }

  private List<String> resolveVariablesAsStrings(String[] paths, boolean multiValued) {
    List<String> result = new LinkedList<>();
    paths = prependWithRootPaths(paths);

    for (String path : paths) {
      for (Object resolvedObject : xpw.asListOfObjects(path)) {
        if (resolvedObject instanceof String && !((String) resolvedObject).isEmpty()) {
          result.add((String) resolvedObject);
        }
        if (multiValued && resolvedObject instanceof DOMNodeList) {
          DOMNodeList nodeList = ((DOMNodeList) resolvedObject);
          for (int i = 0, l = nodeList.getLength(); i < l; i++) {
            String textContent = nodeList.item(i).getTextContent();
            if (textContent == null) {
              textContent = "";
            }
            textContent = textContent.trim();
            if (!result.contains(textContent)) {
              result.add(textContent);
            }
          }
        }
      }
    }

    return result;
  }

  protected static Locale determineLocaleFromCode(String localeCode) {
    if (localeCode == null) {
      return null;
    }

    Locale locale = Locale.forLanguageTag(localeCode);
    if (!locale.getLanguage().isEmpty()) {
      return locale;
    }

    // For cases, in which the language could not be determined
    // (e.g. for "und"), we have to re-build the locale manually
    String[] localeCodeParts = localeCode.split("-");
    if (localeCodeParts.length == 1) {
      // We only have a language, probably "und"
      return new Locale.Builder().setLanguage(localeCodeParts[0]).build();
    } else {
      // We have language and script
      return new Locale.Builder()
          .setLanguage(localeCodeParts[0])
          .setScript(localeCodeParts[1])
          .build();
    }
  }

  private String[] prependWithRootPaths(String[] paths) {
    if (rootPaths == null || rootPaths.isEmpty()) {
      return paths;
    }

    return Arrays.stream(paths)
        .flatMap(e -> rootPaths.stream().map(r -> r + e))
        .toArray(String[]::new);
  }
}
