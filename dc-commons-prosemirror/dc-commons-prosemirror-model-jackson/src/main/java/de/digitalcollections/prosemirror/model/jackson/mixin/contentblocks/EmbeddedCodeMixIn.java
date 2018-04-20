package de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.contentblocks.EmbeddedCodeImpl;
import de.digitalcollections.prosemirror.model.jackson.mixin.NodeContentBlockMixin;

@JsonDeserialize(as = EmbeddedCodeImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface EmbeddedCodeMixIn extends NodeContentBlockMixin {

}
