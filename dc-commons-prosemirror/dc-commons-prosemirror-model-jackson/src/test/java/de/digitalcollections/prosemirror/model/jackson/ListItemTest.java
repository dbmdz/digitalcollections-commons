package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.ListItem;
import de.digitalcollections.prosemirror.model.api.content.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.content.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.content.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ListItemTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    ListItem listItem = new ListItemImpl();

    checkSerializeDeserialize(listItem);
  }

  @Test
  public void testWithParagraphAsContent() throws Exception {
    ListItem listItem = new ListItemImpl();
    Paragraph paragraph = new ParagraphImpl();
    Text content = new TextImpl("Das ist ein Test");
    paragraph.addContent(content);
    listItem.addContent(content);

    checkSerializeDeserialize(listItem);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString = "{\n"
        + "      \"type\": \"list_item\",\n"
        + "      \"content\": [\n"
        + "        {\n"
        + "          \"type\": \"paragraph\",\n"
        + "          \"content\": [\n"
        + "            {\n"
        + "              \"type\": \"text\",\n"
        + "              \"text\": \"test 1\"\n"
        + "            }\n"
        + "          ]\n"
        + "        }\n"
        + "      ]\n"
        + "    }";

    ListItem listItem = mapper.readValue(jsonString, ListItem.class);
    assertThat(listItem).isNotNull();
    assertThat(listItem.getContents()).isNotNull();

    Text text = new TextImpl("test 1");
    Paragraph paragraph = new ParagraphImpl();
    paragraph.addContent(text);

    assertThat(listItem.getContents()).containsExactly(paragraph);
  }

}
