package de.digitalcollections.prosemirror.model.api;

import java.util.List;

public interface NodeContent extends Content {

  List<Content> getContents();

  void setContents(List<Content> contents);

  void addContent(Content content);


}
