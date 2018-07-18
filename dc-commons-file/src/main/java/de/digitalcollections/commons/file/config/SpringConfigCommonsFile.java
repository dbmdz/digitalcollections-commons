package de.digitalcollections.commons.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = {
  "de.digitalcollections.commons.file.backend.impl",
  "de.digitalcollections.commons.file.business.impl.service"
})
@PropertySource(value = {
  "classpath:SpringConfigCommonsFile-${spring.profiles.active}.properties"
})
public class SpringConfigCommonsFile {

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

}
