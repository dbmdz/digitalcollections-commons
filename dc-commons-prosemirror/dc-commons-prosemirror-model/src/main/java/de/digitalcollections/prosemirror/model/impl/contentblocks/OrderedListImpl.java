package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.OrderedList;
import de.digitalcollections.prosemirror.model.impl.ContentBlockWithAttributesImpl;

public class OrderedListImpl extends ContentBlockWithAttributesImpl implements OrderedList {

  public OrderedListImpl() {
  }

  public OrderedListImpl(int order) {
    addAttribute("order", 1);
  }

}
