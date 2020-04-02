package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.ListItem;
import de.digitalcollections.prosemirror.model.impl.NodeContentBlockImpl;

public class ListItemImpl extends NodeContentBlockImpl implements ListItem {

  public ListItemImpl() {}

  public ListItemImpl(String text) {
    addContentBlock(new ParagraphImpl(text));
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
    return getClass().getSimpleName() + "{hashCode=" + hashCode() + "}";
  }
}
