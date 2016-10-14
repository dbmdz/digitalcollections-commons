package org.mdz.dzp.commons.xml.xpath;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;


public class XPathMapper implements InvocationHandler {
  private static final Pattern variablePattern = Pattern.compile("\\{([a-zA-Z_-]+?)\\}");


  private final XPathWrapper xpw;
  private Document doc;

  @SuppressWarnings("unchecked")
  public static <T> T makeProxy(Document doc, Class<? extends T> iface, Class<?>... otherIfaces) {
    Class<?>[] allInterfaces = Stream
        .concat(Stream.of(iface), Stream.of(otherIfaces))
        .distinct()
        .toArray(Class<?>[]::new);
    return (T) Proxy.newProxyInstance(
        iface.getClassLoader(),
        allInterfaces,
        new XPathMapper(doc));
  }

  private XPathMapper(Document doc) {
    this.doc = doc;
    this.xpw = new XPathWrapper(doc);
  }

  public List<String> getVariables(String templateString) {
    Matcher matcher = variablePattern.matcher(templateString);
    List<String> variables = new ArrayList<>();
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

    // List of variable names from the template string
    List<String> variables = getVariables(binding.valueTemplate());

    // Sanity checks
    if (binding.multiValued()) {
      if (!method.getReturnType().isAssignableFrom(Collection.class)) {
        throw new XPathMappingException(
            "Method return type must be a Collection type if multiValued=true.");
      }
      if (variables.size() > 1) {
        throw new XPathMappingException(
            "There cannot be more than one variable in the valueTemplate if multiValued=true");
      }
    } else {
      if (!method.getReturnType().isAssignableFrom(String.class)) {
        throw new XPathMappingException("Return type must be String if multiValued=false;");
      }
    }

    // Set default namespace, if applicable
    if (!binding.defaultNamespace().isEmpty()) {
      xpw.setDefaultNamespace(binding.defaultNamespace());
    }

    // Resolve variables
    Map<String, List<String>> resolvedVariables = new HashMap<>();
    for (String variableName : variables) {
      XPathVariable var = Arrays.stream(binding.variables())
          .filter(v -> v.name().equals(variableName))
          .findFirst()
          .orElseThrow(() -> new XPathMappingException(String.format("Could not resolve variable `%s`", variableName)));
      resolvedVariables.put(variableName, this.resolveVariable(var));
    }

    return this.executeTemplate(binding.valueTemplate(), resolvedVariables);
  }

  private String executeTemplate(String templateString, Map<String, List<String>> resolvedVariables) throws XPathExpressionException {
    // Resolve the <...> contexts
    String context = extractContext(templateString);
    while (context != null) {
      templateString = templateString.replace(
          "<" + context + ">",
          resolveVariableContext(context, resolvedVariables));
      context = extractContext(templateString);
    }

    // Now we just need to resolve top-level variables
    Matcher matcher = variablePattern.matcher(templateString);
    while (matcher.find()) {
      String varName = matcher.group(1);
      templateString = templateString.replace(matcher.group(), resolvedVariables.get(varName).get(0));
      matcher = variablePattern.matcher(templateString);
    }
    return templateString;
  }

  private String extractContext(String template) throws XPathExpressionException {
    StringBuilder ctx = new StringBuilder();
    boolean isEscaped = false;
    boolean wasOpened = false;
    int numOpen = 0;
    for (char c : template.toCharArray()) {
      if (c == '\\') {
        isEscaped = true;
        ctx.append(c);
      } else if (c == '<') {
        if (isEscaped || numOpen > 0) {
          ctx.append(c);
        }
        if (!isEscaped) {
          numOpen++;
          if (!wasOpened) {
            wasOpened = true;
          }
        }
      } else if (c == '>') {
        if (isEscaped || numOpen > 1) {
          ctx.append(c);
        }
        if (!isEscaped) {
          numOpen--;
          if (numOpen == 0) {
            return ctx.toString();
          }
        }
      } else if (wasOpened) {
        ctx.append(c);
      }
    }
    if (wasOpened) {
      throw new XPathExpressionException(String.format(
          "Mismatched context delimiters, % were unclosed at the end of parsing.", numOpen));
    } else {
      return null;
    }
  }

  private String resolveVariableContext(String variableContext, Map<String, List<String>> resolvedVariables) {
    Matcher varMatcher = variablePattern.matcher(variableContext);
    varMatcher.find();
    String variableName = varMatcher.group(1);
    List<String> resolvedValues = resolvedVariables.get(variableName);
    if (resolvedValues == null || resolvedValues.isEmpty()) {
      return "";
    } else {
      return variableContext.replace(varMatcher.group(), resolvedValues.get(0));
    }
  }

  private List<String> resolveVariable(XPathVariable var) throws XPathExpressionException {
    List<String> result = null;
    for (String path : var.paths()) {
      result = xpw.asListOfStrings(path);
      if (result != null && !result.isEmpty()) {
        break;
      }
    }
    return result;
  }
}
