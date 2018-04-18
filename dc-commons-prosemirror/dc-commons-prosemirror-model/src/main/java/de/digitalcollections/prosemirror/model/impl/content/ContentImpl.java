package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.Content;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ContentImpl implements Content {

  protected List<Content> contents;

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
    if (contents == null) {
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
    ContentImpl content = (ContentImpl) o;
    return Objects.equals(contents, content.contents);
  }

  @Override
  public int hashCode() {

    return Objects.hash(contents);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{"
            + "contents=" + contents
            + '}';
  }
}
