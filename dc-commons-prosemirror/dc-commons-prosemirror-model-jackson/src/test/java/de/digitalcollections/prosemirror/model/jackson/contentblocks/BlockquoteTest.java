package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.Blockquote;
import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.contentblocks.BlockquoteImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockquoteTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    BlockquoteImpl blockquote = new BlockquoteImpl();
    checkSerializeDeserialize(blockquote);
  }

  @Test
  public void testWithParagraphAsContent() throws Exception {
    BlockquoteImpl blockquote = new BlockquoteImpl();
    Paragraph paragraph = new ParagraphImpl();
    Text content = new TextImpl("Das ist ein Test");
    paragraph.addContentBlock(content);
    blockquote.addContentBlock(content);

    checkSerializeDeserialize(blockquote);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString = "{\n"
            + "      \"type\": \"blockquote\",\n"
            + "      \"content\": [\n"
            + "        {\n"
            + "          \"type\": \"paragraph\",\n"
            + "          \"content\": [\n"
            + "            {\n"
            + "              \"type\": \"text\",\n"
            + "              \"text\": \"test 1\"\n"
            + "            }\n"
            + "          ]\n"
            + "        }\n"
            + "      ]\n"
            + "    }";

    Blockquote blockquote = mapper.readValue(jsonString, Blockquote.class);
    assertThat(blockquote).isNotNull();
    assertThat(blockquote.getContentBlocks()).isNotNull();

    Text text = new TextImpl("test 1");
    Paragraph paragraph = new ParagraphImpl();
    paragraph.addContentBlock(text);

    assertThat(blockquote.getContentBlocks()).containsExactly(paragraph);
  }

}
