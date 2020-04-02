package de.digitalcollections.commons.springsecurity.web.access;

import de.digitalcollections.commons.springsecurity.access.UnsecuredPaths;
import de.digitalcollections.commons.springsecurity.service.PlainUsernamePasswordUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthenticatedWebSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AuthenticatedWebSecurityConfig.class);

  @Autowired PlainUsernamePasswordUserDetailsService userDetailsService;

  @Autowired private UnsecuredPaths unsecuredPaths;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/**").csrf().disable();

    http.authorizeRequests()
        .antMatchers(
            unsecuredPaths
                .getUnsecuredPaths()
                .toArray(new String[unsecuredPaths.getUnsecuredPaths().size()]))
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .userDetailsService(userDetailsService)
        .httpBasic()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable()
        .headers()
        .disable();
  }
}
