package de.digitalcollections.prosemirror.model.impl;

import de.digitalcollections.prosemirror.model.api.Content;
import de.digitalcollections.prosemirror.model.api.Document;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DocumentImpl implements Document {

  Map<Locale, List<Content>> contentBlocks;

  public DocumentImpl() {}

  @Override
  public Map<Locale, List<Content>> getContentBlocks() {
    return contentBlocks;
  }

  @Override
  public void setContentBlocks(Map<Locale, List<Content>> contentBlocks) {
    this.contentBlocks = contentBlocks;
  }

  @Override
  public List<Content> getContents() {
    if ( contentBlocks == null || contentBlocks.isEmpty() ) {
      return null;
    }
    if ( contentBlocks.keySet().size() == 1) {
      return contentBlocks.get(contentBlocks.keySet().iterator().next());
    }

    throw new RuntimeException("Too many languages to choose from: " + contentBlocks.keySet().iterator());
  }
}
