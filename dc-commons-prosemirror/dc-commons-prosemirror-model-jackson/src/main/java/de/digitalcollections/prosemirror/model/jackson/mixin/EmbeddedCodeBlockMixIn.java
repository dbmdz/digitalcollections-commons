package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.api.Content;
import de.digitalcollections.prosemirror.model.impl.content.EmbeddedCodeBlockImpl;
import java.util.List;

@JsonDeserialize(as = EmbeddedCodeBlockImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface EmbeddedCodeBlockMixIn {

  @JsonProperty("content")
  List<Content> getContents();

  @JsonProperty("content")
  void setContents(List<Content> contents);

  @JsonIgnore
  void addContent(Content content);

}
