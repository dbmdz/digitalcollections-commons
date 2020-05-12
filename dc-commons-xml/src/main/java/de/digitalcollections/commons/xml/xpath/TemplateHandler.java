package de.digitalcollections.commons.xml.xpath;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.xpath.XPathExpressionException;

/**
 * The purpose of this class is to handle templates, mainly to fill the templated with priovided
 * values.
 */
public class TemplateHandler {

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_-]+?)}");

  /**
   * Split a templateString into its embedded variables. Each variable must match the RegEx <code>
   * {([a-zA-Z0-9_-]+?)}</code>
   *
   * @param templateString
   * @return Set of extracted variables of the templateString
   */
  public Set<String> getVariables(String templateString) {
    Matcher matcher = VARIABLE_PATTERN.matcher(templateString);
    Set<String> variables = new HashSet<>();
    while (matcher.find()) {
      variables.add(matcher.group(1));
    }
    return variables;
  }

  /**
   * Fills the variables of a templateString with the provided resolved variables.
   *
   * @param templateString The template string
   * @param resolvedVariables A Map of resolved variables with the variable names as keys and the
   *     values of a <code>Map&lt;Locale, String></code>.
   * @return A localised value map.
   * @throws XPathExpressionException
   */
  public Map<Locale, String> execute(
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
}
