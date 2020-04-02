package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.prosemirror.model.api.contentblocks.HardBreak;
import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.MarkImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.HardBreakImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

public class ParagraphTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    Paragraph paragraph = new ParagraphImpl();

    checkSerializeDeserialize(paragraph);
  }

  @Test
  public void testWithOneContent() throws Exception {
    Paragraph paragraph = new ParagraphImpl();
    Text text = new TextImpl("Impressum");
    paragraph.addContentBlock(text);

    checkSerializeDeserialize(paragraph);
  }

  @Test
  public void testWithMultipleContents() throws Exception {
    Paragraph paragraph = new ParagraphImpl();
    Text text1 = new TextImpl("Impressum");
    paragraph.addContentBlock(text1);
    Text text2 = new TextImpl("Datenschutzerklärung");
    paragraph.addContentBlock(text2);

    checkSerializeDeserialize(paragraph);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString =
        "{\n"
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
    assertThat(paragraph.getContentBlocks()).isNotNull();

    Text text1 = new TextImpl("Ludwigstraße 16");
    HardBreak hardBreak = new HardBreakImpl();
    Text text2 = new TextImpl("80539 München");
    assertThat(paragraph.getContentBlocks()).containsExactly(text1, hardBreak, text2);
  }

  @Test
  public void testDeserializationWithContentsWithMarks() throws Exception {
    String jsonString =
        "{\n"
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
            + "        {\"type\":\"strong\"},\n"
            + "        {\"type\":\"em\"}\n"
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

    assertThat(paragraph.getContentBlocks()).hasSize(5);

    Text text1 = new TextImpl("Telefon:");
    text1.addMark(new MarkImpl("strong"));

    Text text2 = new TextImpl(" +49 89 28638-0");

    HardBreak hardBreak = new HardBreakImpl();

    Text text3 = new TextImpl("Fax:");
    text3.addMark(new MarkImpl("strong"));
    text3.addMark(new MarkImpl("em"));

    Text text4 = new TextImpl(" +49 89 28638-2200");

    assertThat(paragraph.getContentBlocks()).containsExactly(text1, text2, hardBreak, text3, text4);
  }
}
