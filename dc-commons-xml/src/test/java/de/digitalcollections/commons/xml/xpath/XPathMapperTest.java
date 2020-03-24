package de.digitalcollections.commons.xml.xpath;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The XPath Mapper")
public class XPathMapperTest {

  XPathMapperFixture<TestMapper> testMapperFixture =
      new XPathMapperFixture<>(TestMapper.class);

  /*
  @DisplayName("shall evaluate a template with a single variable for a field")
  @Test
  public void testClassTemplateWithSingleVariableForField() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getAuthor().get(Locale.GERMAN)).isEqualTo("Kugelmann, Hans");
    assertThat(mapper.getAuthor().get(Locale.ENGLISH)).isEqualTo("Name, English");
  }
   */

  @DisplayName("shall evaluate a template with a single variable in a setter method")
  @Test
  public void testClassTemplateWithSingleVariableInSetter() throws Exception {
    TestMapper mapper = testMapperFixture.setUpMapperWithResource("bsbstruc.xml");
    assertThat(mapper.getGermanAuthor()).isEqualTo("Kugelmann, Hans");
  }

  // ---------------------------------------------------------------------------------------------

  @XPathRoot(
      defaultNamespace = "http://www.tei-c.org/ns/1.0"
  )
  public static class TestMapper {

    public static final String BIBLSTRUCT_PATH = "/tei:TEI/tei:teiHeader/tei:fileDesc/tei:sourceDesc/tei:listBibl/tei:biblStruct";

    /*
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
     */

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

  }

}
