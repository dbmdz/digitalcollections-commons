package de.digitalcollections.prosemirror.model.api;

import java.util.List;

public interface Content {

  List<Content> getContents();

  void setContents(List<Content> contents);

  void addContent(Content content);

}