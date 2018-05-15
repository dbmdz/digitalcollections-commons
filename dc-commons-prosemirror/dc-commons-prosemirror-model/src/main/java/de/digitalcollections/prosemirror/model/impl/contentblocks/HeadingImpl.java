package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.Heading;
import de.digitalcollections.prosemirror.model.impl.NodeContentBlockWithAttributesImpl;

public class HeadingImpl extends NodeContentBlockWithAttributesImpl implements Heading {

  public HeadingImpl() {
  }

  public HeadingImpl(int level, String text) {
    addContentBlock(new TextImpl(text));
    addAttribute("level", level);
  }

}
