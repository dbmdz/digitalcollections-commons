package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.Heading;

public class HeadingImpl extends ContentWithAttributesImpl implements Heading {

  public HeadingImpl() {}

  public HeadingImpl(int level, String text) {
    addContent(new TextImpl(text));
    addAttribute("level", level);
  }

}
