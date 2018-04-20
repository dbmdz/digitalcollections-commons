package de.digitalcollections.prosemirror.model.jackson.mixin.contentblocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.contentblocks.BulletListImpl;
import de.digitalcollections.prosemirror.model.jackson.mixin.NodeContentBlockMixin;

@JsonDeserialize(as = BulletListImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface BulletListMixIn extends NodeContentBlockMixin {

}
