package de.digitalcollections.prosemirror.model.api;

import de.digitalcollections.prosemirror.model.api.content.Content;
import java.util.List;

public interface Paragraph {

  List<Content> getContents();

  void setContents(List<Content> contents);

  void addContent(Content content);

}
