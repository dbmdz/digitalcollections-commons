package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.MarkImpl;

@JsonDeserialize(as = MarkImpl.class)
public interface MarkMixIn extends AttributesMixIn {}
