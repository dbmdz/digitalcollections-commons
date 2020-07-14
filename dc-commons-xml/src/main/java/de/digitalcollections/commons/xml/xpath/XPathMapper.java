package de.digitalcollections.commons.xml.xpath;

import com.google.common.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Maps XML documents to Java POJOs.
 *
 * <p>An XPathMapper is usually constructed once for every mapped type, avoid constructing it in a
 * loop, since there's a lot of expensive reflection going on during construction.
 *
 * @param <T> the target type to map XML documents to
 */
@SuppressWarnings("UnstableApiUsage")
public class XPathMapper<T> {
  private final Class<T> targetType;
  private final List<String> rootPaths;
  private final String defaultRootNamespace;
  private final List<MappedField> fields;

  private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

  static {
    dbf.setNamespaceAware(true);
  }

  // Various Guava type tokens to help with reflection
  private static final TypeToken<String> SINGLEVALUED_TYPE = new TypeToken<>() {};
  private static final TypeToken<Boolean> FLAG_TYPE = new TypeToken<>() {};
  private static final TypeToken<List<?>> MULTIVALUED_TYPE = new TypeToken<>() {};
  private static final TypeToken<List<String>> MULTIVALUED_STRING_TYPE = new TypeToken<>() {};
  private static final TypeToken<List<Element>> MULTIVALUED_ELEMENT_TYPE = new TypeToken<>() {};
  private static final TypeToken<Map<Locale, String>> LOCALIZED_SINGLEVALUED_TYPE =
      new TypeToken<>() {};
  private static final TypeToken<Map<Locale, List<String>>> LOCALIZED_MULTIVALUED_TYPE =
      new TypeToken<>() {};

  /**
   * Convenience method to construct a temporary mapper and read a single document with it.
   *
   * @deprecated Construct a {@link XPathMapper} that you can reuse and call {@link
   *     XPathMapper#readDocument(Document)}!
   * @param doc source XML document to retrieve mapping values from
   * @param targetType the target type, e.g. a class with fields/setters annotated with
   *     {@literal @}{@link XPathBinding} and/or {@literal @}{@link XPathRoot}
   * @param rootPaths optional array of root paths, to override the root paths parsed from the
   *     target type.
   * @return an instance of the target type, with annotated fields filled from the XML document.
   */
  @Deprecated
  public static <T> T readDocument(Document doc, Class<T> targetType, String... rootPaths)
      throws XPathMappingException {
    return new XPathMapper<>(targetType, rootPaths, null).readDocument(doc);
  }

  /** Create a new mapper for a given type. */
  public XPathMapper(Class<T> targetType) throws XPathMappingException {
    this(targetType, new String[] {}, null);
  }

  /**
   * Create a new mapper for a given type, with custom root paths and a default namespace.
   *
   * <p>Use this constructor if you want to override the root paths and/or the default namespace
   * read from the {@link XPathRoot} annotation on the target type.
   */
  public XPathMapper(Class<T> targetType, String[] rootPaths, String defaultRootNamespace)
      throws XPathMappingException {
    this.targetType = targetType;

    XPathRoot rootAnnotation = targetType.getAnnotation(XPathRoot.class);
    // If there's no user-supplied default namespace, we use the one annotated on the type itself
    this.defaultRootNamespace = determineNamespace(defaultRootNamespace, rootAnnotation);
    // If there are no user-suppplied root paths, we use those annotated on the type itself
    this.rootPaths = Arrays.asList(determineRootPaths(rootPaths, rootAnnotation));

    this.fields = new ArrayList<>();
    // Determine setters and fields annotated with @XPathBinding
    for (Method m : getSettersAnnotatedWith(targetType, XPathBinding.class)) {
      XPathBinding binding = m.getDeclaredAnnotation(XPathBinding.class);
      validateBinding(binding);
      if (!binding.valueTemplate().isEmpty()) {
        fields.add(
            new TemplateField(m, binding.valueTemplate(), Arrays.asList(binding.variables())));
      } else {
        fields.add(new SimpleField(m, binding.value()));
      }
    }
    for (Field fl : getFieldsAnnotatedWith(targetType, XPathBinding.class)) {
      XPathBinding binding = fl.getDeclaredAnnotation(XPathBinding.class);
      validateBinding(binding);
      if (!binding.valueTemplate().isEmpty()) {
        fields.add(
            new TemplateField(fl, binding.valueTemplate(), Arrays.asList(binding.variables())));
      } else {
        fields.add(new SimpleField(fl, binding.value()));
      }
    }

    // Determine all setters and fields that map nested types, i.e. that are annotated with
    // @XPathRoot
    for (Method m : getSettersAnnotatedWith(targetType, XPathRoot.class)) {
      XPathRoot nestedRoot = m.getDeclaredAnnotation(XPathRoot.class);
      fields.add(
          new NestedField(
              m,
              prependWithRootPaths(nestedRoot.value()),
              determineNamespace(this.defaultRootNamespace, nestedRoot)));
    }
    for (Field fl : getFieldsAnnotatedWith(targetType, XPathRoot.class)) {
      XPathRoot nestedRoot = fl.getDeclaredAnnotation(XPathRoot.class);
      fields.add(
          new NestedField(
              fl, nestedRoot.value(), determineNamespace(this.defaultRootNamespace, nestedRoot)));
    }
  }

  /**
   * Create a new instance of the target type from the given XML document located at pathToXmlFile.
   */
  public T readDocument(Path pathToXmlFile)
      throws XPathMappingException, IOException, ParserConfigurationException, SAXException {
    try (InputStream inputStream = Files.newInputStream(pathToXmlFile)) {
      return readDocument(inputStream);
    }
  }

  /** Create a new instance of the target type from the given XML document by its InputStream. */
  public T readDocument(InputStream inputStream)
      throws XPathMappingException, ParserConfigurationException, IOException, SAXException {
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(inputStream);
    return readDocument(document);
  }

  public T readDocument(DocumentReader docReader) throws XPathMappingException {
    // Instantiate an empty target object
    T val;
    try {
      val = targetType.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new XPathMappingException(
          String.format(
              "Cannot create an instance of %s, does the type have a public default constructor?",
              targetType.getName()),
          e);
    }

    for (MappedField field : fields) {
      try {
        field.setValue(docReader, val);
      } catch (InvocationTargetException | IllegalAccessException e) {
        throw new XPathMappingException("Mapping failed: " + e.getMessage(), e);
      } catch (XPathExpressionException e) {
        throw new XPathMappingException("Mapping failed due to illegal XPath expression", e);
      }
    }
    return val;
  }

  /** Create a new instance of the target type from the given XML document. */
  public T readDocument(Document doc) throws XPathMappingException {
    // Map the fields for the current document
    DocumentReader docReader = new DocumentReader(doc, rootPaths, defaultRootNamespace);
    return this.readDocument(docReader);
  }

  private void validateBinding(XPathBinding binding) throws XPathMappingException {
    if (!binding.valueTemplate().isEmpty() && !isEmptyOrHasOnlyBlanks(binding.value())) {
      throw new XPathMappingException(
          "An @XPathBinding must have one of `variables` or `expressions`, but both were set!");
    } else if (binding.valueTemplate().isEmpty() && isEmptyOrHasOnlyBlanks(binding.value())) {
      throw new XPathMappingException(
          "An @XPathBinding must have one of `variables` or `expressions`, but neither were set!");
    }
  }

  private String determineNamespace(String defaultRoot, XPathRoot userRoot) {
    if (defaultRoot != null) {
      return defaultRoot;
    } else if (userRoot != null) {
      return userRoot.defaultNamespace();
    } else {
      return "";
    }
  }

  private String[] determineRootPaths(String[] defaultRoots, XPathRoot root) {
    if (defaultRoots.length > 0) {
      return defaultRoots;
    } else if (root != null) {
      return root.value();
    } else {
      return new String[] {};
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

  private boolean isEmptyOrHasOnlyBlanks(String[] vals) {
    return Arrays.stream(vals).filter(Objects::nonNull).allMatch(String::isEmpty);
  }

  static Set<Method> getSettersAnnotatedWith(
      Class<?> type, final Class<? extends Annotation> annotation) {
    Set<Method> methods = new HashSet<>();
    // Iterate through hierarchy to get inherited methods, too
    while (type != Object.class) {
      Arrays.stream(type.getDeclaredMethods())
          .filter(m -> m.getName().startsWith("set"))
          .filter(m -> m.getParameterTypes().length == 1)
          .filter(m -> m.isAnnotationPresent(annotation))
          .forEach(methods::add);
      type = type.getSuperclass();
    }
    return methods;
  }

  static Set<Field> getFieldsAnnotatedWith(
      Class<?> type, final Class<? extends Annotation> annotation) {
    Set<Field> set = new HashSet<>();
    // Iterate through hierarchy to get inherited fields, too
    while (type != null) {
      Arrays.stream(type.getDeclaredFields())
          .filter(f -> f.isAnnotationPresent(annotation))
          .forEach(set::add);
      type = type.getSuperclass();
    }
    return set;
  }

  /** Base class for mapped fields, takes care of setting the value on the target object. */
  public abstract static class MappedField {
    private final boolean isField;
    private final AccessibleObject member;
    private final Type targetType;

    public MappedField(Method setter) {
      this.isField = false;
      this.member = setter;
      this.targetType = setter.getGenericParameterTypes()[0];
    }

    public MappedField(Field field) {
      this.isField = true;
      this.member = field;
      this.targetType = field.getGenericType();
    }

    /**
     * To be implemented by subclasses, determine the value for the field from the given document.
     */
    protected abstract Object determineValue(DocumentReader r)
        throws XPathMappingException, XPathExpressionException;

    protected Type getTargetType() {
      return targetType;
    }

    public void setValue(DocumentReader r, Object target)
        throws InvocationTargetException, IllegalAccessException, XPathMappingException,
            XPathExpressionException {
      Object val = determineValue(r);
      if (isField) {
        Field fl = (Field) member;
        fl.setAccessible(true);
        fl.set(target, val);
      } else {
        Method setter = (Method) member;
        setter.setAccessible(true);
        setter.invoke(target, val);
      }
    }
  }

  /** Simple, non-templated field binding, can be localized and/or multi-valued. */
  public static final class SimpleField extends MappedField {
    private boolean multiValued;
    private boolean multiLanguage;
    private boolean multiValuedElements;
    private boolean flag;
    private final List<String> paths;

    public SimpleField(Method m, String[] paths) throws XPathMappingException {
      super(m);
      this.paths = Arrays.asList(paths);
      this.analyzeTargetType();
    }

    public SimpleField(Field fl, String[] paths) throws XPathMappingException {
      super(fl);
      this.paths = Arrays.asList(paths);
      this.analyzeTargetType();
    }

    private void analyzeTargetType() throws XPathMappingException {
      Type targetType = this.getTargetType();
      boolean isMultiLocalized = LOCALIZED_MULTIVALUED_TYPE.isSubtypeOf(targetType);
      boolean isSingleLocalized = LOCALIZED_SINGLEVALUED_TYPE.isSubtypeOf(targetType);
      boolean isMultiValued = MULTIVALUED_STRING_TYPE.isSubtypeOf(targetType);
      boolean isSingleValued = SINGLEVALUED_TYPE.isSubtypeOf(targetType);
      this.flag = FLAG_TYPE.isSubtypeOf(targetType);
      this.multiLanguage = isMultiLocalized || isSingleLocalized;
      this.multiValued = isMultiValued || isMultiLocalized;
      this.multiValuedElements = MULTIVALUED_ELEMENT_TYPE.isSubtypeOf(targetType);
      if (!multiLanguage && !multiValued && !multiValuedElements && !isSingleValued && !flag) {
        throw new XPathMappingException(
            String.format(
                "Binding method has illegal target type %s, must be one of %s, %s, %s, %s or %s",
                targetType,
                SINGLEVALUED_TYPE,
                FLAG_TYPE,
                MULTIVALUED_STRING_TYPE,
                MULTIVALUED_ELEMENT_TYPE,
                LOCALIZED_SINGLEVALUED_TYPE,
                LOCALIZED_MULTIVALUED_TYPE));
      }
    }

    @Override
    protected Object determineValue(DocumentReader r) throws XPathMappingException {
      if (multiValuedElements) {
        return r.readElementList(paths);
      } else if (multiValued && multiLanguage) {
        return r.readLocalizedValues(paths);
      } else if (multiValued) {
        return r.readValues(paths);
      } else if (multiLanguage) {
        return r.readLocalizedValue(paths);
      } else if (flag) {
        return Boolean.valueOf(r.readValue(paths));
      } else {
        return r.readValue(paths);
      }
    }
  }

  /** Templated field, can be localized, but not multi-valued. */
  public static final class TemplateField extends MappedField {
    boolean multiLanguage;
    List<XPathVariable> variables;
    String template;

    public TemplateField(Method m, String template, List<XPathVariable> vars)
        throws XPathMappingException {
      super(m);
      this.variables = vars;
      this.template = template;
      this.analyzeTargetType();
    }

    public TemplateField(Field fl, String template, List<XPathVariable> vars)
        throws XPathMappingException {
      super(fl);
      this.variables = vars;
      this.template = template;
      this.analyzeTargetType();
    }

    private void analyzeTargetType() throws XPathMappingException {
      Type targetType = this.getTargetType();
      this.multiLanguage = LOCALIZED_SINGLEVALUED_TYPE.isSubtypeOf(targetType);
      boolean isSingle = SINGLEVALUED_TYPE.isSubtypeOf(targetType);
      if (!multiLanguage && !isSingle) {
        throw new XPathMappingException(
            String.format(
                "Templated binding fields must have a %s or %s type",
                SINGLEVALUED_TYPE, LOCALIZED_SINGLEVALUED_TYPE));
      }
    }

    @Override
    protected Object determineValue(DocumentReader r)
        throws XPathMappingException, XPathExpressionException {
      if (multiLanguage) {
        return r.readLocalizedTemplateValue(template, variables);
      } else {
        return r.readTemplateValue(template, variables);
      }
    }
  }

  /** Nested type. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static final class NestedField extends MappedField {
    private final XPathMapper mapper;
    private boolean isMultiValued;
    private List<String> rootPaths;

    public NestedField(Method setter, String[] paths, String rootNamespace)
        throws XPathMappingException {
      super(setter);
      this.analyzeTargetType();
      this.mapper = createMapper(paths, rootNamespace);
    }

    public NestedField(Field field, String[] paths, String rootNamespace)
        throws XPathMappingException {
      super(field);
      this.analyzeTargetType();
      this.mapper = createMapper(paths, rootNamespace);
    }

    private void analyzeTargetType() {
      this.isMultiValued = MULTIVALUED_TYPE.isSupertypeOf(getTargetType());
    }

    private XPathMapper createMapper(String[] paths, String rootNamespace)
        throws XPathMappingException {
      Type targetType = getTargetType();
      if (isMultiValued) {
        targetType = ((ParameterizedType) targetType).getActualTypeArguments()[0];
      }
      this.rootPaths = Arrays.asList(paths);
      return new XPathMapper((Class<?>) targetType, paths, rootNamespace);
    }

    @Override
    protected Object determineValue(DocumentReader r) throws XPathMappingException {
      // Map the fields for the current document
      List<Object> vals = new ArrayList<>();
      DocumentBuilder db;
      try {
        db = dbf.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        // Should not happen
        throw new RuntimeException(e);
      }

      // No nested root for our type, we can assume single-valued and a shared root with the parent,
      // i.e. we can
      // reuse the reader
      if (this.rootPaths.isEmpty()) {
        return mapper.readDocument(r);
      }
      for (Element elem : r.readElementList(rootPaths)) {
        Document subDoc = db.newDocument();
        subDoc.appendChild(subDoc.adoptNode(elem.cloneNode(true)));
        DocumentReader tempReader =
            new DocumentReader(
                subDoc, List.of("/" + elem.getTagName()), mapper.defaultRootNamespace);
        Object val = mapper.readDocument(tempReader);
        if (!isMultiValued && val != null) {
          return val;
        }
        vals.add(val);
      }

      if (isMultiValued) {
        return vals;
      } else {
        return null;
      }
    }
  }
}
