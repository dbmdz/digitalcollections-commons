package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.api.content.Content;
import de.digitalcollections.prosemirror.model.impl.ParagraphImpl;
import java.util.List;

@JsonDeserialize(as = ParagraphImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ParagraphMixIn {

  @JsonProperty("content")
  List<Content> getContents();

  @JsonProperty("content")
  void setContents(List<Content> contents);

  @JsonIgnore
  void addContent(Content content);

}
