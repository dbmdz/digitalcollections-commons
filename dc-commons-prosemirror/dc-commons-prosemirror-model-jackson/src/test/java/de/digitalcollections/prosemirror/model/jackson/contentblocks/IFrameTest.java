package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.IFrame;
import de.digitalcollections.prosemirror.model.impl.contentblocks.IFrameImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IFrameTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    IFrame embeddedCodeBlock = new IFrameImpl();

    checkSerializeDeserialize(embeddedCodeBlock);
  }

  @Test
  public void testDeserialization() throws Exception {
    String jsonString = "{\n"
            + "  \"type\": \"iframe\",\n"
            + "  \"attrs\": {\n"
            + "    \"src\": \"https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de\",\n"
            + "    \"width\": \"98%\",\n"
            + "    \"height\": \"auto\"\n"
            + "  }\n"
            + "}";

    IFrame iframe = mapper.readValue(jsonString, IFrame.class);

    assertThat(iframe).isNotNull();
    assertThat(((String) iframe.getAttribute("src")))
            .isEqualTo("https://statistiken.digitale-sammlungen.de/index.php?module=CoreAdminHome&amp;action=optOut&amp;language=de");
    assertThat(((String) iframe.getAttribute("height"))).isEqualTo("auto");
    assertThat(((String) iframe.getAttribute("width"))).isEqualTo("98%");
  }

}
