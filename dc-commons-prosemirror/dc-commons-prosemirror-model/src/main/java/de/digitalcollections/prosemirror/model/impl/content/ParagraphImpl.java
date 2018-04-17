package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.Paragraph;

public class ParagraphImpl extends ContentImpl implements Paragraph {

  public ParagraphImpl() {}

  public ParagraphImpl(String text) {
    addContent(new TextImpl(text));
  }

}
