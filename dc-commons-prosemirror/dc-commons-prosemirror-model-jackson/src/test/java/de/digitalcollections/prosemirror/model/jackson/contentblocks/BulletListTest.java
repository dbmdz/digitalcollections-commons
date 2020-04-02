package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.prosemirror.model.api.contentblocks.BulletList;
import de.digitalcollections.prosemirror.model.api.contentblocks.ListItem;
import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.contentblocks.BulletListImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import org.junit.jupiter.api.Test;

public class BulletListTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    BulletList bulletList = new BulletListImpl();

    checkSerializeDeserialize(bulletList);
  }

  @Test
  public void testWithListItems() throws Exception {
    BulletList bulletList = new BulletListImpl();

    ListItem item1 = new ListItemImpl();
    Paragraph paragraph1 = new ParagraphImpl();
    Text content1 = new TextImpl("Punkt 1");
    paragraph1.addContentBlock(content1);
    item1.addContentBlock(paragraph1);
    bulletList.addContentBlock(item1);

    ListItem item2 = new ListItemImpl();
    Paragraph paragraph2 = new ParagraphImpl();
    Text content2 = new TextImpl("Punkt 2");
    paragraph2.addContentBlock(content2);
    item2.addContentBlock(paragraph2);
    bulletList.addContentBlock(item2);

    checkSerializeDeserialize(bulletList);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString =
        "{\n"
            + "  \"type\": \"bullet_list\",\n"
            + "  \"content\": [\n"
            + "    {\n"
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
            + "    },\n"
            + "    {\n"
            + "      \"type\": \"list_item\",\n"
            + "      \"content\": [\n"
            + "        {\n"
            + "          \"type\": \"paragraph\",\n"
            + "          \"content\": [\n"
            + "            {\n"
            + "              \"type\": \"text\",\n"
            + "              \"text\": \"test 2\"\n"
            + "            }\n"
            + "          ]\n"
            + "        }\n"
            + "      ]\n"
            + "    },\n"
            + "    {\n"
            + "      \"type\": \"list_item\",\n"
            + "      \"content\": [\n"
            + "        {\n"
            + "          \"type\": \"paragraph\",\n"
            + "          \"content\": [\n"
            + "            {\n"
            + "              \"type\": \"text\",\n"
            + "              \"text\": \"test 3\"\n"
            + "            }\n"
            + "          ]\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    BulletList bulletList = mapper.readValue(jsonString, BulletList.class);

    Paragraph paragraph1 = new ParagraphImpl();
    paragraph1.addContentBlock(new TextImpl("test 1"));
    ListItem item1 = new ListItemImpl();
    item1.addContentBlock(paragraph1);

    Paragraph paragraph2 = new ParagraphImpl();
    paragraph2.addContentBlock(new TextImpl("test 2"));
    ListItem item2 = new ListItemImpl();
    item2.addContentBlock(paragraph2);

    Paragraph paragraph3 = new ParagraphImpl();
    paragraph3.addContentBlock(new TextImpl("test 3"));
    ListItem item3 = new ListItemImpl();
    item3.addContentBlock(paragraph3);

    assertThat(bulletList.getContentBlocks()).containsExactly(item1, item2, item3);
  }
}
