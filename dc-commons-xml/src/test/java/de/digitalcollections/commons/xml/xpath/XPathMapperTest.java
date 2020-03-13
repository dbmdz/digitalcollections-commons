package de.digitalcollections.commons.xml.xpath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.w3c.dom.Document;

@DisplayName("The XPath Mapper")
public class XPathMapperTest {

  private TestMapper mapper;
  private XPathRootMapper xPathRootMapper;
  private OuterMapper hierarchicalMapper;
  private BrokenOuterMapper brokenHierarchicalMapper;
  private BrokenNamespacedOuterMapper brokenNamespacedOuterMapper;

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  private interface TestMapper {

    String BIBLSTRUCT_PATH = "/tei:TEI/tei:teiHeader/tei:fileDesc/tei:sourceDesc/tei:listBibl/tei:biblStruct";

    @XPathBinding(
            valueTemplate = "{author}",
            variables = {
              @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
            }
    )
    Map<Locale, String> getAuthor() throws XPathMappingException;

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
            @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author[@type=\"ChuckNorris\"]/tei:persName/tei:name"})
        }
    )
    String getNoAuthor() throws XPathMappingException;

    @XPathBinding(
            valueTemplate = "{title}<: {subtitle}>< [. {partNumber}<, {partTitle}>]>",
            variables = {
              @XPathVariable(name = "title", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"main\"]"}),
              @XPathVariable(name = "subtitle", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"sub\"]"}),
              @XPathVariable(name = "partNumber", paths = {BIBLSTRUCT_PATH + "/tei:series/tei:biblScope[@ana=\"#norm\"]"}),
              @XPathVariable(name = "partTitle", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"part\"]"})
            }
    )
    String getTitle() throws XPathMappingException;

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"alt\" and @subtype=\"main\"]")
    Map<Locale,List<String>> getMultlangAlternativeTitles() throws XPathMappingException;

    @XPathBinding(valueTemplate = "{broken}", variables = {})
    String broken() throws XPathMappingException;

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
    Map<Locale, String> getAuthorFromExpression() throws XPathMappingException;

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
            @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
        },
        value = BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"
    )
    Map<Locale, String> getAuthorFromExpressionAndVariables() throws XPathMappingException;

    @XPathBinding()
    String getNothing() throws XPathMappingException;

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:pubPlace")
    List<String> getPlaces() throws XPathMappingException;

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:pubPlace[1]")
    String getFirstPlace() throws XPathMappingException;

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:noPlace[1]")
    String getNoPlace() throws XPathMappingException;

    @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id[1]")
    Integer wrongReturnTypeSinglevalued() throws XPathMappingException;

    @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id")
    List<Integer> wrongReturnTypeMultivalued() throws XPathMappingException;

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
    Map<Locale, Integer> wrongReturnTypeMultiLanguage() throws XPathMappingException;

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
    String wrongReturnTypeExplicitMultiLanguage() throws XPathMappingException;

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
            @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
        }
    )
    Map<Locale, List<String>> wrongReturnTypeTemplatedMultiLanguage() throws XPathMappingException;
  }

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0",
      value = { "/tei:TEI/tei:teiHeader/tei:fileDesc/tei:sourceDesc/tei:listBibl/tei:biblStruct" }
  )
  private interface XPathRootMapper {

    @XPathBinding("/tei:monogr/tei:author/tei:persName/tei:name")
    String getAuthor() throws XPathMappingException;

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
            @XPathVariable(name = "author", paths = {"/tei:monogr/tei:author/tei:persName/tei:name"})
        }
    )
    Map<Locale, String> getAuthors() throws XPathMappingException;
  }

  @XPathRoot("/outer")
  private interface OuterMapper {
      @XPathRoot("/inner")
      InnerMapper getInnerMapper();
  }

  @XPathRoot("/ignored")
  private interface InnerMapper {
      @XPathBinding("/author")
      String getAuthor();
  }

  private interface BrokenOuterMapper {
    @XPathRoot("/inner")
    BrokenInnerMapper getInnerMapper() throws XPathMappingException;
  }

  private interface BrokenInnerMapper {
    String someMethod() throws XPathMappingException;
  }

  @XPathRoot(
      value = "/outer",
      defaultNamespace = "http://www.tei-c.org/ns/1.0")
  private interface BrokenNamespacedOuterMapper {
    @XPathRoot(
        value = "/inner",
        defaultNamespace = "foo"
    )
    BrokenNamespacedInnerMapper getInnerMapper() throws XPathMappingException;;
  }

  private interface BrokenNamespacedInnerMapper {
    @XPathBinding("/author")
    String getAuthor() throws XPathMappingException;;
  }

  @BeforeEach
  public void setUp() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bsbstruc.xml");
    Document doc = db.parse(is);
    this.mapper = XPathMapper.makeProxy(doc, TestMapper.class);
    this.xPathRootMapper = XPathMapper.makeProxy(doc, XPathRootMapper.class);

    is = Thread.currentThread().getContextClassLoader().getResourceAsStream("simple.xml");
    Document simpleDoc = db.parse(is);
    this.hierarchicalMapper = XPathMapper.makeProxy(simpleDoc, OuterMapper.class);

    this.brokenHierarchicalMapper = XPathMapper.makeProxy(simpleDoc, BrokenOuterMapper.class);

    this.brokenNamespacedOuterMapper = XPathMapper.makeProxy(simpleDoc,
        BrokenNamespacedOuterMapper.class);
  }

  @DisplayName("shall evaluate a template with a single variable")
  @Test
  public void testTemplateWithSingleVariable() throws Exception {
    assertThat(mapper.getAuthor().get(Locale.GERMAN)).isEqualTo("Kugelmann, Hans");
    assertThat(mapper.getAuthor().get(Locale.ENGLISH)).isEqualTo("Name, English");
  }

  @DisplayName("shall return null for a template with a single variable and non matching xpath")
  @Test
  public void testTemplateWithNonmatchingXPath() throws Exception {
    assertThat(mapper.getNoAuthor()).isNull();
  }

  @DisplayName("shall evaluate a template with context")
  @Test
  public void testTemplateWithContext() throws Exception {
    assertThat(mapper.getTitle()).isEqualTo("Ein Titel: Ein Untertitel");
  }

  @DisplayName("shall evaluate an expression without template")
  @Test
  public void testExpression() throws Exception {
    assertThat(mapper.getAuthorFromExpression().get(Locale.GERMAN)).isEqualTo("Kugelmann, Hans");
    assertThat(mapper.getAuthorFromExpression().get(Locale.ENGLISH)).isEqualTo("Name, English");
  }

  @DisplayName("shall evaluate a single value expression without template")
  @Test
  public void testSingleValueExpression() throws Exception {
    assertThat(mapper.getFirstPlace()).isEqualTo("Augsburg");
  }

  @DisplayName("shall return multivalued contents in the same order as in the bind")
  @Test
  public void testMultivaluedFieldsAndTheirOrder() throws Exception {
    assertThat(mapper.getPlaces()).containsExactly("Augsburg","München","Aachen");
  }

  @DisplayName("shall return multivalued, multilanguage contents in correct order")
  @Test
  public void testMultivaluedMultilanguageFieldsAndTheirOrder() throws Exception {
    assertThat(mapper.getMultlangAlternativeTitles().get(Locale.GERMAN)).containsExactly("Chuck Norris Biographie","Fakten");
    assertThat(mapper.getMultlangAlternativeTitles().get(Locale.ENGLISH)).containsExactly("Chuck Norris Biography","Facts");
  }

  @DisplayName("shall return null for a non matching single value expression")
  @Test
  public void testNonmatchingExpressionReturnsNull() throws Exception {
    assertThat(mapper.getNoPlace()).isNull();
  }

  @DisplayName("shall throw an exception, when a template variable is missing")
  @Test
  public void testMissingVariableThrows() throws Exception {
    assertThatThrownBy(mapper::broken).isInstanceOf(XPathMappingException.class).hasMessageContaining("Could not resolve variable");
  }

  @DisplayName("shall throw an exception, when templates and expressions are used at the same time")
  @Test
  public void testTemplatesAndExpressionsThrowException() {
    assertThatThrownBy(mapper::getAuthorFromExpressionAndVariables).isInstanceOf(XPathMappingException.class).hasMessageContaining("Only one XPath evaluation type");
  }

  @DisplayName("shall throw an exception, when neither templares nor expressions are defined")
  @Test
  public void testNoTemplatesAndExpressionsThrowException() {
    assertThatThrownBy(mapper::getNothing).isInstanceOf(XPathMappingException.class).hasMessageContaining("Either variables or expressions must be used");
  }

  @DisplayName("shall throw an exception, when the return type of a single valued field is wrong")
  @Test
  public void testWrongReturnTypeOfSingleValuedFieldsThrowsException() {
    assertThatThrownBy(mapper::wrongReturnTypeSinglevalued).isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal return type");
  }

  @DisplayName("shall throw an exception, when the return type of a multivalued field is wrong")
  @Test
  public void testWrongReturnTypeOfMultiValuedFieldsThrowsException() {
    assertThatThrownBy(mapper::wrongReturnTypeMultivalued).isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal return type");
  }

  @DisplayName("shall throw an exception, when the return type of a multilanguage field is wrong")
  @Test
  public void testWrongReturnTypeOfMultilanguageFieldsThrowsException() {
    assertThatThrownBy(mapper::wrongReturnTypeMultiLanguage).isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal return type");
  }

  @DisplayName("shall throw an exception, when the return type of a multilanguage field is wrong in templated context")
  @Test
  public void testWrongReturnTypeOfMultilanguageFieldsInTemplatesThrowsException() {
    assertThatThrownBy(mapper::wrongReturnTypeTemplatedMultiLanguage).isInstanceOf(XPathMappingException.class).hasMessageContaining("Templated binding methods must have a java.lang.String");
  }

  @DisplayName("shall throw an exception, when embedded mappers lack @XPathBinding annotations")
  @Test
  public void testInvalidHierarchy() {
    assertThatThrownBy(brokenHierarchicalMapper::getInnerMapper)
        .isInstanceOf(XPathMappingException.class).hasMessageContaining(
        "Childs must contain at least one method with @XPathBinding annotation");
  }

  @DisplayName("shall pass down root path definitions on hierarchies")
  @Test
  public void shallPassDownRootPathsOnHierarchies() {
    assertThat(hierarchicalMapper.getInnerMapper().getAuthor()).isEqualTo("Chuck Norris");
  }

  @DisplayName("shall throw an exception, when embedding and embedded mappers both set a default "
      + "namespace")
  @Test
  public void testHierarchyWithConflicingDefaultNamespaces() {
    assertThatThrownBy(brokenNamespacedOuterMapper::getInnerMapper)
        .isInstanceOf(XPathMappingException.class).hasMessageContaining(
        "Default namespace can only be set on type level @XPathRoot annotation, not on method level.");
  }
}
