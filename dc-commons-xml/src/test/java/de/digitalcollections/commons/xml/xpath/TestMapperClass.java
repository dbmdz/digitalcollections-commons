package de.digitalcollections.commons.xml.xpath;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TestMapperClass {

  static final String BIBLSTRUCT_PATH =
      "/tei:TEI/tei:teiHeader/tei:fileDesc/tei:sourceDesc/tei:listBibl/tei:biblStruct";

  @XPathBinding(
      valueTemplate = "{author}",
      variables = {
        @XPathVariable(
            name = "author",
            paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
      })
  Map<Locale, String> author;

  Map<Locale, String> getAuthor() {
    return author;
  };

  @XPathBinding(
      valueTemplate = "{author}",
      variables = {
        @XPathVariable(
            name = "author",
            paths = {
              BIBLSTRUCT_PATH
                  + "/tei:monogr/tei:author[@type=\"ChuckNorris\"]/tei:persName/tei:name"
            })
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
            paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"main\"]"}),
        @XPathVariable(
            name = "subtitle",
            paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"sub\"]"}),
        @XPathVariable(
            name = "partNumber",
            paths = {BIBLSTRUCT_PATH + "/tei:series/tei:biblScope[@ana=\"#norm\"]"}),
        @XPathVariable(
            name = "partTitle",
            paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"part\"]"})
      })
  String title;

  String getTitle() {
    return title;
  }

  /*
  @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:title[@type=\"alt\" and @subtype=\"main\"]")
  Map<Locale, List<String>> multlangAlternativeTitles;

  Map<Locale, List<String>> getMultilangAlternativeTitles() {
    return multlangAlternativeTitles;
  }

   */

  /*
  @XPathBinding(valueTemplate = "{broken}", variables = {})
  String broken;

  String getBroken() {
    return broken;
  }
   */

  /*
  FIXME
  @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
  Map<Locale, String> authorFromExpression;

  Map<Locale, String> getAutorFromExpression() {
    return authorFromExpression;
  }
   */

  /*
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

   */

  @XPathBinding() String nothing;

  String getNothing() {
    return nothing;
  }

  @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:pubPlace")
  List<String> places;

  List<String> getPlaces() {
    return places;
  }

  @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:pubPlace[1]")
  String firstPlace;

  String getFirstPlace() {
    return firstPlace;
  }

  @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:imprint/tei:noPlace[1]")
  String noPlace;

  String getNoPlace() {
    return noPlace;
  }

  /*
  @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id[1]")
  Integer wrongFieldTypeSinglevalued;


  @XPathBinding("/tei:TEI/tei:facsimile/tei:surface/@xml:id")
  List<Integer> wrongFieldTypeMultivalued;

  @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
  Map<Locale, Integer> wrongReturnTypeMultiLanguage;

  @XPathBinding(BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name")
  String wrongFieldTypeExplicitMultiLanguage;

  @XPathBinding(
      valueTemplate = "{author}",
      variables = {
          @XPathVariable(name = "author", paths = {BIBLSTRUCT_PATH + "/tei:monogr/tei:author/tei:persName/tei:name"})
      }
  )
  Map<Locale, List<String>> wrongReturnTypeTemplatedMultiLanguage;

   */

  // TODO test setters
}
