package de.digitalcollections.prosemirror.model.api.contentblocks;

import de.digitalcollections.prosemirror.model.api.ContentBlock;

public interface EmbeddedCode extends ContentBlock {

  String getCode();

  void setCode(String code);

}
