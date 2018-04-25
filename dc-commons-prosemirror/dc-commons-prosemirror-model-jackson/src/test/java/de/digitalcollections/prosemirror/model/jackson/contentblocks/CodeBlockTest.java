package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.CodeBlock;
import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.contentblocks.CodeBlockImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeBlockTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    CodeBlockImpl codeBlock = new CodeBlockImpl();
    checkSerializeDeserialize(codeBlock);
  }

  @Test
  public void testWithParagraphAsContent() throws Exception {
    CodeBlockImpl codeBlock = new CodeBlockImpl();
    Paragraph paragraph = new ParagraphImpl();
    Text content = new TextImpl("Das ist ein Test");
    paragraph.addContentBlock(content);
    codeBlock.addContentBlock(content);

    checkSerializeDeserialize(codeBlock);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString = "{\n"
            + "      \"type\": \"code_block\",\n"
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

    CodeBlock codeBlock = mapper.readValue(jsonString, CodeBlock.class);
    assertThat(codeBlock).isNotNull();
    assertThat(codeBlock.getContentBlocks()).isNotNull();

    Text text = new TextImpl("test 1");
    Paragraph paragraph = new ParagraphImpl();
    paragraph.addContentBlock(text);

    assertThat(codeBlock.getContentBlocks()).containsExactly(paragraph);
  }

}
