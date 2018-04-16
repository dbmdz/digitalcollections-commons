package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.EmbeddedCodeBlock;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.content.EmbeddedCodeBlockImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedCodeBlockTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    EmbeddedCodeBlock embeddedCodeBlock = new EmbeddedCodeBlockImpl();

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testWithOneBlock() throws Exception {
    EmbeddedCodeBlock embeddedCodeBlock = new EmbeddedCodeBlockImpl();
    Text text = new TextImpl("<img src=\"foo.jpg\"/>");
    embeddedCodeBlock.addContent(text);

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testWithMultipleBlocks() throws Exception {
    EmbeddedCodeBlock embeddedCodeBlock = new EmbeddedCodeBlockImpl();
    Text text1 = new TextImpl("<img src=\"foo.jpg\"/>");
    embeddedCodeBlock.addContent(text1);
    Text text2 = new TextImpl("<img src=\"bar.jpg\"/>");
    embeddedCodeBlock.addContent(text2);

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString = "{\n"
        + "  \"type\": \"embedded_code_block\",\n"
        + "  \"content\": [\n"
        + "    {\n"
        + "     \"type\": \"text\",\n"
        + "     \"text\": \"<iframe style='border: 1px solid lightgrey' frameborder='no' width='98%' height='auto' src='https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de'></iframe>\"\n"
        + "    }\n"
        + "  ]\n"
        + "}";

    EmbeddedCodeBlock embeddedCodeBlock = mapper.readValue(jsonString, EmbeddedCodeBlock.class);

    assertThat(embeddedCodeBlock).isNotNull();
    assertThat(embeddedCodeBlock.getContents()).isNotNull();

    Text block = new TextImpl("<iframe style='border: 1px solid lightgrey' frameborder='no' width='98%' height='auto' src='https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de'></iframe>");
    EmbeddedCodeBlock expectedCodeBlock = new EmbeddedCodeBlockImpl();
    expectedCodeBlock.addContent(block);

    assertThat(embeddedCodeBlock.getContents()).containsExactly(block);
  }


}
