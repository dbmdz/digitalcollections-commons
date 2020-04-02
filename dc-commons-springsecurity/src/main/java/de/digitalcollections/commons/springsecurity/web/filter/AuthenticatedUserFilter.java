package de.digitalcollections.commons.springsecurity.web.filter;

import de.digitalcollections.commons.springsecurity.access.UnsecuredPaths;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AuthenticatedUserFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatedUserFilter.class);

  private final UnsecuredPaths unsecuredPaths;

  @Autowired
  public AuthenticatedUserFilter(UnsecuredPaths unsecuredPaths) {
    this.unsecuredPaths = unsecuredPaths;
  }

  @Override
  public void init(FilterConfig fc) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Found principal=" + principal);
      }
      if (principal instanceof User) {
        request.setAttribute("username", ((User) principal).getUsername());
      }
    } else if (request instanceof HttpServletRequest) {
      String uri = ((HttpServletRequest) request).getRequestURI();
      if (!unsecuredPaths.getUnsecuredPaths().contains(uri)) {
        LOGGER.warn("Unauthorized request detected to " + uri);
      }
    } else {
      LOGGER.warn("Unauthorized request detected !");
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {}
}
