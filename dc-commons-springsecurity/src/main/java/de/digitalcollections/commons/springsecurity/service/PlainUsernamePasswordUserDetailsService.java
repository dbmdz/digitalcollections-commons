package de.digitalcollections.commons.springsecurity.service;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class PlainUsernamePasswordUserDetailsService implements UserDetailsService, InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlainUsernamePasswordUserDetailsService.class);

  @Value("${security.userproperties.location:classpath:/users.properties}")
  private String userpropertiesLocation;

  private InMemoryUserDetailsManager repository;

  @Override
  public void afterPropertiesSet() throws Exception {
    Resource resource = null;

    if ( userpropertiesLocation.startsWith("classpath")) {
      resource = new ClassPathResource(userpropertiesLocation.replaceFirst("classpath:", ""));
    } else {
      resource = new UrlResource(userpropertiesLocation);
    }

    Properties userProperties = PropertiesLoaderUtils.loadProperties(resource);

    Enumeration e = userProperties.propertyNames();
    while (e.hasMoreElements()) {
      String userName = (String) e.nextElement();
      String[] parts = ((String) userProperties.get(userName)).split(",");
      String password = "{noop}" + parts[0];
      String roles = Arrays.stream(parts, 1, parts.length).collect(Collectors.joining(","));
      userProperties.put(userName, password + "," + roles);
    }

    repository = new InMemoryUserDetailsManager(userProperties);

    LOGGER.info("Load users=" + userProperties.entrySet().stream().map(user -> user.getKey() + ":["
        + ((String)user.getValue()).replaceFirst(".*?,","") + "]").collect(Collectors.joining(", ")));
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository.loadUserByUsername(username);
  }
}
