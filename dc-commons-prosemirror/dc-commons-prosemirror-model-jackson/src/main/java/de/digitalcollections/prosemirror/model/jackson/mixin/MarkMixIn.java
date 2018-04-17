package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.content.MarkImpl;
import java.util.Map;

@JsonDeserialize(as = MarkImpl.class)
public interface MarkMixIn {

  @JsonProperty("attrs")
  Map<String, Object> getAttributes();

  @JsonProperty("attrs")
  void setAttributes(Map<String, Object> attributes);

  @JsonIgnore
  void addAttribute(String key, Object value);

  @JsonIgnore
  Object getAttribute(String key);

}
