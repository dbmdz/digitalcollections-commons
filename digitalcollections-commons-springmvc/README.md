# DigitalCollections: Commons Spring MVC

## Global Exception Handling

Spring MVC provides the functionality of central exception handling (see <https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-error-handling>).

This library provides a preconfigured "GlobalExceptionController" as central location for handling all exceptions in the presentation (Spring MVC) layer.

It can handle 404 errors when catching a ResourceNotFoundException (also included in this library) or all other exceptions (handled as 500 error): when catching an exception, the view template identified by "error" is returned as view with error code and timestamp as model.

Additionally it adds and shows the stacktrace if spring.profile.active is not "PROD" (production) to make debugging more comfortable.

### Configuration

Add dependency to pom.xml:

```xml
<dependency>
  <groupId>de.digitalcollections.commons</groupId>
  <artifactId>digitalcollections-commons-springmvc</artifactId>
  <version>1.2.0</version>
</dependency>
```

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