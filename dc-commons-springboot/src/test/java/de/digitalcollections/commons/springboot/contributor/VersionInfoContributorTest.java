package de.digitalcollections.commons.springboot.contributor;

import de.digitalcollections.commons.springboot.monitoring.VersionInfo;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {VersionInfo.class, VersionInfoContributor.class})
@SpringBootConfiguration()
public class VersionInfoContributorTest {

  @Autowired
  VersionInfoContributor versionInfoContributor;

  @Test
  @DisplayName("Test for contribution return values to contain dependency version information")
  void testContribute() {
    Builder builder = new Builder();
    versionInfoContributor.contribute(builder);

    Map<String, Object> infoValues = builder.build().getDetails();
    @SuppressWarnings("unchecked")
    Map<String, String> versionInfoValues = (Map) infoValues.get("version");

    assertThat(versionInfoValues.get("junit-jupiter-5.4.1.jar")).isEqualTo("5.4.1");
  }
}