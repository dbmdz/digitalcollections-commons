package de.digitalcollections.prosemirror.model.jackson.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.ListItem;
import de.digitalcollections.prosemirror.model.api.contentblocks.OrderedList;
import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.OrderedListImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import de.digitalcollections.prosemirror.model.jackson.BaseProseMirrorObjectMapperTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderedListTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    OrderedList orderedList = new OrderedListImpl();

    checkSerializeDeserialize(orderedList);
  }

  @Test
  public void testWithListItems() throws Exception {
    OrderedList orderedList = new OrderedListImpl();
    orderedList.addAttribute("order", 1);

    ListItem item1 = new ListItemImpl();
    Paragraph paragraph1 = new ParagraphImpl();
    Text content1 = new TextImpl("Punkt 1");
    paragraph1.addContentBlock(content1);
    item1.addContentBlock(paragraph1);
    orderedList.addContentBlock(item1);

    ListItem item2 = new ListItemImpl();
    Paragraph paragraph2 = new ParagraphImpl();
    Text content2 = new TextImpl("Punkt 2");
    paragraph2.addContentBlock(content2);
    item2.addContentBlock(paragraph2);
    orderedList.addContentBlock(item2);

    checkSerializeDeserialize(orderedList);
  }

  @Test
  public void testDeserializationWithContents() throws Exception {
    String jsonString = "{\n"
            + "  \"type\": \"ordered_list\",\n"
            + "  \"attrs\": {\"order\": 1},\n"
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

    OrderedList orderedList = mapper.readValue(jsonString, OrderedList.class);

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

    assertThat(orderedList.getContentBlocks()).containsExactly(item1, item2, item3);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("order", 1);
    assertThat(orderedList.getAttributes()).isEqualTo(attributes);
  }

}
