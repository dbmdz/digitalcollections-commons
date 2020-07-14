package de.digitalcollections.commons.xml.xpath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

@DisplayName("The XPath Mapper")
public class XPathMapperTest {

  public static final String BIBLSTRUCT_PATH =
      "/TEI/teiHeader/fileDesc/sourceDesc/listBibl/biblStruct";

  XPathMapperFixture<TestMapper> testMapperFixture = new XPathMapperFixture<>(TestMapper.class);
  XPathMapperFixture<BrokenTestMapper> brokenTestMapperFixture =
      new XPathMapperFixture<>(BrokenTestMapper.class);
  XPathMapperFixture<BrokenStructuredTestMapper> brokenStructuredTestMapperFixture =
      new XPathMapperFixture<>(BrokenStructuredTestMapper.class);
  XPathMapperFixture<IncompleteTestMapper> incompleteTestMapperFixture =
      new XPathMapperFixture<>(IncompleteTestMapper.class);
  XPathMapperFixture<WrongFieldTestMapper> wrongFieldTestMapperFixture =
      new XPathMapperFixture<>(WrongFieldTestMapper.class);
  XPathMapperFixture<WrongArgumentTestMapper> wrongArgumentTestMapperFixture =
      new XPathMapperFixture<>(WrongArgumentTestMapper.class);
  XPathMapperFixture<WrongMultivalueFieldTestMapper> wrongMultivalueFieldTestMapperFixture =
      new XPathMapperFixture<>(WrongMultivalueFieldTestMapper.class);
  XPathMapperFixture<WrongMultivalueArgumentTestMapper> wrongMultivalueArgumentTestMapperFixture =
      new XPathMapperFixture<>(WrongMultivalueArgumentTestMapper.class);
  XPathMapperFixture<WrongMultilanguageFieldTestMapper> wrongMultilanguageFieldTestMapperFixture =
      new XPathMapperFixture<>(WrongMultilanguageFieldTestMapper.class);
  XPathMapperFixture<WrongMultilanguageArgumentTestMapper>
      wrongMultilanguageArgumentTestMapperFixture =
          new XPathMapperFixture<>(WrongMultilanguageArgumentTestMapper.class);
  XPathMapperFixture<HierarchicalMapper> hierarchicalMapperFixture =
      new XPathMapperFixture<>(HierarchicalMapper.class);
  XPathMapperFixture<BrokenHierarchicalMapper> brokenHierarchivalMapperFixture =
      new XPathMapperFixture<>(BrokenHierarchicalMapper.class);

  @DisplayName("shall evaluate a template with a single variable for a field")
  @Test
  public void testClassTemplateWithSingleVariableForField() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getAuthor().get(Locale.GERMAN)).isEqualTo("Kugelmann, Hans");
    assertThat(mapper.getAuthor().get(Locale.ENGLISH)).isEqualTo("Name, English");
  }

  @DisplayName("shall evaluate a template with a single variable in a setter method")
  @Test
  public void testClassTemplateWithSingleVariableInSetter() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getGermanAuthor()).isEqualTo("Kugelmann, Hans");
  }

  @DisplayName("shall return null for a template with a single variable and non matching xpath")
  @Test
  public void testTemplateWithNonmatchingXPath() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getNoAuthor()).isNull();
  }

  @DisplayName("shall evaluate a template with context")
  @Test
  public void testTemplateWithContext() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getTitle().values().toArray()[0]).isEqualTo("Ein Titel: Ein Untertitel");
  }

  @DisplayName("shall evaulate boolean expressions")
  @Test
  public void testBooleanExpressions() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.containsAuthors()).isTrue();

    TestMapper mapper2 = testMapperFixture.setUpMapperWithResource("simple.xml");
    assertThat(mapper2.containsAuthors()).isFalse();
  }

  @DisplayName("shall evaluate correct title mapping")
  @Test
  public void testPageXmlTitle() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsb10000001_page.xml");
    final Map<Locale, String> title = mapper.getTitle();
    assertThat(title.size()).isEqualTo(1);
    assertThat(title.values().toArray()[0])
        .isEqualTo(
            "Actorum Bohemicorum, ... Theil, Das ist: Warhaffte vnd eigentliche Beschreibung aller fürnembsten vnd denckwürdigsten Historien vnd Geschichten, Welche sich im Königreich Böheim vnd dessen incorporirten Ländern ... begeben vnd zugetragen haben: Auß allerhand glaubwürdigen Publicis scriptis in eine feine richtige Ordnung zusammen verfasset, jetzo mit fleiß ubersehen, gemehret vnd auffs newe zugerichtet [. 1]");
  }

  @DisplayName("shall evaluate an expression in a setter without template")
  @Test
  public void testExpressionInSetter() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getAuthorFromExpression().get(Locale.GERMAN)).isEqualTo("Kugelmann, Hans");
    assertThat(mapper.getAuthorFromExpression().get(Locale.ENGLISH)).isEqualTo("Name, English");
  }

  @DisplayName("shall evaluate setter with an argument list of elements")
  @Test
  public void testElementArgumentList() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getAuthorElements()).hasSize(2);
  }

  @DisplayName("shall evaluate a single value expression without template")
  @Test
  public void testSingleValueExpression() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getFirstPlace()).isEqualTo("Augsburg");
  }

  @DisplayName("shall evaluate a single valued expression with a function")
  @Test
  public void testSingleValueExpressionWithFunction() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getDateScan()).isEqualTo("2019-11-19");
  }

  @DisplayName("shall return multivalued contents in the same order as in the bind")
  @Test
  public void testMultivaluedFieldsAndTheirOrder() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getPlaces()).containsExactly("Augsburg", "München", "Aachen");
  }

  @DisplayName("shall return multivalued, multilanguage contents in correct order")
  @Test
  public void testMultivaluedMultilanguageFieldsAndTheirOrder() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getMultlangAlternativeTitles().get(Locale.GERMAN))
        .containsExactly("Chuck Norris Biographie", "Fakten");
    assertThat(mapper.getMultlangAlternativeTitles().get(Locale.ENGLISH))
        .containsExactly("Chuck Norris Biography", "Facts");
  }

  @DisplayName("shall return null for a non matching single value expression")
  @Test
  public void testNonmatchingExpressionReturnsNull() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getNoPlace()).isNull();
  }

  @DisplayName("shall throw an exception, when a template variable is missing")
  @Test
  public void testMissingVariableThrows() {
    assertThatThrownBy(() -> brokenTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Could not resolve template due to missing variables: broken");
  }

  @DisplayName("shall throw an exception, when templates and expressions are used at the same time")
  @Test
  public void testTemplatesAndExpressionsThrowException() {
    assertThatThrownBy(
            () -> brokenStructuredTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining(
            "An @XPathBinding must have one of `variables` or `expressions`, but both were set!");
  }

  @DisplayName("shall throw an exception, when neither templates nor expressions are defined")
  @Test
  public void testNoTemplatesAndExpressionsThrowException() {
    assertThatThrownBy(() -> incompleteTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining(
            "An @XPathBinding must have one of `variables` or `expressions`, but neither were set!");
  }

  @DisplayName("shall throw an exception, when the type of a single valued field is wrong")
  @Test
  public void testWrongTypeOfSingleValuedFieldsThrowsException() {
    assertThatThrownBy(() -> wrongFieldTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName(
      "shall throw an exception, when the type of a single valued setter argument is wrong")
  @Test
  public void testWrongTypeOfSingleValuedArgumentsThrowsException() {
    assertThatThrownBy(() -> wrongArgumentTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a multivalued field is wrong")
  @Test
  public void testWrongTypeOfMultiValuedFieldsThrowsException() {
    assertThatThrownBy(
            () -> wrongMultivalueFieldTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a multivalued setter argument is wrong")
  @Test
  public void testWrongTypeOfMultiValuedArgumentsThrowsException() {
    assertThatThrownBy(
            () -> wrongMultivalueArgumentTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a multilanguage field is wrong")
  @Test
  public void testWrongTypeOfMultilanguageFieldsThrowsException() {
    assertThatThrownBy(
            () -> wrongMultilanguageFieldTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName(
      "shall throw an exception, when the type of a multilanguage setter argument is wrong")
  @Test
  public void testWrongTypeOfMultilanguageArgumentsThrowsException() {
    assertThatThrownBy(
            () ->
                wrongMultilanguageArgumentTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("returns a nullpointer, when embedded mappers lack @XPathBinding annotations")
  @Test
  public void testInvalidHierarchy() throws XPathMappingException {
    HierarchicalMapper mapper = hierarchicalMapperFixture.setUpMapperWithResource("simple.xml");
    assertThat(mapper.getUnaccessibleInnerMapper()).isNull();
  }

  @DisplayName("shall pass down root path definitions on hierarchies on class mappers")
  @Test
  public void shallPassDownRootPathsOnInterfaceHierarchies() throws XPathMappingException {
    HierarchicalMapper mapper = hierarchicalMapperFixture.setUpMapperWithResource("simple.xml");
    assertThat(mapper.getInnerMapper().getAuthor()).isEqualTo("Chuck Norris");
  }

  @DisplayName("shall handle collections of nested types")
  @Test
  public void testNestedTypeCollections() throws XPathMappingException {
    MultiNestedInnerRoot innerMapper =
        new XPathMapperFixture<>(MultiNestedInnerRoot.class)
            .setUpMapperWithResource("nested-multi.xml");
    assertThat(innerMapper.persons).hasSize(3);
    assertThat(innerMapper.persons.get(0))
        .hasFieldOrPropertyWithValue("name", "Chuck Norris")
        .hasFieldOrPropertyWithValue("id", "1337");
    assertThat(innerMapper.persons.get(1))
        .hasFieldOrPropertyWithValue("name", "Frank Zappa")
        .hasFieldOrPropertyWithValue("id", "42");
    assertThat(innerMapper.persons.get(2))
        .hasFieldOrPropertyWithValue("name", "Bobby Brown")
        .hasFieldOrPropertyWithValue("id", "1970");
    MultiNestedOuterRoot outerMapper =
        new XPathMapperFixture<>(MultiNestedOuterRoot.class)
            .setUpMapperWithResource("nested-multi.xml");
    assertThat(outerMapper.persons.get(2))
        .hasFieldOrPropertyWithValue("name", "Bobby Brown")
        .hasFieldOrPropertyWithValue("id", "1970");
  }

  @DisplayName("shall handle nested empty root paths")
  @Test
  public void testNestedEmptyRootPaths() throws XPathMappingException {
    EmptyXPathRootOuterRoot outerMapper =
        new XPathMapperFixture<>(EmptyXPathRootOuterRoot.class)
            .setUpMapperWithResource("bsbstruc.xml");

    assertThat(outerMapper.getEmptyXPathRootInnerRoot()).isNotNull();
    assertThat(outerMapper.getEmptyXPathRootInnerRoot().getXmlId()).isEqualTo("bsb00050852");
  }

  @DisplayName(
      "shall be able to evaluate statements, which return integer values by returning their string representation")
  @Test
  public void testStatementsWithIntegerResult() throws XPathMappingException {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getAmountPlaces()).isEqualTo(3);
  }

  @DisplayName(
      "shall be able to evaluate statements, which return a node by returning its string representation")
  @Test
  public void testStatementsWithReturnNode() throws XPathMappingException {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getFirstPersNameNode()).contains("Kugelmann, Hans");
    assertThat(mapper.getFirstPersNameNode()).contains("Name, English");
  }
  // ---------------------------------------------------------------------------------------------

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class TestMapper {

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
          @XPathVariable(
              name = "author",
              paths = {BIBLSTRUCT_PATH + "/monogr/author/persName/name"})
        })
    Map<Locale, String> author;

    Map<Locale, String> getAuthor() {
      return author;
    }

    @XPathBinding("count(" + BIBLSTRUCT_PATH + "/monogr/author)>0")
    Boolean containsAuthors;
    public Boolean containsAuthors() {
      return containsAuthors;
    }


    List<Element> authorElements;

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/author/persName/name")
    void setAuthorElement(List<Element> authorElements) {
      this.authorElements = authorElements;
    }

    List<Element> getAuthorElements() {
      return authorElements;
    }

    String germanAuthor;

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
          @XPathVariable(
              name = "author",
              paths = {BIBLSTRUCT_PATH + "/monogr/author/persName/name"})
        })
    void setLocalizedAuthors(Map<Locale, String> localizedAuthors) {
      this.germanAuthor = localizedAuthors.get(Locale.GERMAN);
    }

    String getGermanAuthor() {
      return germanAuthor;
    }

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
          @XPathVariable(
              name = "author",
              paths = {BIBLSTRUCT_PATH + "/monogr/author[@type=\"ChuckNorris\"]/persName/name"})
        })
    String noAuthor;

    String getNoAuthor() {
      return noAuthor;
    }

    @XPathBinding(
        valueTemplate = "{title}<: {subtitle}>< [. {partNumber}<, {partTitle}>]>",
        variables = {
          @XPathVariable(
              name = "title",
              paths = {BIBLSTRUCT_PATH + "/monogr/title[@type=\"main\"]"}),
          @XPathVariable(
              name = "subtitle",
              paths = {BIBLSTRUCT_PATH + "/monogr/title[@type=\"sub\"]"}),
          @XPathVariable(
              name = "partNumber",
              paths = {BIBLSTRUCT_PATH + "/series/biblScope[@ana=\"#norm\"]"}),
          @XPathVariable(
              name = "partTitle",
              paths = {BIBLSTRUCT_PATH + "/monogr/title[@type=\"part\"]"})
        })
    Map<Locale, String> title;

    Map<Locale, String> getTitle() {
      return title;
    }

    Map<Locale, String> authorFromExpression;

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/author/persName/name")
    void setAuthorFromExpression(Map<Locale, String> authorFromExpression) {
      this.authorFromExpression = authorFromExpression;
    }

    Map<Locale, String> getAuthorFromExpression() {
      return authorFromExpression;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/imprint/pubPlace[1]")
    String firstPlace;

    String getFirstPlace() {
      return firstPlace;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/imprint/pubPlace")
    List<String> places;

    List<String> getPlaces() {
      return places;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/title[@type=\"alt\" and @subtype=\"main\"]")
    Map<Locale, List<String>> multilangAlternativeTitles;

    Map<Locale, List<String>> getMultlangAlternativeTitles() {
      return multilangAlternativeTitles;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/imprint/noPlace[1]")
    String noPlace;

    String getNoPlace() {
      return noPlace;
    }

    @XPathBinding(
        "substring(/TEI/teiHeader/fileDesc/notesStmt/note[@type=\"digDate\"]/date[@ana=\"#scan\"]/@when,1,10)")
    String dateScan;

    public String getDateScan() {
      return dateScan;
    }

    @XPathBinding("count(" + BIBLSTRUCT_PATH + "/monogr/imprint/pubPlace)")
    void setAmountPlaces(String strAmountPlaces) {
      this.amountPlaces = Integer.parseInt(strAmountPlaces);
    }

    int amountPlaces;

    public int getAmountPlaces() {
      return amountPlaces;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/author/persName[1]")
    String firstPersNameNode;

    public String getFirstPersNameNode() {
      return firstPersNameNode;
    }
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class BrokenTestMapper {

    @XPathBinding(
        valueTemplate = "{broken}",
        variables = {})
    String broken;

    String getBroken() {
      return broken;
    }
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class BrokenStructuredTestMapper {

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
          @XPathVariable(
              name = "author",
              paths = {BIBLSTRUCT_PATH + "/monogr/author/persName/name"})
        },
        value = BIBLSTRUCT_PATH + "/monogr/author/persName/name")
    Map<Locale, String> authorFromExpressionAndVariables;

    Map<Locale, String> getAuthorFromExpressionAndVariables() {
      return authorFromExpressionAndVariables;
    }
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class IncompleteTestMapper {

    @XPathBinding() String nothing;

    String getNothing() {
      return nothing;
    }
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class WrongFieldTestMapper {

    @XPathBinding("/TEI/facsimile/surface/@xml:id[1]")
    Integer wrongFieldTypeSingleValued;
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class WrongArgumentTestMapper {

    Integer wrongArgumentTypeSingleValued;

    @XPathBinding("/TEI/facsimile/surface/@xml:id[1]")
    void setWrongArgumentTypeSingleValued(Integer wrongArgumentTypeSingleValued) {
      this.wrongArgumentTypeSingleValued = wrongArgumentTypeSingleValued;
    }
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class WrongMultivalueFieldTestMapper {

    @XPathBinding("/TEI/facsimile/surface/@xml:id")
    List<Integer> wrongFieldTypeMultiValued;
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class WrongMultivalueArgumentTestMapper {

    List<Integer> wrongArgumentTypeMultiValued;

    @XPathBinding("/TEI/facsimile/surface/@xml:id[1]")
    void setWrongArgumentMultiMultiValued(List<Integer> wrongArgumentTypeMultiValued) {
      this.wrongArgumentTypeMultiValued = wrongArgumentTypeMultiValued;
    }
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class WrongMultilanguageFieldTestMapper {

    @XPathBinding(BIBLSTRUCT_PATH + "/monogr/author/persName/name")
    Map<Locale, Integer> wrongFieldTypeMultiLanguage;
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class WrongMultilanguageArgumentTestMapper {

    Map<Locale, Integer> wrongArgumentTypeMultiLanguage;

    @XPathBinding("/TEI/facsimile/surface/@xml:id[1]")
    void setWrongArgumentTypeMultiLanguage(Map<Locale, Integer> wrongArgumentTypeMultiLanguage) {
      this.wrongArgumentTypeMultiLanguage = wrongArgumentTypeMultiLanguage;
    }
  }

  @XPathRoot(value = "/outer")
  public static class HierarchicalMapper {

    @XPathRoot("/inner")
    InnerMapper innerMapper;

    InnerMapper getInnerMapper() {
      return innerMapper;
    }

    UnaccessibleInnerMapper unaccessibleInnerMapper;

    UnaccessibleInnerMapper getUnaccessibleInnerMapper() {
      return unaccessibleInnerMapper;
    }

    public static class InnerMapper {

      @XPathBinding("/author")
      String author;

      String getAuthor() {
        return author;
      }
    }

    public static class UnaccessibleInnerMapper {

      @XPathBinding("/author")
      String author;

      String getAuthor() {
        return author;
      }
    }
  }

  @XPathRoot(value = "/outer", defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class BrokenHierarchicalMapper {

    @XPathRoot("/inner")
    InnerMapper innerMapper;

    InnerMapper getInnerMapper() {
      return innerMapper;
    }

    @XPathRoot("/broken")
    public static class InnerMapper {

      @XPathBinding("/author")
      String author;

      String getAuthor() {
        return author;
      }
    }
  }

  public static class MultiNestedInnerRoot {
    @XPathRoot({"/outer/author", "/outer/character"})
    public List<Person> persons;

    public static class Person {
      @XPathBinding("/name")
      public String name;

      @XPathBinding("/id")
      public String id;
    }
  }

  @XPathRoot("/outer")
  public static class MultiNestedOuterRoot {
    @XPathRoot({"/author", "/character"})
    public List<Person> persons;

    public static class Person {
      @XPathBinding("/name")
      public String name;

      @XPathBinding("/id")
      public String id;
    }
  }

  @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
  public static class EmptyXPathRootOuterRoot {

    @XPathRoot() EmptyXPathRootInnerRoot emptyXPathRootInnerRoot;

    public EmptyXPathRootInnerRoot getEmptyXPathRootInnerRoot() {
      return emptyXPathRootInnerRoot;
    }

    @XPathRoot(defaultNamespace = "http://www.tei-c.org/ns/1.0")
    public static class EmptyXPathRootInnerRoot {

      @XPathBinding("/TEI/@xml:id")
      String xmlId;

      public String getXmlId() {
        return xmlId;
      }
    }
  }
}
