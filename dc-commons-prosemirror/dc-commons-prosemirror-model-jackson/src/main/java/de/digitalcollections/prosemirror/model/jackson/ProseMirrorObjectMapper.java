package de.digitalcollections.prosemirror.model.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ProseMirrorObjectMapper extends ObjectMapper {

  public ProseMirrorObjectMapper() {
    super();
    customize(this);
  }

  public static ObjectMapper customize(ObjectMapper objectMapper) {
    objectMapper.registerModule(new ProseMirrorModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}
