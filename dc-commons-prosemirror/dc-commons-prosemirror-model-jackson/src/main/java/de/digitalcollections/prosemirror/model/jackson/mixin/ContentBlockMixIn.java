package de.digitalcollections.prosemirror.model.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.digitalcollections.prosemirror.model.api.ContentBlock;
import de.digitalcollections.prosemirror.model.impl.contentblocks.BulletListImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.EmbeddedCodeImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.HardBreakImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.HeadingImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ListItemImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.OrderedListImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.ParagraphImpl;
import de.digitalcollections.prosemirror.model.impl.contentblocks.TextImpl;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BulletListImpl.class, name = "bullet_list"),
  @JsonSubTypes.Type(value = EmbeddedCodeImpl.class, name = "embedded_code"),
  @JsonSubTypes.Type(value = HardBreakImpl.class, name = "hard_break"),
  @JsonSubTypes.Type(value = HeadingImpl.class, name = "heading"),
  @JsonSubTypes.Type(value = ListItemImpl.class, name = "list_item"),
  @JsonSubTypes.Type(value = OrderedListImpl.class, name = "ordered_list"),
  @JsonSubTypes.Type(value = ParagraphImpl.class, name = "paragraph"),
  @JsonSubTypes.Type(value = TextImpl.class, name = "text")
})
public interface ContentBlockMixIn {

  @JsonProperty("content")
  List<ContentBlock> getContents();

  @JsonProperty("content")
  void setContents(List<ContentBlock> contents);

  @JsonIgnore
  void addContent(ContentBlock content);

}
