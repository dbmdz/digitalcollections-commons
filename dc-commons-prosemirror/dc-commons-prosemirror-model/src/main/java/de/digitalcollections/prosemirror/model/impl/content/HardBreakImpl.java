package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.HardBreak;

public class HardBreakImpl implements HardBreak {

  @Override
  public boolean equals(Object obj) {
    return ( obj instanceof HardBreak );
  }
}
