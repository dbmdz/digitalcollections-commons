package de.digitalcollections.prosemirror.model.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import de.digitalcollections.prosemirror.model.api.Content;
import de.digitalcollections.prosemirror.model.api.Document;
import de.digitalcollections.prosemirror.model.api.content.BulletList;
import de.digitalcollections.prosemirror.model.api.content.EmbeddedCodeBlock;
import de.digitalcollections.prosemirror.model.api.content.Heading;
import de.digitalcollections.prosemirror.model.api.content.ListItem;
import de.digitalcollections.prosemirror.model.api.content.Mark;
import de.digitalcollections.prosemirror.model.api.content.OrderedList;
import de.digitalcollections.prosemirror.model.api.content.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.jackson.mixin.BulletListMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.ContentMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.DocumentMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.EmbeddedCodeBlockMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.HeadingMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.ListItemMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.MarkMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.OrderedListMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.ParagraphMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.TextMixIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProseMirrorModule extends Module {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProseMirrorModule.class);

  @Override
  public String getModuleName() {
    return "dc-commons prosemirror jackson module";
  }

  @Override
  public void setupModule(SetupContext context) {
    if ( LOGGER.isDebugEnabled() ) {
      LOGGER.debug("Using " + getModuleName());
    }
    context.setMixInAnnotations(BulletList.class, BulletListMixIn.class);
    context.setMixInAnnotations(Content.class, ContentMixIn.class);
    context.setMixInAnnotations(Document.class, DocumentMixIn.class);
    context.setMixInAnnotations(EmbeddedCodeBlock.class, EmbeddedCodeBlockMixIn.class);
    context.setMixInAnnotations(Heading.class, HeadingMixIn.class);
    context.setMixInAnnotations(ListItem.class, ListItemMixIn.class);
    context.setMixInAnnotations(Mark.class, MarkMixIn.class);
    context.setMixInAnnotations(OrderedList.class, OrderedListMixIn.class);
    context.setMixInAnnotations(Paragraph.class, ParagraphMixIn.class);
    context.setMixInAnnotations(Text.class, TextMixIn.class);
  }

  @Override
  public Version version() {
    return new Version(2, 0, 0, "SNAPSHOT", "de.digitalcollections.dc-commons-prosemirror", "model-jackson");
  }

}
