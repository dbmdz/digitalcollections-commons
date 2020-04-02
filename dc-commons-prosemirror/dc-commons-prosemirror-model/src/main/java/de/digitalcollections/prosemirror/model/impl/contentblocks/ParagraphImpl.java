package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.Paragraph;
import de.digitalcollections.prosemirror.model.impl.NodeContentBlockImpl;

public class ParagraphImpl extends NodeContentBlockImpl implements Paragraph {

  public ParagraphImpl() {}

  public ParagraphImpl(String text) {
    addContentBlock(new TextImpl(text));
  }
}
