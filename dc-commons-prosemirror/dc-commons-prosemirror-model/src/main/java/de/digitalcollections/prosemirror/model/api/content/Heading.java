package de.digitalcollections.prosemirror.model.api.content;

import java.util.List;
import java.util.Map;

public interface Heading extends Content {

  Map<String, Object> getAttributes();

  void setAttributes(Map<String, Object> attributes);

  void addAttribute(String key, Object value);

  Object getAttribute(String key);

  List<Content> getContents();

  void setContents(List<Content> contents);

  void addContent(Content content);
}
