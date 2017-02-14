package de.digitalcollections.commons.springmvc.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public class TokenAuthenticationService {
  private final String TOKEN_PREFIX = "Bearer";
  private final String HEADER_KEY = "Authorization";

  private String secret;
  private long expirationTime = 1000 * 60 * 60 * 24 * 7;  // By default a week;

  public TokenAuthenticationService(String secret) {
    this.secret = secret;
  }

  public TokenAuthenticationService(String secret, long expirationTime) {
    this.secret = secret;
    this.expirationTime = expirationTime;
  }

  public void addAuthentication(HttpServletResponse response, String username) {
    String token = Jwts.builder()
        .setSubject(username)
        .setExpiration(Date.from(Instant.now().plusMillis(expirationTime)))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
    response.addHeader(HEADER_KEY, TOKEN_PREFIX + " " + token);
  }

  public Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_KEY);
    if (token == null) {
      return null;
    }
    String username = null;
    try {
      username = Jwts.parser()
          .setSigningKey(secret)
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
