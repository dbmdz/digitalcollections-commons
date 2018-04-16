package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.digitalcollections.prosemirror.model.api.Content;
import de.digitalcollections.prosemirror.model.impl.content.BulletListImpl;
import de.digitalcollections.prosemirror.model.impl.content.ContentImpl;
import de.digitalcollections.prosemirror.model.impl.content.EmbeddedCodeBlockImpl;
import de.digitalcollections.prosemirror.model.impl.content.HardBreakImpl;
import de.digitalcollections.prosemirror.model.impl.content.HeadingImpl;
import de.digitalcollections.prosemirror.model.impl.content.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.content.OrderedListImpl;
import de.digitalcollections.prosemirror.model.impl.content.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BulletListImpl.class, name = "bullet_list"),
    @JsonSubTypes.Type(value = ContentImpl.class, name = "content"),
    @JsonSubTypes.Type(value = EmbeddedCodeBlockImpl.class, name = "embedded_code_block"),
    @JsonSubTypes.Type(value = HardBreakImpl.class, name = "hard_break"),
    @JsonSubTypes.Type(value = HeadingImpl.class, name = "heading"),
    @JsonSubTypes.Type(value = ListItemImpl.class, name = "list_item"),
    @JsonSubTypes.Type(value = OrderedListImpl.class, name = "ordered_list"),
    @JsonSubTypes.Type(value = ParagraphImpl.class, name = "paragraph"),
    @JsonSubTypes.Type(value = TextImpl.class, name = "text")
})
public interface ContentMixIn {

  @JsonProperty("content")
  List<Content> getContents();

  @JsonProperty("content")
  void setContents(List<Content> contents);

  @JsonIgnore
  void addContent(Content content);

}
