package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.EmbeddedCode;
import de.digitalcollections.prosemirror.model.impl.contentblocks.EmbeddedCodeImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedCodeTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    EmbeddedCode embeddedCodeBlock = new EmbeddedCodeImpl();

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testConstructor() throws Exception {
    EmbeddedCode embeddedCodeBlock = new EmbeddedCodeImpl("<img src=\"foo.jpg\"/>");

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testSetter() throws Exception {
    EmbeddedCode embeddedCodeBlock = new EmbeddedCodeImpl();
    embeddedCodeBlock.setCode("<img src=\"foo.jpg\"/>");

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testDeserialization() throws Exception {
    String jsonString = "{\n"
            + "  \"type\": \"embedded_code\",\n"
            + "  \"code\": \"<iframe style='border: 1px solid lightgrey' frameborder='no' width='98%' height='auto' src='https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de'></iframe>\"\n"
            + "}";

    EmbeddedCode embeddedCodeBlock = mapper.readValue(jsonString, EmbeddedCode.class);

    assertThat(embeddedCodeBlock).isNotNull();
    assertThat(embeddedCodeBlock.getCode())
            .isEqualTo("<iframe style='border: 1px solid lightgrey' frameborder='no' width='98%' height='auto' src='https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de'></iframe>");
  }

}
