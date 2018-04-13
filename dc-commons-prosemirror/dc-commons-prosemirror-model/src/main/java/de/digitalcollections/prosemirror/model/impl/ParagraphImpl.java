package de.digitalcollections.prosemirror.model.impl;

import de.digitalcollections.prosemirror.model.api.Paragraph;
import de.digitalcollections.prosemirror.model.api.content.Content;
import java.util.ArrayList;
import java.util.List;

public class ParagraphImpl implements Paragraph {

  private List<Content> contents;

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
}
