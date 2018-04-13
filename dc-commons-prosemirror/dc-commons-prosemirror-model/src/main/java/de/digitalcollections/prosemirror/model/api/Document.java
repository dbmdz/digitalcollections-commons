package de.digitalcollections.prosemirror.model.api;

import de.digitalcollections.prosemirror.model.api.content.Content;
import java.util.List;

public interface Document {

  List<Content> getContents();

  void setContent(List<Content> contents);

  void addContent(Content content);

}
