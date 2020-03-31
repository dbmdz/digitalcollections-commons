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

  public static final String BIBLSTRUCT_PATH = "/tei:TEI/tei:teiHeader/tei:fileDesc/tei:sourceDesc/tei:listBibl/tei:biblStruct";

  XPathMapperFixture<TestMapper> testMapperFixture =
      new XPathMapperFixture<>(TestMapper.class);
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
  XPathMapperFixture<WrongMutivalueFieldTestMapper> wrongMultivalueFieldTestMapperFixture =
      new XPathMapperFixture<>(WrongMutivalueFieldTestMapper.class);
  XPathMapperFixture<WrongMultivalueArgumentTestMapper> wrongMultivalueArgumentTestMapperFixture =
      new XPathMapperFixture<>(WrongMultivalueArgumentTestMapper.class);
  XPathMapperFixture<WrongMutilanguageFieldTestMapper> wrongMultilanguageFieldTestMapperFixture =
      new XPathMapperFixture<>(WrongMutilanguageFieldTestMapper.class);
  XPathMapperFixture<WrongMultilanguageArgumentTestMapper> wrongMultilanguageArgumentTestMapperFixture =
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
    assertThat(mapper.getTitle()).isEqualTo("Ein Titel: Ein Untertitel");
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

  @DisplayName("shall return multivalued contents in the same order as in the bind")
  @Test
  public void testMultivaluedFieldsAndTheirOrder() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getPlaces()).containsExactly("Augsburg","München","Aachen");
  }

  @DisplayName("shall return multivalued, multilanguage contents in correct order")
  @Test
  public void testMultivaluedMultilanguageFieldsAndTheirOrder() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getMultlangAlternativeTitles().get(Locale.GERMAN)).containsExactly("Chuck Norris Biographie","Fakten");
    assertThat(mapper.getMultlangAlternativeTitles().get(Locale.ENGLISH)).containsExactly("Chuck Norris Biography","Facts");
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
    assertThatThrownBy(
        () -> brokenTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("Could not resolve template due to missing variables: broken");
  }

  @DisplayName("shall throw an exception, when templates and expressions are used at the same time")
  @Test
  public void testTemplatesAndExpressionsThrowException() {
    assertThatThrownBy(
        () -> brokenStructuredTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class).hasMessageContaining("An @XPathBinding must have one of `variables` or `expressions`, but both were set!");
  }

  @DisplayName("shall throw an exception, when neither templates nor expressions are defined")
  @Test
  public void testNoTemplatesAndExpressionsThrowException() {
    assertThatThrownBy(
        () -> incompleteTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class)
        .hasMessageContaining("An @XPathBinding must have one of `variables` or `expressions`, but neither were set!");
  }

  @DisplayName("shall throw an exception, when the type of a single valued field is wrong")
  @Test
  public void testWrongTypeOfSingleValuedFieldsThrowsException() {
    assertThatThrownBy(
        () -> wrongFieldTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a single valued setter argument is wrong")
  @Test
  public void testWrongTypeOfSingleValuedArgumentsThrowsException() {
    assertThatThrownBy(
        () -> wrongArgumentTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a multivalued field is wrong")
  @Test
  public void testWrongTypeOfMultiValuedFieldsThrowsException() {
    assertThatThrownBy(
        () -> wrongMultivalueFieldTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a multivalued setter argument is wrong")
  @Test
  public void testWrongTypeOfMultiValuedArgumentsThrowsException() {
    assertThatThrownBy(
        () -> wrongMultivalueArgumentTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a multilanguage field is wrong")
  @Test
  public void testWrongTypeOfMultilanguageFieldsThrowsException() {
    assertThatThrownBy(
        () -> wrongMultilanguageFieldTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal target type");
  }

  @DisplayName("shall throw an exception, when the type of a multilanguage setter argument is wrong")
  @Test
  public void testWrongTypeOfMultilanguageArgumentsThrowsException() {
    assertThatThrownBy(
        () -> wrongMultilanguageArgumentTestMapperFixture.setUpMapperWithResource("bsbstruc.xml"))
        .isInstanceOf(XPathMappingException.class).hasMessageContaining("Binding method has illegal target type");
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
  // ---------------------------------------------------------------------------------------------

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class TestMapper {



    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
            @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
        }
    )
    Map<Locale, String> author;
    Map<Locale, String> getAuthor() {
      return author;
    };

    List<Element> authorElements;
    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
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
            @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
        }
    )
    void setLocalizedAuthors(Map<Locale, String> localizedAuthors) {
      this.germanAuthor = localizedAuthors.get(Locale.GERMAN);
    }
    String getGermanAuthor() {
      return germanAuthor;
    }

    @XPathBinding(
        valueTemplate = "{author}",
        variables = {@XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author[@type=\"ChuckNorris\"]/tei:persName/tei:name"})
        }
    )
    String noAuthor;
    String getNoAuthor() {
      return noAuthor;
    }

    @XPathBinding(
        valueTemplate = "{title}<: {subtitle}>< [. {partNumber}<, {partTitle}>]>",
        variables = {
          @XPathVariable(name = "title", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"main\"]"}),
          @XPathVariable(name = "subtitle", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"sub\"]"}),
          @XPathVariable(name = "partNumber", paths = {BIBLSTRUCT_PATH + "/tei:series/tei:biblScope[@ana=\"#norm\"]"}),
          @XPathVariable(name = "partTitle", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"part\"]"})
        }
    )
    String title;
    String getTitle() {
      return title;
    }

    Map<Locale, String> authorFromExpression;
    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
    void setAuthorFromExpression(Map<Locale, String> authorFromExpression) {
      this.authorFromExpression = authorFromExpression;
    }
    Map<Locale, String> getAuthorFromExpression() {
      return authorFromExpression;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:pubPlace[1]")
    String firstPlace;
    String getFirstPlace() {
      return firstPlace;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:pubPlace")
    List<String> places;
    List<String> getPlaces() {
      return places;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"alt\" and @subtype=\"main\"]")
    Map<Locale,List<String>> multilangAlternativeTitles;
    Map<Locale,List<String>> getMultlangAlternativeTitles() {
      return multilangAlternativeTitles;
    }

    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:noPlace[1]")
    String noPlace;
    String getNoPlace() {
      return noPlace;
    }
  }

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class BrokenTestMapper {
    @XPathBinding(valueTemplate = "{broken}", variables = {})
    String broken;
    String getBroken() {
      return broken;
    }
  }

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class BrokenStructuredTestMapper {
    @XPathBinding(
        valueTemplate = "{author}",
        variables = {
            @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
        },
        value = BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"
    )
    Map<Locale, String> authorFromExpressionAndVariables;
    Map<Locale, String> getAuthorFromExpressionAndVariables() {
      return authorFromExpressionAndVariables;
    }
  }

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class IncompleteTestMapper {
    @XPathBinding()
    String nothing;
    String getNothing() {
      return nothing;
    }
  }

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class WrongFieldTestMapper {
    @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id[1]")
    Integer wrongFieldTypeSingleValued;
  }


  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class WrongArgumentTestMapper {
    Integer wrongArgumentTypeSingleValued;
    @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id[1]")
    void setWrongArgumentTypeSingleValued(Integer wrongArgumentTypeSingleValued) {
      this.wrongArgumentTypeSingleValued = wrongArgumentTypeSingleValued;
    }
  }

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class WrongMutivalueFieldTestMapper {
    @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id")
    List<Integer> wrongFieldTypeMultiValued;
  }


  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class WrongMultivalueArgumentTestMapper {
    List<Integer> wrongArgumentTypeMultiValued;
    @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id[1]")
    void setWrongArgumentMultiMultiValued(List<Integer> wrongArgumentTypeMultiValued) {
      this.wrongArgumentTypeMultiValued = wrongArgumentTypeMultiValued;
    }
  }

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class WrongMutilanguageFieldTestMapper {
    @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
    Map<Locale, Integer> wrongFieldTypeMultiLanguage;
  }


  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class WrongMultilanguageArgumentTestMapper {
    Map<Locale, Integer> wrongArgumentTypeMultiLanguage;
    @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id[1]")
    void setWrongArgumentTypeMultiLanguage(Map<Locale, Integer> wrongArgumentTypeMultiLanguage) {
      this.wrongArgumentTypeMultiLanguage = wrongArgumentTypeMultiLanguage;
    }
  }


  @XPathRoot(
      value = "/outer",
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
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

  @XPathRoot(
      value = "/outer",
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
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


}
