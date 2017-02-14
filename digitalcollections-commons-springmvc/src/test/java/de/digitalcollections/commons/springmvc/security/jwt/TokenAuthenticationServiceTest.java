package de.digitalcollections.commons.springmvc.security.jwt;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.core.Authentication;

public class TokenAuthenticationServiceTest {

  private static String obtainToken(TokenAuthenticationService service) {
    HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
    ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
    service.addAuthentication(mockResponse, "admin");
    verify(mockResponse).addHeader(anyString(), tokenCaptor.capture());
    return tokenCaptor.getValue().split(" ")[1];
  }

  @Test
  public void testAuthentication() throws IOException {
    TokenAuthenticationService service = new TokenAuthenticationService("this.is.a.test.secret");
    String token = obtainToken(service);
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(anyString())).thenReturn(token);
    Authentication auth = service.getAuthentication(mockRequest);
    assertThat(auth).isNotNull();
  }

  @Test
  public void testTimedOutAuthentication() {
    TokenAuthenticationService service = new TokenAuthenticationService("this.is.a.test.secret", 50);
    String token = obtainToken(service);
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(anyString())).thenReturn(token);
    Authentication auth = service.getAuthentication(mockRequest);
    assertThat(auth).isNull();
  }
}