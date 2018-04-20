package de.digitalcollections.prosemirror.model.impl;

import de.digitalcollections.prosemirror.model.api.ContentBlock;
import de.digitalcollections.prosemirror.model.api.NodeContentBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class NodeContentBlockImpl extends ContentBlockImpl implements NodeContentBlock {

  protected List<ContentBlock> contentBlocks;

  @Override
  public List<ContentBlock> getContentBlocks() {
    return contentBlocks;
  }

  @Override
  public void setContentBlocks(List<ContentBlock> contentBlocks) {
    this.contentBlocks = contentBlocks;
  }

  @Override
  public void addContentBlock(ContentBlock contentBlock) {
    if (contentBlocks == null) {
      contentBlocks = new ArrayList<>();
    }

    contentBlocks.add(contentBlock);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NodeContentBlockImpl)) {
      return false;
    }
    NodeContentBlockImpl that = (NodeContentBlockImpl) o;
    return Objects.equals(contentBlocks, that.contentBlocks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentBlocks);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{"
            + "contentBlocks=" + contentBlocks + ", "
            + "hashCode=" + hashCode()
            + '}';
  }
}
