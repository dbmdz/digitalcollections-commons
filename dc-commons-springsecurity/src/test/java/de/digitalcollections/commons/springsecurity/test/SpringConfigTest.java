package de.digitalcollections.commons.springsecurity.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
    basePackages = {
      "de.digitalcollections.commons.springsecurity.access",
      "de.digitalcollections.commons.springsecurity.service",
      "de.digitalcollections.commons.springsecurity.web"
    })
public class SpringConfigTest {}
