package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.EmbeddedCodeBlock;
import de.digitalcollections.prosemirror.model.impl.content.EmbeddedCodeBlockImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedCodeBlockTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    EmbeddedCodeBlock embeddedCodeBlock = new EmbeddedCodeBlockImpl();

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testConstructor() throws Exception {
    EmbeddedCodeBlock embeddedCodeBlock = new EmbeddedCodeBlockImpl("<img src=\"foo.jpg\"/>");

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testSetter() throws Exception {
    EmbeddedCodeBlock embeddedCodeBlock = new EmbeddedCodeBlockImpl();
    embeddedCodeBlock.setCode("<img src=\"foo.jpg\"/>");

    checkSerializeDeserialize(embeddedCodeBlock);
  }


  @Test
  public void testDeserialization() throws Exception {
    String jsonString = "{\n"
        + "  \"type\": \"embedded_code_block\",\n"
        + "  \"code\": \"<iframe style='border: 1px solid lightgrey' frameborder='no' width='98%' height='auto' src='https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de'></iframe>\"\n"
        + "}";

    EmbeddedCodeBlock embeddedCodeBlock = mapper.readValue(jsonString, EmbeddedCodeBlock.class);

    assertThat(embeddedCodeBlock).isNotNull();
    assertThat(embeddedCodeBlock.getCode()).isEqualTo("<iframe style='border: 1px solid lightgrey' frameborder='no' width='98%' height='auto' src='https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de'></iframe>");
  }


}
