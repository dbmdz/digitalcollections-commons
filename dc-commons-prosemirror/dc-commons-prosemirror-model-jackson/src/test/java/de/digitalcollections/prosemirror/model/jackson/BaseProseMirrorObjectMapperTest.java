package de.digitalcollections.prosemirror.model.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseProseMirrorObjectMapperTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseProseMirrorObjectMapperTest.class);

  public ObjectMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new ProseMirrorObjectMapper();
  }

  @SuppressWarnings("unchecked")
  public <T> void checkSerializeDeserialize(T objectIn) throws Exception {
    T objectOut = (T) serializeDeserialize(objectIn);

    try {
      Set<String> keys = BeanUtils.describe(objectIn).keySet();
      for (String key : keys) {
        if ("UUID".equals(key)) {
          BeanUtils.setProperty(objectIn, key, null);
          BeanUtils.setProperty(objectOut, key, null);
        } else {
          BeanUtils.setProperty(objectIn, key + ".UUID", null);
          BeanUtils.setProperty(objectOut, key + ".UUID", null);
        }
      }
    } catch (InvocationTargetException e) {
      LOGGER.warn(e.toString());
    }

    /*
     * try { Method methodGetUuid = objectIn.getClass().getMethod("getUUID"); UUID uuid = (UUID)
     * methodGetUuid.invoke(objectIn); Method methodSetUUid = objectOut.getClass().getMethod("setUUID", UUID.class);
     * methodSetUUid.invoke(objectOut, uuid); } catch (NoSuchMethodException ignore) { }
     */
    try {
      assertThat(objectOut).usingRecursiveComparison().isEqualTo(objectIn);
    } catch (Throwable e) {
      LOGGER.error("ERR: IN=" + dump(objectIn) + "\n    OUT=" + dump(objectOut) + "\n\nERROR=" + e.getClass() + "=" + e.getMessage());
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  private Object serializeDeserialize(Object o) throws JsonProcessingException, IOException {
    String serializedObject = mapper.writeValueAsString(o);
    Class valueType = o.getClass();
    Object deserializedObject = mapper.readValue(serializedObject, valueType);
    return deserializedObject;
  }

  private String dump(Object o) throws JsonProcessingException {
    return mapper.writeValueAsString(o);
  }

}
