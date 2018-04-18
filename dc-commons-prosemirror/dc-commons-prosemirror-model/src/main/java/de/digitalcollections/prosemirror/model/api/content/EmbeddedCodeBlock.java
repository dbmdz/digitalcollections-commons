package de.digitalcollections.prosemirror.model.api.content;

import de.digitalcollections.prosemirror.model.api.Content;

public interface EmbeddedCodeBlock extends Content {

  String getCode();

  void setCode(String code);

}
