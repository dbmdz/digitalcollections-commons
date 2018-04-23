package de.digitalcollections.prosemirror.model.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import de.digitalcollections.prosemirror.model.api.ContentBlock;
import de.digitalcollections.prosemirror.model.api.Document;
import de.digitalcollections.prosemirror.model.api.Mark;
import de.digitalcollections.prosemirror.model.api.contentblocks.BulletList;
import de.digitalcollections.prosemirror.model.api.contentblocks.EmbeddedCode;
import de.digitalcollections.prosemirror.model.api.contentblocks.Heading;
import de.digitalcollections.prosemirror.model.api.contentblocks.ListItem;
import de.digitalcollections.prosemirror.model.api.contentblocks.OrderedList;
import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.api.contentblocks.Text;
import de.digitalcollections.prosemirror.model.jackson.mixin.ContentBlockMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.DocumentMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.MarkMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks.BulletListMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks.EmbeddedCodeMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks.HeadingMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks.ListItemMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks.OrderedListMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks.ParagraphMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks.TextMixIn;
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
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Using " + getModuleName());
    }
    context.setMixInAnnotations(BulletList.class, BulletListMixIn.class);
    context.setMixInAnnotations(ContentBlock.class, ContentBlockMixIn.class);
    context.setMixInAnnotations(Document.class, DocumentMixIn.class);
    context.setMixInAnnotations(EmbeddedCode.class, EmbeddedCodeMixIn.class);
    context.setMixInAnnotations(Heading.class, HeadingMixIn.class);
    context.setMixInAnnotations(ListItem.class, ListItemMixIn.class);
    context.setMixInAnnotations(Mark.class, MarkMixIn.class);
    context.setMixInAnnotations(OrderedList.class, OrderedListMixIn.class);
    context.setMixInAnnotations(Paragraph.class, ParagraphMixIn.class);
    context.setMixInAnnotations(Text.class, TextMixIn.class);
  }

  @Override
  public Version version() {
    return new Version(1, 3, 0, "SNAPSHOT", "de.digitalcollections.dc-commons-prosemirror", "model-jackson");
  }

}
