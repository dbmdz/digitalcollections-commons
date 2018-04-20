package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.HardBreak;
import de.digitalcollections.prosemirror.model.impl.contentblocks.HardBreakImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HardBreakTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    HardBreak hardBreak = new HardBreakImpl();

    checkSerializeDeserialize(hardBreak);
  }

  @Test
  public void testDeserialization() throws Exception {
    String jsonString = "{\n"
            + "          \"type\": \"hard_break\"\n"
            + "        }";

    HardBreak hardBreak = mapper.readValue(jsonString, HardBreak.class);
    assertThat(hardBreak).isNotNull();
  }

}
