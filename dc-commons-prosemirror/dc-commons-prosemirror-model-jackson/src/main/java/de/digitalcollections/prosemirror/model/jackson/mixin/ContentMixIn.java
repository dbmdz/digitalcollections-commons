package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.digitalcollections.prosemirror.model.impl.content.HardBreakImpl;
import de.digitalcollections.prosemirror.model.impl.content.HeadingImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = HardBreakImpl.class, name = "hard_break"),
    @JsonSubTypes.Type(value = HeadingImpl.class, name = "heading"),
    @JsonSubTypes.Type(value = TextImpl.class, name = "text")
})
public interface ContentMixIn {

}
