package de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.contentblocks.CodeBlockImpl;
import de.digitalcollections.prosemirror.model.jackson.mixin.NodeContentBlockMixin;

@JsonDeserialize(as = CodeBlockImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface CodeBlockMixIn extends NodeContentBlockMixin {}
