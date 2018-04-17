package de.digitalcollections.prosemirror.model.api.content;

import de.digitalcollections.prosemirror.model.api.Attributes;

public interface Mark extends Attributes {

  String getType();

  void setType(String type);
  
}
