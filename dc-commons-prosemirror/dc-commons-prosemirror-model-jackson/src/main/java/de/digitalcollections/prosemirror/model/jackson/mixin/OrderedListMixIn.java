package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.api.content.ListItem;
import de.digitalcollections.prosemirror.model.impl.content.OrderedListImpl;
import java.util.List;
import java.util.Map;

@JsonDeserialize(as = OrderedListImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface OrderedListMixIn {

  @JsonProperty("attrs")
  Map<String, Object> getAttributes();

  @JsonProperty("attrs")
  void setAttributes(Map<String, Object> attributes);

  @JsonIgnore
  void addAttribute(String key, Object value);

  @JsonIgnore
  Object getAttribute(String key);

  @JsonProperty("content")
  List<ListItem> getContents();

  @JsonProperty("content")
  void setContents(List<ListItem> contents);

  @JsonIgnore
  void addContent(ListItem content);

}
