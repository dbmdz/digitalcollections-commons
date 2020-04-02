package de.digitalcollections.commons.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
    basePackages = {
      "de.digitalcollections.commons.file.backend.impl",
      "de.digitalcollections.commons.file.business.impl"
    })
@EnableConfigurationProperties
public class SpringConfigCommonsFile {}
