package de.digitalcollections.prosemirror.model.impl;

import de.digitalcollections.prosemirror.model.api.ContentBlock;
import de.digitalcollections.prosemirror.model.api.Document;
import java.util.List;

public class DocumentImpl implements Document {

  List<ContentBlock> contentBlocks;

  public DocumentImpl() {
  }

  @Override
  public List<ContentBlock> getContentBlocks() {
    return contentBlocks;
  }

  @Override
  public void setContentBlocks(List<ContentBlock> contentBlocks) {
    this.contentBlocks = contentBlocks;
  }
}
