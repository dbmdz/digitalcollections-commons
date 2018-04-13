package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.Text;
import java.util.Objects;

public class TextImpl implements Text {

  private String text;

  public TextImpl() {
  }

  public TextImpl(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TextImpl text1 = (TextImpl) o;
    return Objects.equals(text, text1.text);
  }

  @Override
  public int hashCode() {

    return Objects.hash(text);
  }
}
