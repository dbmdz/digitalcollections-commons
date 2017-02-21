package de.digitalcollections.commons.springmvc.security.jwt;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JWTAuthenticationFilter extends GenericFilterBean {
  private TokenAuthenticationService service;

  public JWTAuthenticationFilter(String secret) {
    this.service = new TokenAuthenticationService(secret);
  }

  public JWTAuthenticationFilter(PrivateKey privateKey, PublicKey publicKey) {
    this.service = new TokenAuthenticationService(privateKey, publicKey);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    Authentication auth = service.getAuthentication((HttpServletRequest) request);
    SecurityContextHolder.getContext().setAuthentication(auth);
    chain.doFilter(request, response);
  }
}
