package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.Mark;
import de.digitalcollections.prosemirror.model.impl.content.MarkImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
  public void testDeserialization() throws Exception {
    String jsonString = "{\n"
        + "          \"type\": \"em\"\n"
        + "        }";

    Mark mark = mapper.readValue(jsonString, Mark.class);
    assertThat(mark).isNotNull();
    assertThat(mark.getType()).isEqualTo("em");
  }

}
