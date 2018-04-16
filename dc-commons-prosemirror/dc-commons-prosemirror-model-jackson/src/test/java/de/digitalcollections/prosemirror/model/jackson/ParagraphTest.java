package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.HardBreak;
import de.digitalcollections.prosemirror.model.api.content.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.content.HardBreakImpl;
import de.digitalcollections.prosemirror.model.impl.content.MarkImpl;
import de.digitalcollections.prosemirror.model.impl.content.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import org.junit.jupiter.api.Disabled;
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

  @Test
  @Disabled("This is not valid JSON!")
  public void testDeserializationWithContentsWithMarks() throws Exception {
    String jsonString = "{\n"
        + "  \"type\": \"paragraph\",\n"
        + "  \"content\": [\n"
        + "    {\n"
        + "      \"type\": \"text\",\n"
        + "      \"marks\": [\n"
        + "        {\n"
        + "          \"type\": \"strong\"\n"
        + "        }\n"
        + "      ],\n"
        + "      \"text\": \"Telefon:\"\n"
        + "    },\n"
        + "    {\n"
        + "      \"type\": \"text\",\n"
        + "      \"text\": \" +49 89 28638-0\"\n"
        + "    },\n"
        + "    {\n"
        + "      \"type\": \"hard_break\"\n"
        + "    },\n"
        + "    {\n"
        + "      \"type\": \"text\",\n"
        + "      \"marks\": [\n"
        + "        {\n"
        + "          \"type\": \"strong\",\n"
        + "          \"type\": \"em\"\n"
        + "        }\n"
        + "      ],\n"
        + "      \"text\": \"Fax:\"\n"
        + "    },\n"
        + "    {\n"
        + "      \"type\": \"text\",\n"
        + "      \"text\": \" +49 89 28638-2200\"\n"
        + "    }\n"
        + "  ]\n"
        + "}";

    Paragraph paragraph = mapper.readValue(jsonString, Paragraph.class);

    assertThat(paragraph.getContents()).hasSize(5);

    Text text1 = new TextImpl("Telefon");
    text1.addMark(new MarkImpl("strong"));

    Text text2 = new TextImpl(" +49 89 28638-0");

    HardBreak hardBreak = new HardBreakImpl();

    Text text3 = new TextImpl("Fax:");
    text3.addMark(new MarkImpl("strong"));
    text3.addMark(new MarkImpl("em"));

    Text text4 = new TextImpl(" +49 89 28638-2200");

    assertThat(paragraph.getContents()).containsExactly(text1, text2, hardBreak, text3, text4);
  }
}
