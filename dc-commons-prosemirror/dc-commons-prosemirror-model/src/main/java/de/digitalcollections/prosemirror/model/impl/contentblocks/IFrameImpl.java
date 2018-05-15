package de.digitalcollections.prosemirror.model.impl.contentblocks;

import de.digitalcollections.prosemirror.model.api.contentblocks.IFrame;
import de.digitalcollections.prosemirror.model.impl.ContentBlockWithAttributesImpl;

public class IFrameImpl extends ContentBlockWithAttributesImpl implements IFrame {

  public IFrameImpl() {
  }

  public IFrameImpl(String src, String width, String height) {
    super();
    addAttribute("src", src);
    addAttribute("width", width);
    addAttribute("height", height);
  }

}
