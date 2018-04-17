package de.digitalcollections.prosemirror.model.api;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface Document {

  /**
   * @return (multilingual) text content
   */
  Map<Locale, List<Content>> getContentBlocks();

  /**
   * @param contentBlocks the (multilingual) text content
   */
  void setContentBlocks(Map<Locale, List<Content>> contentBlocks);

  default void addContentBlocks(Locale locale, List<Content> contentBlocks) {
    if (getContentBlocks() == null) {
      setContentBlocks(new HashMap<>());
    }
    getContentBlocks().put(locale, contentBlocks);
  }
}
