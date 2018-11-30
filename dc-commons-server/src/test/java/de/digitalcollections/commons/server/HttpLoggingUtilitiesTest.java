package de.digitalcollections.commons.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpLoggingUtilitiesTest {
  @Test
  public void testAnonymizeIp() {
    assertEquals(HttpLoggingUtilities.anonymizeIp("192.168.0.1"), "192.168");
    assertEquals(HttpLoggingUtilities.anonymizeIp("255.255.255.255"), "255.255");
  }

  /**
   * Test of isValidPublicIp method, of class HttpLoggingUtilities.
   */
  @Test
  public void testIsValidPublicIp() {
    System.out.println("isValidPublicIp");
    String ip = "192.168.0.1";
    boolean expResult = false;
    boolean result = HttpLoggingUtilities.isValidPublicIp(ip);
    assertEquals(expResult, result);
  }
}
