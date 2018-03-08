package de.digitalcollections.commons.springboot.actuator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.commons.springboot.monitoring.VersionInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {VersionInfo.class, VersionActuatorEndpoint.class})
@SpringBootConfiguration()
public class VersionActuatorEndpointTest {

  @Autowired
  VersionActuatorEndpoint versionActuatorEndpoint;

  @Test
  @DisplayName("Test for return value of the /version endpoint")
  public void testGetVersion() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    String jsonResult = mapper.writeValueAsString(versionActuatorEndpoint.getVersion());

    assertThat(jsonResult).isEqualTo("{\"name\":\"dc-commons-springboot-example\",\"version\":\"1.2.3\",\"details\":\"build by foo@bar.com\"}");
  }
}