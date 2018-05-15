package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public interface AttributesMixIn {

  @JsonProperty("attrs")
  Map<String, Object> getAttributes();

  @JsonProperty("attrs")
  void setAttributes(Map<String, Object> attributes);

  @JsonIgnore
  void addAttribute(String key, Object value);

  @JsonIgnore
  Object getAttribute(String key);
}
