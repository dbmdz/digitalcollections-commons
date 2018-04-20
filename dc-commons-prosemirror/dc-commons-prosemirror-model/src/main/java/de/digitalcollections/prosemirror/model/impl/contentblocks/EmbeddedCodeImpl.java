package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.impl.ContentBlockImpl;
import de.digitalcollections.prosemirror.model.api.contentblocks.EmbeddedCode;

public class EmbeddedCodeImpl extends ContentBlockImpl implements EmbeddedCode {

  private String code;

  public EmbeddedCodeImpl() {
  }

  public EmbeddedCodeImpl(String code) {
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
