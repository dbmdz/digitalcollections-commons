package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.Mark;
import java.util.Objects;

public class MarkImpl implements Mark {

  String type;

  public MarkImpl() {}

  public MarkImpl(String type ) {
    this.type = type;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MarkImpl)) {
      return false;
    }
    MarkImpl mark = (MarkImpl) o;
    return Objects.equals(type, mark.type);
  }

  @Override
  public int hashCode() {

    return Objects.hash(type);
  }

  @Override
  public String toString() {
    return "MarkImpl{"
        + "type='" + type + '\''
        + '}';
  }
}
