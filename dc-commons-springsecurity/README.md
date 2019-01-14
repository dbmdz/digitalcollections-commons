# Digital Collections Commons Spring Security

Offers simple username/password authentication for Spring Security 5 projects.

## Usage

Add the following artifact to your maven ```pom.xml```

```xml
<dependency>
   <groupId>de.digitalcollections.commons</groupId>
   <artifactId>dc-commons-springsecurity</artifactId>
   <version>{set to current version}</version>
</dependency>
```

### Authentication component

To use the authentication, extend your ComponentScan by the following base package:

```java
@ComponentScan(basePackages = {
    "de.digitalcollections.commons.springsecurity.service",
})
``` 

Now, you can autowire the ```PlainUsernamePasswordUserDetailsService```:

```java
@Autowired
PlainUsernamePasswordUserDetailsService userService;
```

The configuration works by a simple properties file, which consists of the username, the plain password
and the list of roles attached to the user:

```properties
user1=password1,ROLE_TEST1
user2=password2,ROLE_TEST1,ROLE_TEST2
```

The location of that properties file is defined in the spring property ```security.userproperties.location```.
It can either reside in the classpath or (preferred!) externalized on the filesystem:

An example for the ```application.yml``` for an externalized location is:
```yml
security:
    userproperties:
      location: /local/service/user.properties
```

### Path access security

To use the path access security alone, extend your ComponentScan by the following base package:

```java
@ComponentScan(basePackages = {
    "de.digitalcollections.commons.springsecurity.access",
})
``` 

Now, you can autowire the ```UnsecuredPaths``` bean, if you want to use it directly:

```java
@Autowired
UnsecuredPaths unsecuredPaths;
```

You can either use the default configuration, which marks a useful set of paths as unsecured, or you can set them
as a list in the property ```security.access.unsecured```

The default unsecured paths are:
```
/health
/info
/javamelody
/jolokia
/jolokia/**
/jsondoc
/monitoring**
/monitoring/health
/monitoring/jolokia
/monitoring/jolokia/**
/monitoring/prometheus
/monitoring/prometheus/**
/monitoring/version
/resources/**
/version
```

### Authenticated web access configuration

If you want to use the path access security without the need for writing an own HttpSecurity configuration, just enhance the
component scan by the following base packages:

```java
@ComponentScan(basePackages = {
    "de.digitalcollections.commons.springsecurity.access",
    "de.digitalcollections.commons.springsecurity.web"
})
``` 

Access to all unsecured paths (see above) is possible without authentication; for the rest, you need to authenticate against
the user properties from the authentication component (see above).

In your MVC controllers, you can now use the ```@PreAuthorize``` annotation, e.g.

```java
@PreAuthorize("hasRole('ADMIN') or hasRole('GOD')")
public void doSomeAdminStuff() throws Exception {
  ...
}
```