package de.digitalcollections.prosemirror.model.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import de.digitalcollections.prosemirror.model.api.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.Content;
import de.digitalcollections.prosemirror.model.api.content.Heading;
import de.digitalcollections.prosemirror.model.jackson.mixin.ContentMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.HeadingMixIn;
import de.digitalcollections.prosemirror.model.jackson.mixin.ParagraphMixIn;
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
    LOGGER.info("Using " + getModuleName());
    context.setMixInAnnotations(Content.class, ContentMixIn.class);
    context.setMixInAnnotations(Heading.class, HeadingMixIn.class);
    context.setMixInAnnotations(Paragraph.class, ParagraphMixIn.class);
  }

  @Override
  public Version version() {
    return new Version(2, 0, 0, "SNAPSHOT", "de.digitalcollections.dc-commons-prosemirror", "model-jackson");
  }

}
