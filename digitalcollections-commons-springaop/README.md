# DigitalCollections Commons: Spring AOP

Several Spring AOP <https://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html> ready to use aspects.

## Usage

pom.xml:
```xml
<dependency>
  <groupId>de.digitalcollections.commons</groupId>
  <artifactId>digitalcollections-commons-springaop</artifactId>
  <version>1.2.2-SNAPSHOT</version>
</dependency>
```

## AbstractAopMethodLogger

Aspect oriented logging around methods:

- logs method call with method parameters (if not null), e.g.

```
14:16:15.960 [qtp267098351-23] INFO de.example.portal.frontend.webapp.controller.NewsController - viewNews(model=BindingAwareModelMap {menu=news, starsign=Stier, names_of_day=[Ljava.l...)
```

- logs duration (in ms) of method execution (only if logging is at minimum at debug level), e.g.

```
14:16:15.986 [qtp267098351-23] DEBUG de.example.portal.frontend.webapp.controller.NewsController - viewNews(): duration 26 ms
```

### Configuration

Extend AbstractAopMethodLogger with your project specific AopMethodLogger and define the method pointcuts to be logged. Example:

```java
package de.example.portal.frontend.webapp.aop;

import de.digitalcollections.commons.springaop.AbstractAopMethodLogger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopMethodLogger extends AbstractAopMethodLogger {

  @Pointcut("execution(public * de.example.portal.frontend.webapp.controller..*Controller.*(..)) || "
          + "execution(public * de.example.portal.business.service..*Service.*(..)) || "
          + "execution(public * de.example.portal.backend.repository..*.*Repository.*(..))")
  @Override
  public void methodsToBeLogged() {
  }
}
```

Add package containing your implementation to ComponentScan of your Spring config and add @EnableAspectJAutoProxy annotation. Example:

```java
@Configuration
@ComponentScan(basePackages = {
  "de.example.portal.frontend.webapp.aop",
  ...
})
@EnableAspectJAutoProxy
...
public class SpringConfigWeb extends WebMvcConfigurerAdapter {
  ...
}
```