package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.HardBreak;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.content.HardBreakImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParagraphTest extends BaseProseMirrorObjectMapperTest{

  @Test
  public void testDefaultConstructor() throws Exception {
    Paragraph paragraph = new ParagraphImpl();

    checkSerializeDeserialize(paragraph);
  }

  @Test
  public void testWithOneContent() throws Exception {
    Paragraph paragraph = new ParagraphImpl();
    Text text = new TextImpl("Impressum");
    paragraph.addContent(text);

    checkSerializeDeserialize(paragraph);
  }

  @Test
  public void testWithMultipleContents() throws Exception {
    Paragraph paragraph = new ParagraphImpl();
    Text text1 = new TextImpl("Impressum");
    paragraph.addContent(text1);
    Text text2 = new TextImpl("Datenschutzerklärung");
    paragraph.addContent(text2);

    checkSerializeDeserialize(paragraph);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString = "{\n"
        + "  \"type\": \"paragraph\",\n"
        + "  \"content\": [\n"
        + "    {\n"
        + "      \"type\": \"text\",\n"
        + "      \"text\": \"Ludwigstraße 16\"\n"
        + "    },\n"
        + "    {\n"
        + "      \"type\": \"hard_break\"\n"
        + "    },\n"
        + "    {\n"
        + "      \"type\": \"text\",\n"
        + "      \"text\": \"80539 München\"\n"
        + "    }\n"
        + "  ]\n"
        + "}";


    Paragraph paragraph = mapper.readValue(jsonString, Paragraph.class);
    assertThat(paragraph).isNotNull();
    assertThat(paragraph.getContents()).isNotNull();

    Text text1 = new TextImpl("Ludwigstraße 16");
    HardBreak hardBreak = new HardBreakImpl();
    Text text2 = new TextImpl("80539 München");
    assertThat(paragraph.getContents()).containsExactly(text1, hardBreak, text2);
  }

}
