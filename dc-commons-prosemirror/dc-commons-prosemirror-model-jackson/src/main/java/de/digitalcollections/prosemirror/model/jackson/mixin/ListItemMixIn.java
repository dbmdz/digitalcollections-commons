package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.api.Content;
import de.digitalcollections.prosemirror.model.impl.content.ListItemImpl;
import java.util.List;

@JsonDeserialize(as = ListItemImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ListItemMixIn {

  @JsonProperty("content")
  List<Content> getContents();

  @JsonProperty("content")
  void setContents(List<Content> contents);

  @JsonIgnore
  void addContent(Content content);

}
