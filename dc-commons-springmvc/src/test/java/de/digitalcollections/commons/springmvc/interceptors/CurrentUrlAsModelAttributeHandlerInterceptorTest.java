package de.digitalcollections.commons.springmvc.interceptors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CurrentUrlAsModelAttributeHandlerInterceptorTest {

  CurrentUrlAsModelAttributeHandlerInterceptor instance = new CurrentUrlAsModelAttributeHandlerInterceptor();

  @Test
  public void testDeleteLanguageParam() {
    System.out.println("deleteLanguageParam");

    String currentUrl = "http://example.org?language=de";
    String expResult = "http://example.org";
    String result = instance.deleteParam("language", "de", currentUrl);
    assertEquals(expResult, result);

    currentUrl = "http://example.org?foo=bar&language=en";
    expResult = "http://example.org?foo=bar";
    result = instance.deleteParam("language", "en", currentUrl);
    assertEquals(expResult, result);

    currentUrl = "http://example.org?language=en&foo=bar";
    expResult = "http://example.org?foo=bar";
    result = instance.deleteParam("language", "en", currentUrl);
    assertEquals(expResult, result);
  }
}
