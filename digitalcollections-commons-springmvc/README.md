# DigitalCollections: Commons Spring MVC

<!-- toc -->

Spring MVC related library.

## Usage

Add dependency to pom.xml:

```xml
<dependency>
  <groupId>de.digitalcollections.commons</groupId>
  <artifactId>digitalcollections-commons-springmvc</artifactId>
  <version>1.2.2-SNAPSHOT</version>
</dependency>
```

Add message sources of this library ("messages-commons_....properties") to your messageSource-Bean:

```java
@Configuration
public class SpringConfig implements EnvironmentAware {
  ...

  @Bean(name = "messageSource")
  public MessageSource configureMessageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames("classpath:messages", "classpath:messages-overlay", "classpath:messages-commons");
    messageSource.setCacheSeconds(5);
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
```

## Global Exception Handling

Spring MVC provides the functionality of central exception handling (see <https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-error-handling>).

This library provides a preconfigured "GlobalExceptionController" as central location for handling all exceptions (usingthe ControllerAdvice functionality) in the presentation (Spring MVC) layer.

It can handle 404 errors when catching a ResourceNotFoundException (also included in this library) or all other exceptions (handled as 500 error): when catching an exception, the view template identified by "error" is returned as view with error code and timestamp as model.

Additionally it adds and shows the stacktrace if spring.profile.active is not "PROD" (production) to make debugging more comfortable.

### Configuration

To add global exception controller (using view "error") make sure the global exception controller is detected.
Add ComponentScan to your Spring configuration:

```java
@ComponentScan(basePackages = {
  "de.digitalcollections.commons.springmvc.controller"
})
```

In case you want to use provided Thymeleaf error templates, make sure there is a template called "base" that provides the skeleton around the inner content of pages (error template "decorates" base template).

Then follow this steps:

1. import spring configuration SpringConfigCommonsMvc into your spring config:

```java
@Configuration
...
@Import(SpringConfigCommonsMvc.class)
public class SpringConfigWeb extends WebMvcConfigurerAdapter {
  ...
}
```

2. add error template resolver (commonsClasspathThymeleafResolver, before your standard resolver) to your config:

```java
@Autowired
@Qualifier("CommonsClasspathThymeleafResolver")
private ClassLoaderTemplateResolver commonsClasspathThymeleafResolver;

...

@Bean
public SpringTemplateEngine templateEngine(ServletContextTemplateResolver servletContextTemplateResolver) {
  SpringTemplateEngine templateEngine = new SpringTemplateEngine();
  commonsClasspathThymeleafResolver.setOrder(1);
  servletContextTemplateResolver.setOrder(2);
  templateEngine.addTemplateResolver(commonsClasspathThymeleafResolver);
...
```

That's it! Now no Exception-Stacktrace should be shown without the design and page-skeleton of your webapp.

## Global Error Handling

After setting up a global exception handling with a "error"-view template, we reuse this error page for global error handling.
An error is defined as HTTP-error which is not caused by an exception in a mapped request, but e.g. if a request can not be mapped as a webapp request (404).

Follow the above configuration steps for global exception handling, what puts the resolving and error page in place and also the ComponentScan that detects the ErrorController.

Now we just have to tell the webapp to forward specific errors to the ErrorController path ("/error/...").
This still (Servlet specification 3.0, see also <http://stackoverflow.com/questions/10813993/using-spring-mvc-3-1-webapplicationinitializer-to-programmatically-configure-se>) has to be done in the "/WEB-INF/web.xml" file:

File "src/main/webapp/WEB-INF/web.xml":

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

  <error-page>
    <error-code>401</error-code>
    <location>/error/401</location>
  </error-page>

  <error-page>
    <error-code>404</error-code>
    <location>/error/404</location>
  </error-page>

  <error-page>
    <error-code>500</error-code>
    <location>/error/500</location>
  </error-page>
</web-app>
```

That's it! The specified HTTP-errors are now forwarded to the error-page surrounded by the webapp-specific design showing timestamp and error code.

## Frontend specific Exceptions

There are usse cases where exceptions in the service layer occur. These service-specific exceptions
must be catched and translated into frontend specific exceptions.

The package "de.digitalcollections.commons.springmvc.exceptions" contains common frontend exceptions you can use.

- ResourceNotFoundException: used when service can not deliver some resource because it can not be found. It is translated to HTTP-Status 404 (Not Found).