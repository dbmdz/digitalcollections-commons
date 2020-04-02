package de.digitalcollections.prosemirror.model.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.prosemirror.model.api.Mark;
import de.digitalcollections.prosemirror.model.impl.MarkImpl;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MarkTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testConstructor() throws Exception {
    Mark mark = new MarkImpl("strong");

    checkSerializeDeserialize(mark);
  }

  @Test
  public void testSetter() throws Exception {
    Mark mark = new MarkImpl();
    mark.setType("em");

    checkSerializeDeserialize(mark);
  }

  @Test
  public void testTypeAndAttributes() throws Exception {
    Mark mark = new MarkImpl();
    mark.setType("link");
    mark.addAttribute("href", "https://www.bsb-muenchen.de");

    checkSerializeDeserialize(mark);
  }

  @Test
  public void testDeserialization() throws Exception {
    String jsonString = "{\n" + "          \"type\": \"em\"\n" + "        }";

    Mark mark = mapper.readValue(jsonString, Mark.class);
    assertThat(mark).isNotNull();
    assertThat(mark.getType()).isEqualTo("em");
  }

  @Test
  public void testDeserializationWithAttributes() throws Exception {
    String jsonString =
        "{\n"
            + "          \"type\": \"link\",\n"
            + "          \"attrs\": {\n"
            + "             \"href\": \"https://www.km.bayern.de/\",\n"
            + "             \"title\": null\n"
            + "          }\n"
            + "        }";

    Mark mark = mapper.readValue(jsonString, Mark.class);
    assertThat(mark).isNotNull();
    assertThat(mark.getType()).isEqualTo("link");
    Map<String, Object> expectedAttributes = new HashMap<>();
    expectedAttributes.put("href", "https://www.km.bayern.de/");
    expectedAttributes.put("title", null);
    assertThat(mark.getAttributes()).isEqualTo(expectedAttributes);
  }
}
