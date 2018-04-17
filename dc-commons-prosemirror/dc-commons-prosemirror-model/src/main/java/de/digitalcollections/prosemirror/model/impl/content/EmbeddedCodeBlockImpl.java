package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.EmbeddedCodeBlock;

public class EmbeddedCodeBlockImpl extends ContentImpl implements EmbeddedCodeBlock {

  public EmbeddedCodeBlockImpl() {}

  public EmbeddedCodeBlockImpl(String code) {
    addContent(new TextImpl(code));
  }
}
