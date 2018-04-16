package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.ListItem;
import de.digitalcollections.prosemirror.model.api.content.OrderedList;
import de.digitalcollections.prosemirror.model.api.content.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.content.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.content.OrderedListImpl;
import de.digitalcollections.prosemirror.model.impl.content.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
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
    orderedList.addAttribute("order",1);

    ListItem item1 = new ListItemImpl();
    Paragraph paragraph1 = new ParagraphImpl();
    Text content1 = new TextImpl("Punkt 1");
    paragraph1.addContent(content1);
    item1.addContent(paragraph1);
    orderedList.addContent(item1);

    ListItem item2= new ListItemImpl();
    Paragraph paragraph2 = new ParagraphImpl();
    Text content2 = new TextImpl("Punkt 2");
    paragraph2.addContent(content2);
    item2.addContent(paragraph2);
    orderedList.addContent(item2);

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

    assertThat(orderedList.getContents()).containsExactly(item1, item2, item3);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("order", 1);
    assertThat(orderedList.getAttributes()).isEqualTo(attributes);
  }

}
