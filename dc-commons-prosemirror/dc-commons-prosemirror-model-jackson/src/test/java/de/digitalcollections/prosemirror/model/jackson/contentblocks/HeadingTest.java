package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.Heading;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.contentblocks.HeadingImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HeadingTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    Heading heading = new HeadingImpl();

    checkSerializeDeserialize(heading);
  }

  @Test
  public void testWithTextContentOnly() throws Exception {
    Heading heading = new HeadingImpl();
    Text text = new TextImpl("Impressum");
    heading.addContentBlock(text);

    checkSerializeDeserialize(heading);
  }

  @Test
  public void testWithAttributesOnly() throws Exception {
    Heading heading = new HeadingImpl();
    heading.addAttribute("level", 3);

    checkSerializeDeserialize(heading);
  }

  @Test
  public void testDeserializationWithEmptyContentAndEmptyAttributes() throws Exception {
    String jsonString = "{\n"
            + "          \"type\": \"heading\"\n"
            + "        }";

    Heading heading = mapper.readValue(jsonString, Heading.class);
    assertThat(heading).isNotNull();
    assertThat(heading.getAttributes()).isNull();
    assertThat(heading.getContentBlocks()).isNull();
  }

  @Test
  public void testDeserializationWithAttributesOnly() throws Exception {
    String jsonString = "{\n"
            + "      \"type\": \"heading\",\n"
            + "      \"attrs\": {\n"
            + "        \"level\": 3\n"
            + "      }\n"
            + "    },";

    Heading heading = mapper.readValue(jsonString, Heading.class);
    assertThat(heading).isNotNull();
    assertThat(heading.getAttributes()).isNotNull();
    assertThat(heading.getAttribute("level")).isEqualTo(3);
    assertThat(heading.getAttribute("foo")).isNull();
  }

  @Test
  public void testDeserializationWithAttributesAndContent() throws Exception {
    String jsonString = "{\n"
            + "      \"type\": \"heading\",\n"
            + "      \"attrs\": {\n"
            + "        \"level\": 3\n"
            + "      },\n"
            + "  \"content\": [\n"
            + "    {\n"
            + "      \"type\": \"text\",\n"
            + "      \"text\": \"Impressum\"\n"
            + "    }\n"
            + "  ]\n"
            + "    },";

    Heading heading = mapper.readValue(jsonString, Heading.class);
    assertThat(heading).isNotNull();
    assertThat(heading.getAttributes()).isNotNull();
    assertThat(heading.getAttribute("level")).isEqualTo(3);
    assertThat(heading.getAttribute("foo")).isNull();
    assertThat(heading.getContentBlocks()).isNotEmpty();
    Text impressum = new TextImpl("Impressum");
    assertThat(heading.getContentBlocks().get(0)).isEqualTo(impressum);
  }

}
