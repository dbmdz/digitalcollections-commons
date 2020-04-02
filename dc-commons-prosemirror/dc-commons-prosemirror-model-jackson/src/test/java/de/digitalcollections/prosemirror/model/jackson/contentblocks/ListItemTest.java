package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.prosemirror.model.api.contentblocks.ListItem;
import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

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
    paragraph.addContentBlock(content);
    listItem.addContentBlock(content);

    checkSerializeDeserialize(listItem);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString =
        "{\n"
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
    assertThat(listItem.getContentBlocks()).isNotNull();

    Text text = new TextImpl("test 1");
    Paragraph paragraph = new ParagraphImpl();
    paragraph.addContentBlock(text);

    assertThat(listItem.getContentBlocks()).containsExactly(paragraph);
  }
}
