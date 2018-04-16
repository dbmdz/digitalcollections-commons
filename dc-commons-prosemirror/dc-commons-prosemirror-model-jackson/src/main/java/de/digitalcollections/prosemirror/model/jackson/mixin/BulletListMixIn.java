package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.api.content.ListItem;
import de.digitalcollections.prosemirror.model.impl.content.BulletListImpl;
import java.util.List;

@JsonDeserialize(as = BulletListImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface BulletListMixIn {

  @JsonProperty("content")
  List<ListItem> getContents();

  @JsonProperty("content")
  void setContents(List<ListItem> contents);

  @JsonIgnore
  void addContent(ListItem content);

}
