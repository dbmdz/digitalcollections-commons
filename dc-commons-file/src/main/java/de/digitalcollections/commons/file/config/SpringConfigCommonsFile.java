package de.digitalcollections.commons.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
  "de.digitalcollections.commons.file.backend.impl",
  "de.digitalcollections.commons.file.business.impl.service"
})
@EnableConfigurationProperties
//@PropertySource(value = {
//  "classpath:SpringConfigCommonsFile-${spring.profiles.active}.properties"
//})
public class SpringConfigCommonsFile {

//  @Bean
//  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//    return new PropertySourcesPlaceholderConfigurer();
//  }
}
