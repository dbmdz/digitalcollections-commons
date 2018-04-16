package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.BulletList;
import de.digitalcollections.prosemirror.model.api.content.ListItem;
import de.digitalcollections.prosemirror.model.api.content.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.content.BulletListImpl;
import de.digitalcollections.prosemirror.model.impl.content.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.content.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    paragraph1.addContent(content1);
    item1.addContent(paragraph1);
    bulletList.addContent(item1);

    ListItem item2= new ListItemImpl();
    Paragraph paragraph2 = new ParagraphImpl();
    Text content2 = new TextImpl("Punkt 2");
    paragraph2.addContent(content2);
    item2.addContent(paragraph2);
    bulletList.addContent(item2);

    checkSerializeDeserialize(bulletList);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString = "{\n"
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
    paragraph1.addContent(new TextImpl("test 1"));
    ListItem item1 = new ListItemImpl();
    item1.addContent(paragraph1);

    Paragraph paragraph2 = new ParagraphImpl();
    paragraph2.addContent(new TextImpl("test 2"));
    ListItem item2 = new ListItemImpl();
    item2.addContent(paragraph2);

    Paragraph paragraph3 = new ParagraphImpl();
    paragraph3.addContent(new TextImpl("test 3"));
    ListItem item3 = new ListItemImpl();
    item3.addContent(paragraph3);

    assertThat(bulletList.getContents()).containsExactly(item1, item2, item3);
  }

}
