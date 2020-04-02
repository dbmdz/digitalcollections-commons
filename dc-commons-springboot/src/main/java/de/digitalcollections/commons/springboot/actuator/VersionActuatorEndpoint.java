package de.digitalcollections.commons.springboot.actuator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.digitalcollections.commons.springboot.monitoring.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "version")
public class VersionActuatorEndpoint {

  @Autowired VersionInfo versionInfo;

  @ReadOperation
  public VersionResponse getVersion() {
    return new VersionResponse(
        versionInfo.getApplicationName(),
        versionInfo.getVersionInfo(),
        versionInfo.getBuildDetails());
  }

  @JsonInclude(Include.NON_NULL)
  @JsonPropertyOrder({"name", "version", "details"})
  public static class VersionResponse {

    @JsonProperty private String name;

    @JsonProperty private String version;

    @JsonProperty private String details;

    public VersionResponse(String name, String version, String details) {
      this.name = name;
      this.version = version;
      this.details = details;
    }
  }
}
