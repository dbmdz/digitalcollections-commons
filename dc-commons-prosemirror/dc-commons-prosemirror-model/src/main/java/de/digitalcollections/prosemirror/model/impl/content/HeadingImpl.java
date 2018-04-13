package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.Content;
import de.digitalcollections.prosemirror.model.api.content.Heading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HeadingImpl implements Heading {

  Map<String, Object> attributes = null;
  List<Content> contents;

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public void addAttribute(String key, Object value) {
    if ( attributes == null ) {
      attributes = new HashMap<>();
    }

    attributes.put(key, value);
  }

  @Override
  public Object getAttribute(String key) {
    if ( attributes == null ) {
      return null;
    }

    return attributes.get(key);
  }

  @Override
  public List<Content> getContents() {
    return contents;
  }

  @Override
  public void setContents(List<Content> contents) {
    this.contents = contents;
  }

  @Override
  public void addContent(Content content) {
    if ( contents == null ) {
      contents = new ArrayList<>();
    }

    contents.add(content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HeadingImpl heading = (HeadingImpl) o;
    return Objects.equals(attributes, heading.attributes) &&
        Objects.equals(contents, heading.contents);
  }

  @Override
  public int hashCode() {

    return Objects.hash(attributes, contents);
  }
}
