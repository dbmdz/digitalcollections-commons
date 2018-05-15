package de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.contentblocks.IFrameImpl;
import de.digitalcollections.prosemirror.model.jackson.mixin.AttributesMixIn;

@JsonDeserialize(as = IFrameImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IFrameMixIn extends AttributesMixIn {

}
