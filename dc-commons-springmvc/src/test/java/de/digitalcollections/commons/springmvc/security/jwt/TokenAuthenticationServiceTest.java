package de.digitalcollections.commons.springmvc.security.jwt;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenAuthenticationServiceTest {

  private static final char[] KEYSTORE_PASSWORD = "testpassword".toCharArray();

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
  public void testTimedOutAuthentication() throws InterruptedException {
    TokenAuthenticationService service = new TokenAuthenticationService("this.is.a.test.secret", 50);
    String token = obtainToken(service);
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(anyString())).thenReturn(token);
    Thread.sleep(300);
    Authentication auth = service.getAuthentication(mockRequest);
    assertThat(auth).isNull();
  }

  @Test
  public void testKeypairAuthentication() throws Exception {
    InputStream is = getClass().getClassLoader().getResourceAsStream("test-keystore.jks");
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(is, KEYSTORE_PASSWORD);
    PrivateKey privateKey = (PrivateKey) keyStore.getKey("jwtkey", KEYSTORE_PASSWORD);
    PublicKey publicKey = keyStore.getCertificate("jwtkey").getPublicKey();

    TokenAuthenticationService issuingService = new TokenAuthenticationService(privateKey, publicKey);
    TokenAuthenticationService authenticatingService = new TokenAuthenticationService(publicKey);

    assertThat(issuingService.canIssueTokens()).isTrue();
    assertThat(authenticatingService.canIssueTokens()).isFalse();

    String token = obtainToken(issuingService);
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(anyString())).thenReturn(token);
    Authentication auth = authenticatingService.getAuthentication(mockRequest);
    assertThat(auth).isNotNull();
  }
}
