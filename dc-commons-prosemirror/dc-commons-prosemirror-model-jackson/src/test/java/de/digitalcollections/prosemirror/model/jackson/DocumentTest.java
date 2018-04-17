package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.Content;
import de.digitalcollections.prosemirror.model.api.Document;
import de.digitalcollections.prosemirror.model.impl.DocumentImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class DocumentTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testSerialization() throws Exception {
    Document document = new DocumentImpl();

    List<Content> contentBlocks = new ArrayList<Content>();
    contentBlocks.add(new TextImpl("Test"));
    document.addContentBlocks(Locale.GERMAN, contentBlocks);

    checkSerializeDeserialize(document);
  }

}
