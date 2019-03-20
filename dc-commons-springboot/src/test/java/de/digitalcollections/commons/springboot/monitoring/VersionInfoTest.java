package de.digitalcollections.commons.springboot.monitoring;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = VersionInfo.class)
@SpringBootConfiguration()
public class VersionInfoTest {

  private static final String JUNIT_VERSION = "5.4.1";

  @Autowired
  VersionInfo versionInfo;

  @Test
  @DisplayName("Testing application info from application.yml")
  void testApplicationInfoFromApplicationYml() {
    assertThat(versionInfo.getApplicationName()).isEqualTo("dc-commons-springboot-example");
    assertThat(versionInfo.getVersionInfo()).isEqualTo("1.2.3");
    assertThat(versionInfo.getBuildDetails()).isEqualTo("build by foo@bar.com");
  }

  @Test
  @DisplayName("Testing dependency detail info")
  void testBuildDetails() {
    Map<String, String> versions = versionInfo.getArtifactVersions();

    assertThat(versions.get("junit-jupiter-" + JUNIT_VERSION + ".jar")).isEqualTo(JUNIT_VERSION);
  }
}
