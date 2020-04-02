package de.digitalcollections.commons.springsecurity.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
  private TokenAuthenticationService tokenAuthenticationService;

  public JWTLoginFilter(
      String route,
      AuthenticationManager authenticationManager,
      TokenAuthenticationService service) {
    super(new AntPathRequestMatcher(route));
    setAuthenticationManager(authenticationManager);
    tokenAuthenticationService = service;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(request.getInputStream());
    String username = root.get("username").asText();
    String password = root.get("password").asText();
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(username, password);
    return getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException, ServletException {
    String name = authResult.getName();
    tokenAuthenticationService.addAuthentication(response, name);
  }
}
