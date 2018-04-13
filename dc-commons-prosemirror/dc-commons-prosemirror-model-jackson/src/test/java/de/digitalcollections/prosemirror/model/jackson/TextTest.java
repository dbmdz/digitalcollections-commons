package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    Text text = new TextImpl();

    checkSerializeDeserialize(text);
  }

  @Test
  public void testWithConstructor() throws Exception {
    Text text = new TextImpl("MDZ");

    checkSerializeDeserialize(text);
  }

  @Test
  public void testWithSetters() throws Exception {
    Text text = new TextImpl();
    text.setText("MDZ");

    checkSerializeDeserialize(text);
  }

  @Test
  public void testDeserializationWithEmptyText() throws Exception {
    String jsonString = "{\n"
        + "          \"type\": \"text\"\n"
        + "        }";

    Text text = mapper.readValue(jsonString, Text.class);
    assertThat(text).isNotNull();
    assertThat(text.getText()).isNull();
  }


  @Test
  public void testDeserializationWithText() throws Exception {
    String jsonString = "{\n"
        + "          \"type\": \"text\",\n"
        + "          \"text\": \"Impressum\"\n"
        + "        }";

    Text text = mapper.readValue(jsonString, Text.class);
    assertThat(text).isNotNull();
    assertThat(text.getText()).isEqualTo("Impressum");
  }


}
