package de.digitalcollections.commons.xml.xpath;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Node;

/**
 * This class extracts key/value tuples out of an XML document by their XPaths.
 *
 * <p>The <code>paths</code> are always absolute (and prepended with the root paths, if they were
 * provided in the constructor), the paths for the keys must be relative, to establish the
 * connection to the resolved items.
 */
public class VariableResolver {

  private final List<String> rootPaths;
  private final XPathWrapper xpw;

  public VariableResolver(List<String> rootPaths, XPathWrapper xpw) {
    this.rootPaths = rootPaths;
    this.xpw = xpw;
  }

  /**
   * A generic method to resolve a given list of paths and to extract their values. For referencing,
   * for each value a <code>Pair</code> is returned with a key, calculated as described below, and
   * the value.
   *
   * <p>If the keyPath is set (to an XPath expression relative to the current path), the key value
   * is calculated by the <code>keyTransformer</code>, which gets the key node as input.
   *
   * <p>If the keyPath is <code>null</code>, the <code>keyTransfomer</code> must also return a key
   * (e.g. randomized), but gets <code>null</code> as input.
   *
   * @param keyPath The relative path, of whose node the key is calculated from. If the key is of no
   *     use, the path can be <code>null</code>, but then the <code>keyTransformer</code> function
   *     must return a random value for a <code>null</code> input.
   * @param paths The XPath expressions, where the values are retrieved from. If one or <code>
   *     rootPaths</code> are set in the constructor of this class, they are prepended (and
   *     multiplied, if more than one) to the XPath expressions.
   * @param keyTransformer a function to transform a key node into a key of the expected type
   * @param valueTransformer a function to transform a value node into a value of the expected type
   * @param <K> the type of the key
   * @param <V> the type of the value
   * @return a list of key/value pairs
   * @throws XPathMappingException when the value node could not be resolved.
   */
  public <K, V> List<Pair<K, V>> resolveVariable(
      String keyPath,
      List<String> paths,
      Function<Node, K> keyTransformer,
      Function<Node, V> valueTransformer)
      throws XPathMappingException {

    List<Pair<K, V>> resolvedVariables = new ArrayList<>();

    paths = prependWithRootPaths(paths);

    for (String path : paths) {
      List<Node> nodes;
      try {
        nodes = xpw.asListOfNodes(path);
      } catch (IllegalArgumentException e) {
        throw new XPathMappingException("Failed to resolve XPath: " + path, e);
      }

      for (Node node : nodes) {
        K key;
        if (keyPath == null) {
          // The keyTransformer alone must return the key.
          key = keyTransformer.apply(null);
        } else {
          Node keyNode = xpw.asNode(node, keyPath);
          key = keyTransformer.apply(keyNode);
        }

        V value = valueTransformer.apply(node);

        resolvedVariables.add(Pair.of(key, value));
      }
    }

    return resolvedVariables;
  }

  private List<String> prependWithRootPaths(List<String> paths) {
    if (rootPaths == null || rootPaths.isEmpty()) {
      return paths;
    }

    return paths.stream()
        .flatMap(e -> rootPaths.stream().map(r -> r + e))
        .collect(Collectors.toList());
  }

  /**
   * Extract the payload string(s) from a node by traversing down the node to all its childs in the
   * order, given by the DOM.
   *
   * @param node the start node
   * @return A linked list (ordered by the order of the children in the DOM) with all text node
   *     contents in the subtree
   */
  public List<String> extractStringListFromNode(Node node) {
    List<String> ret = new LinkedList<>();
    if (node.getNodeType() == Node.TEXT_NODE) {
      ret.add(node.getTextContent());
      return ret;
    }

    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
      ret.addAll(extractStringListFromNode(node.getChildNodes().item(i)));
    }
    return ret;
  }

  /**
   * Extract the locale information from a node, based on the value of the <code>xml:lang</code>
   * attribute.
   *
   * @param node the noode
   * @return the Locale or null
   */
  public Locale extractLocaleFromNode(Node node) {
    Node langCode = node.getAttributes().getNamedItem("xml:lang");
    if (langCode != null) {
      return determineLocaleFromCode(langCode.getNodeValue());
    }
    return null;
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
}
