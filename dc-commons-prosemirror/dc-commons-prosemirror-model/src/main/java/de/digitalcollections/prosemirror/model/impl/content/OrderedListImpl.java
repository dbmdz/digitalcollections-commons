package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.OrderedList;

public class OrderedListImpl extends ContentWithAttributesImpl implements OrderedList {

  public OrderedListImpl() {}

  public OrderedListImpl(int order) {
    addAttribute("order", 1);
  }


}
