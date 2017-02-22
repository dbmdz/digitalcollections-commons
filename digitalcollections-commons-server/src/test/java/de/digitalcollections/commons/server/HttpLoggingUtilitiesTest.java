package de.digitalcollections.commons.server;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpLoggingUtilitiesTest {

  public HttpLoggingUtilitiesTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
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
