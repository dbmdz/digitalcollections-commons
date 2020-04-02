package de.digitalcollections.prosemirror.model.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.prosemirror.model.api.ContentBlock;
import de.digitalcollections.prosemirror.model.api.Document;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.DocumentImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DocumentTest extends BaseProseMirrorObjectMapperTest {

  private static final String JSON =
      "{      \"type\": \"doc\",\n"
          + "  \"content\": [\n"
          + "    {\n"
          + "      \"type\": \"text\",\n"
          + "      \"text\": \"Test\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  @Test
  public void testSerialization() throws Exception {
    Document document = new DocumentImpl();

    List<ContentBlock> contentBlocks = new ArrayList<>();
    contentBlocks.add(new TextImpl("Test"));
    document.setContentBlocks(contentBlocks);

    checkSerializeDeserialize(document);

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
    assertThat(json.replaceAll("\\s", "")).isEqualTo(JSON.replaceAll("\\s", ""));
  }

  @Test
  public void testDeserializationWithText() throws Exception {

    Document document = mapper.readValue(JSON, Document.class);
    assertThat(document).isNotNull();
    assertThat(document.getContentBlocks().size()).isEqualTo(1);
    assertThat(((Text) document.getContentBlocks().get(0)).getText()).isEqualTo("Test");
  }
}
