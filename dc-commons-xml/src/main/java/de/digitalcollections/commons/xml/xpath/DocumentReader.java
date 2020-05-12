package de.digitalcollections.commons.xml.xpath;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Helper class to read simple or templated values from an XML document. */
class DocumentReader {

  private final List<String> rootPaths;
  private final XPathWrapper xpw;
  private final VariableResolver variableResolver;
  private final TemplateHandler templateHandler;

  public DocumentReader(Document doc, List<String> rootPaths, String namespace) {
    this.rootPaths = rootPaths;
    this.xpw = new XPathWrapper(doc);
    if (namespace != null) {
      xpw.setDefaultNamespace(namespace);
    }

    variableResolver = new VariableResolver(rootPaths, xpw);

    templateHandler = new TemplateHandler();
  }

  public Document getDocument() {
    return xpw.getDocument();
  }

  /**
   * Retrieve a single String value for a list of expressions, where the first match is used.
   *
   * @param expressions list of XPath expressions
   * @return one single string for the first matching XPath expression or <code>null</code>, if no
   *     match was possible.
   * @throws XPathMappingException in case of a resolving error
   */
  public String readValue(List<String> expressions) throws XPathMappingException {
    return variableResolver
        .resolveVariable(
            null,
            expressions,
            keynode -> null,
            valuenode -> valuenode.getTextContent().replace("<", "\\<").replace(">", "\\>"))
        .stream()
        .map(p -> p.getRight())
        .findFirst()
        .orElse(null);
  }

  /**
   * Retrieve a List of all matching values for the provided list of XPath expressions
   *
   * @param expressions list of XPath expressions
   * @return list of Strings for all matching XPathExpressions
   * @throws XPathMappingException in case of a resolving error
   */
  public List<String> readValues(List<String> expressions) throws XPathMappingException {
    return variableResolver
        .resolveVariable(
            null,
            expressions,
            keynode -> null,
            valuenodes -> valuenodes.getTextContent().replace("<", "\\<").replace(">", "\\>"))
        .stream()
        .map(pair -> pair.getRight())
        .collect(Collectors.toList());
  }

  /**
   * Retrieve a LinkedHashMap of all matching values for the provided list of XPath expressions
   * together with their keys, calculated from its relative XPath expression
   *
   * @param expressions list of XPath expressions
   * @param keyPath XPath expression, relative to the node, where the values are calculated from
   * @return LinkedHashMap of key/value pairs
   * @throws XPathMappingException in case of a resolving error
   */
  public Map<String, String> readValueMap(List<String> expressions, String keyPath)
      throws XPathMappingException {

    return variableResolver
        .resolveVariable(
            keyPath,
            expressions,
            keynode -> keynode.getTextContent(),
            valuenodes -> valuenodes.getTextContent().replace("<", "\\<").replace(">", "\\>"))
        .stream()
        .collect(
            Collectors.toMap(
                p -> p.getLeft(), p -> p.getRight().trim(), (e1, e2) -> e1, LinkedHashMap::new));
  }

  /**
   * Retrieve a LinkedHashMap of all matching DOM Elements for the provided list of XPath
   * expressions together with their keys, calculated from its relative XPath expression
   *
   * @param expressions list of XPath expressions
   * @param keyPath XPath expression, relative to the DOM element
   * @return LinkedHashMap of key/value pairs
   * @throws XPathMappingException in case of a resolving error
   */
  public Map<String, Element> readElementValueMap(List<String> expressions, String keyPath)
      throws XPathMappingException {
    return variableResolver
        .resolveVariable(
            keyPath,
            expressions,
            keynode -> keynode.getTextContent(),
            valuenodes -> (Element) valuenodes)
        .stream()
        .collect(
            Collectors.toMap(
                p -> p.getLeft(), p -> p.getRight(), (e1, e2) -> e1, LinkedHashMap::new));
  }

  /**
   * Retrieve a LinkedHashMap of all matching values (multivalued) for the provided list of XPath
   * expressions together with their keys, calculated from its relative XPath expression
   *
   * @param expressions list of XPath expressions
   * @param keyPath XPath expression, relative to the node, where the values are calculated from
   * @return LinkedHashMap of key/value pairs
   * @throws XPathMappingException in case of a resolving error
   */
  public Map<String, List<String>> readMultiValueMap(List<String> expressions, String keyPath)
      throws XPathMappingException {
    return variableResolver
        .resolveVariable(
            keyPath,
            expressions,
            keynode -> keynode.getTextContent(),
            valuenode -> variableResolver.extractStringListFromNode(valuenode))
        .stream()
        .collect(
            Collectors.toMap(
                p -> p.getLeft(),
                p -> p.getRight(),
                // We have to join the values for the same key
                (e1, e2) -> Stream.concat(e1.stream(), e2.stream()).collect(Collectors.toList()),
                LinkedHashMap::new));
  }

  /**
   * Retrieve a Map of all matching values for the provided list of XPath expressions together with
   * their locale (taken from the <code>xml:id</code> attribute, calculated from its relative XPath
   * expression
   *
   * @param expressions list of XPath expressions
   * @return LinkedHashMap of key/value pairs
   * @throws XPathMappingException in case of a resolving error
   */
  public Map<Locale, String> readLocalizedValue(List<String> expressions)
      throws XPathMappingException {
    return resolveVariable(expressions.toArray(new String[] {}));
  }

  /**
   * Retrieve a Map of all matching values (multivalued) for the provided list of XPath expressions
   * together with their locale (taken from the <code>xml:id</code> attribute, calculated from its
   * relative XPath expression
   *
   * @param expressions list of XPath expressions
   * @return LinkedHashMap of key/value pairs
   * @throws XPathMappingException in case of a resolving error
   */
  public Map<Locale, List<String>> readLocalizedValues(List<String> expressions)
      throws XPathMappingException {
    return resolveVariable(expressions.toArray(new String[] {}), true);
  }

  /**
   * Return a list of DOM elements for the provided list of XPath expressions
   *
   * @param expressions list of XPath expressions
   * @return List of resolved DOM elements
   * @throws XPathMappingException in case of a resolving error
   */
  public List<Element> readElementList(List<String> expressions) throws XPathMappingException {
    return resolveVariableAsElements(expressions.toArray(new String[] {}));
  }

  /**
   * Fill a template with the first matching XPath variable
   *
   * @param template the template string
   * @param variables a List of {@link XPathVariable} definitions
   * @return filled template. Can be null, if no match was found.
   * @throws XPathMappingException in case of a resolving error
   * @throws XPathExpressionException if an invalid XPath expression was provided
   */
  public String readTemplateValue(String template, List<XPathVariable> variables)
      throws XPathMappingException, XPathExpressionException {
    return readLocalizedTemplateValue(template, variables).values().stream()
        .findFirst()
        .orElse(null);
  }

  /**
   * Fill a localized map of templates with the first matching XPath variable each.
   *
   * @param template the template string
   * @param givenVariables a List of {@link XPathVariable} definitions
   * @return Map with Locale as key and filled template as value. Can be null, if no match was
   *     found.
   * @throws XPathMappingException in case of a resolving error
   * @throws XPathExpressionException if an invalid XPath expression was provided
   */
  public Map<Locale, String> readLocalizedTemplateValue(
      String template, List<XPathVariable> givenVariables)
      throws XPathMappingException, XPathExpressionException {
    Set<String> requiredVariables = templateHandler.getVariables(template);
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
      resolvedVariables.put(var.name(), this.resolveVariable(var.paths()));
    }
    return templateHandler.execute(template, resolvedVariables);
  }

  private Map<Locale, String> resolveVariable(String[] paths) throws XPathMappingException {
    return resolveVariable(paths, false).entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, var -> var.getValue().get(0)));
  }

  private Map<Locale, List<String>> resolveVariable(String[] paths, boolean multiValued)
      throws XPathMappingException {

    return variableResolver
        .resolveVariable(
            ".", // Since we extract the locale from the current node, the keyPath is "."
            List.of(paths),
            node -> variableResolver.extractLocaleFromNode(node),
            valuenode -> variableResolver.extractStringListFromNode(valuenode))
        .stream()
        .collect(
            Collectors.toMap(
                p -> p.getLeft(),
                p -> p.getRight(),
                // We have to join the values for the same key
                (e1, e2) -> Stream.concat(e1.stream(), e2.stream()).collect(Collectors.toList()),
                LinkedHashMap::new));
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

  private String[] prependWithRootPaths(String[] paths) {
    if (rootPaths == null || rootPaths.isEmpty()) {
      return paths;
    }

    return Arrays.stream(paths)
        .flatMap(e -> rootPaths.stream().map(r -> r + e))
        .toArray(String[]::new);
  }
}
