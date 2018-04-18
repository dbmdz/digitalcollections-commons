package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.ListItem;

public class ListItemImpl extends NodeContentImpl implements ListItem {

  public ListItemImpl() {}

  public ListItemImpl(String text) {
    addContent(new ParagraphImpl(text));
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "ListItemImpl{hashCode=" + hashCode() + "}";
  }
}
