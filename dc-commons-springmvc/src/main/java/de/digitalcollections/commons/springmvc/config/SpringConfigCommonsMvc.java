package de.digitalcollections.commons.springmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/** Commons Spring MVC configuration. */
@Configuration
public class SpringConfigCommonsMvc {

  @Bean(name = "CommonsClasspathThymeleafResolver")
  public ClassLoaderTemplateResolver commonsClassLoaderThymeleafTemplateResolver() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("/de/digitalcollections/commons/springmvc/thymeleaf/templates/");
    templateResolver.setSuffix(".html");
    templateResolver.setCheckExistence(true);
    templateResolver.setCharacterEncoding("UTF-8");
    templateResolver.setTemplateMode(TemplateMode.HTML);
    return templateResolver;
  }
}
