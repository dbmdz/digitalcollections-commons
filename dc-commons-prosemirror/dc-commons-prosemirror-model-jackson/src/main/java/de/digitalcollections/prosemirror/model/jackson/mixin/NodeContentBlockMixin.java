package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.digitalcollections.prosemirror.model.api.ContentBlock;
import java.util.List;

public interface NodeContentBlockMixin {

  @JsonProperty("content")
  List<ContentBlock> getContentBlocks();

  @JsonProperty("content")
  void setContentBlocks(List<ContentBlock> contentBlocks);

  @JsonIgnore
  void addContentBlock(ContentBlock contentBlock);
}
