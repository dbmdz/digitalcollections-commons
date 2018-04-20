package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.Heading;
import de.digitalcollections.prosemirror.model.impl.ContentBlockWithAttributesImpl;

public class HeadingImpl extends ContentBlockWithAttributesImpl implements Heading {

  public HeadingImpl() {
  }

  public HeadingImpl(int level, String text) {
    addContentBlock(new TextImpl(text));
    addAttribute("level", level);
  }

}
