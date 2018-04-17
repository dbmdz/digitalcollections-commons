package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.api.Content;
import de.digitalcollections.prosemirror.model.impl.DocumentImpl;
import java.util.List;

@JsonDeserialize(as = DocumentImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface DocumentMixIn {

  @JsonIgnore
  public List<Content> getContents();

}
