package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.EmbeddedCodeBlock;

public class EmbeddedCodeBlockImpl extends ContentImpl implements EmbeddedCodeBlock {

  private String code;

  public EmbeddedCodeBlockImpl() {}

  public EmbeddedCodeBlockImpl(String code) {
    setCode(code);
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public void setCode(String code) {
    this.code = code;
  }
}
