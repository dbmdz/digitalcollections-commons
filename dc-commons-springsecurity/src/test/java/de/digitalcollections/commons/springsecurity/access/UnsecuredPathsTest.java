package de.digitalcollections.commons.springsecurity.access;

import de.digitalcollections.commons.springsecurity.test.SpringConfigTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
@SpringBootTest(classes = {SpringConfigTest.class, UnsecuredPaths.class, UnsecuredPathsConfig.class})
@TestPropertySource(locations = "classpath:/application.yml")
@ActiveProfiles("pathstest")
class UnsecuredPathsTest {

  @Autowired
  UnsecuredPaths unsecuredPaths;

  @Test
  public void testConfiguredPaths() {
    assertThat(unsecuredPaths.getUnsecuredPaths()).containsExactly("/foo", "/bar", "/baz");
  }

}