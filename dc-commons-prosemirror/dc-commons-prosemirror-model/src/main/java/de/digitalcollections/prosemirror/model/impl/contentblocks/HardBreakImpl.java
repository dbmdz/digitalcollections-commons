package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.HardBreak;
import de.digitalcollections.prosemirror.model.impl.ContentBlockImpl;

public class HardBreakImpl extends ContentBlockImpl implements HardBreak {

  @Override
  public int hashCode() {
    return -1;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof HardBreak);
  }

  @Override
  public String toString() {
    return "HardBreakImpl{hashCode=" + hashCode() + "}";
  }
}
