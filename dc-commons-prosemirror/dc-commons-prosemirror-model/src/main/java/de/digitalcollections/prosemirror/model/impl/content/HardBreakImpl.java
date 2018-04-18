package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.HardBreak;

public class HardBreakImpl extends ContentImpl implements HardBreak {

  @Override
  public int hashCode() {
    return -1;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof HardBreak );
  }

  @Override
  public String toString() {
    return "HardBreakImpl{hashCode=" + hashCode() + "}";
  }
}
