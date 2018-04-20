package de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.contentblocks.OrderedListImpl;
import de.digitalcollections.prosemirror.model.jackson.mixin.NodeContentBlockMixin;
import java.util.Map;

@JsonDeserialize(as = OrderedListImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface OrderedListMixIn extends NodeContentBlockMixin {

  @JsonProperty("attrs")
  Map<String, Object> getAttributes();

  @JsonProperty("attrs")
  void setAttributes(Map<String, Object> attributes);

  @JsonIgnore
  void addAttribute(String key, Object value);

  @JsonIgnore
  Object getAttribute(String key);

}
