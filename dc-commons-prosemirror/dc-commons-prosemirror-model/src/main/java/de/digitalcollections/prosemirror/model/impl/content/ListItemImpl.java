package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.ListItem;

public class ListItemImpl extends ContentImpl implements ListItem {

  public ListItemImpl() {}

  public ListItemImpl(String text) {
    addContent(new ParagraphImpl(text));
  }

}
