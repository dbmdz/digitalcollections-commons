package de.digitalcollections.prosemirror.model.api.content;

public interface Text extends Content {

  String getText();

  void setText(String text);

}
