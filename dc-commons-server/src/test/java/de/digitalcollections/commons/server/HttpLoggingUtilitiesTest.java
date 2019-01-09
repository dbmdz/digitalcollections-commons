package de.digitalcollections.commons.server;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpLoggingUtilitiesTest {

  @Test
  public void testAnonymizeIp() {
    assertThat(HttpLoggingUtilities.anonymizeIp("192.168.0.1")).isEqualTo("192.168");
    assertThat(HttpLoggingUtilities.anonymizeIp("255.255.255.255")).isEqualTo("255.255");
  }

  /**
   * Test of isValidPublicIp method, of class HttpLoggingUtilities.
   */
  @Test
  public void testIsValidPublicIp() {
    assertThat(HttpLoggingUtilities.isValidPublicIp("192.168.0.1")).isEqualTo(false);
  }
}
