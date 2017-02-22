package de.digitalcollections.commons.springmvc.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public class TokenAuthenticationService {

  private final String TOKEN_PREFIX = "Bearer";
  private final String HEADER_KEY = "Authorization";

  private String secret;
  private PrivateKey privateKey;
  private PublicKey publicKey;

  private long expirationTime = 1000 * 60 * 60 * 24 * 7;  // By default a week

  /**
   * Configure the service with a string secret.
   *
   * @param secret a secret
   */
  public TokenAuthenticationService(String secret) {
    this.secret = secret;
  }

  /**
   * Configure the service with a public/private key pair. The pair must have been generated with the RSA cipher, e.g.
   * with `keytool`: $ keytool -keyalg RSA -keystore my-keystore.jks -genkeypair
   *
   * @param privateKey the private key
   * @param publicKey the public key
   */
  public TokenAuthenticationService(PrivateKey privateKey, PublicKey publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  /**
   * Configure the service with a public key. This has the effect that the service can no longer issue tokens, but only
   * verify them. Can be useful in scenarios where a single entity is issuing tokens and services that wish to
   * authenticate users do not have access to the secret key.
   *
   * @param publicKey the public key
   */
  public TokenAuthenticationService(PublicKey publicKey) {
    this.publicKey = publicKey;
  }

  public TokenAuthenticationService(String secret, long expirationTime) {
    this.secret = secret;
    this.expirationTime = expirationTime;
  }

  public boolean canIssueTokens() {
    return (privateKey != null && privateKey.getAlgorithm().equals("RSA")) || (secret != null && !secret.isEmpty());
  }

  public void addAuthentication(HttpServletResponse response, String username) {
    if (privateKey != null && !privateKey.getAlgorithm().equals("RSA")) {
      throw new RuntimeException(String.
              format("Private Key must use RSA cipher, but uses %s", privateKey.getAlgorithm()));
    }
    if ((secret == null || secret.isEmpty()) && privateKey == null) {
      throw new RuntimeException("Cannot issue tokens due to missing secret or private key.");
    }
    JwtBuilder builder = Jwts.builder()
            .setSubject(username)
            .setExpiration(Date.from(Instant.now().plusMillis(expirationTime)));
    if (privateKey != null) {
      builder.signWith(SignatureAlgorithm.RS512, privateKey);
    } else {
      builder.signWith(SignatureAlgorithm.HS512, secret);
    }
    String token = builder.compact();
    response.addHeader(HEADER_KEY, TOKEN_PREFIX + " " + token);
  }

  public Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_KEY);
    if (token == null) {
      return null;
    }
    String username = null;
    try {
      JwtParser parser = Jwts.parser();
      if (publicKey != null) {
        parser.setSigningKey(publicKey);
      } else {
        parser.setSigningKey(secret);
      }
      username = parser
              .parseClaimsJws(token)
              .getBody()
              .getSubject();
    } catch (ExpiredJwtException ignored) {
    }
    if (username != null) {
      return new AuthenticatedUser(username);
    } else {
      return null;
    }
  }
}
