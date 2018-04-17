package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.digitalcollections.prosemirror.model.impl.DocumentImpl;

@JsonDeserialize(as = DocumentImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface DocumentMixIn {

}
