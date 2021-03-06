package de.digitalcollections.prosemirror.model.api;

import java.util.ArrayList;
import java.util.List;

public interface Document {

  List<ContentBlock> getContentBlocks();

  void setContentBlocks(List<ContentBlock> contentBlocks);

  default void addContentBlock(ContentBlock contentBlock) {
    if (getContentBlocks() == null) {
      setContentBlocks(new ArrayList<>());
    }
    getContentBlocks().add(contentBlock);
  }
}
