package de.digitalcollections.prosemirror.model.impl;

import de.digitalcollections.prosemirror.model.api.Attributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ContentBlockWithAttributesImpl extends ContentBlockImpl
    implements Attributes {

  Map<String, Object> attributes = null;

  @Override
  public void addAttribute(String key, Object value) {
    if (attributes == null) {
      attributes = new HashMap<>();
    }
    if (value != null && key != null) {
      attributes.put(key, value);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ContentBlockWithAttributesImpl that = (ContentBlockWithAttributesImpl) o;
    return Objects.equals(attributes, that.attributes);
  }

  @Override
  public Object getAttribute(String key) {
    if (attributes == null) {
      return null;
    }

    return attributes.get(key);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), attributes);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + "attributes=" + attributes + '}';
  }
}
