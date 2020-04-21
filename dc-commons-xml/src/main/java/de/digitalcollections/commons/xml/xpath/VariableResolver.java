package de.digitalcollections.commons.xml.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.units.qual.K;
import org.w3c.dom.Node;

public class VariableResolver {

  private final List<String> rootPaths;
  private final XPathWrapper xpw;

  public VariableResolver(List<String> rootPaths, XPathWrapper xpw) {
    this.rootPaths = rootPaths;
    this.xpw = xpw;
  }

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
        K key = null;
        if (keyPath != null) {
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
}
