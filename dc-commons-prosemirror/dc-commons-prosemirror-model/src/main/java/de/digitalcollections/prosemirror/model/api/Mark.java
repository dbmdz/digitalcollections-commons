package de.digitalcollections.prosemirror.model.api;

public interface Mark extends Attributes {

  String getType();

  void setType(String type);

}
