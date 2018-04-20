package de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.jackson.mixin.NodeContentBlockMixin;

@JsonDeserialize(as = ParagraphImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ParagraphMixIn extends NodeContentBlockMixin {

}
