# DigitalCollections: Commons File

This library ("DC Commons File") contains Services for reading files from configurable paths.

Originally it has been developed for usage with Spring Framework. Thus it has `@Service` and `@Repository` annotated classes being well known to Spring developers. It has been extended for an usage outside a Spring environment, too.

With this library you are able to read files via different protocols (as part of URIs):

- `file://`
- `classpath://`
- `http(s)://`

are supported.

Files (`FileResource`s) are usually stored following a systematic pattern.

If you have a look at the interface `FileResource` you will see, that it has properties of type `MimeType`, `Identifier`s and `UUID`. Based on this informations (and your project specific logic) you can select/create an unique identifier of a `FileResource` to be handed over to the `FileResourceService` to get access to the `FileResource`'s data.

By default the `IdentifierPatternToFileResourceUriResolverImpl` resolver is used to lookup the access URI using the given unique identifier. It can be configured specifiying a list of identifier-regex-patterns to URI/filepath-patterns (`substitutions`) with dynamic parts filled by using regular expression matching groups. The configuration is bound to the application property / environment variable `resourceRepository.resolved.patterns`. It is possible to configure multiple URIs for one identifier-regex-pattern.

The sequence of the substitution-entries is also considered in two ways:

- mimetype matching: The first matching mimetype from top down is selected
- existing check: If file resource of first selected match (uri) does not exist, the next matching uri is tested. Finally the first uri matching and existing is returned for the given identifier

Example configuration (via a Spring Boot `application.yml`):

```yml
resourceRepository:
  resolved:
    patterns:
      # resolving based on a given FileResource-UUID
      - pattern: '^([0-9a-f]{4})([0-9a-f]{4})-([0-9a-f]{4})-([1-5][0-9a-f]{3})-([89ab][0-9a-f]{3})-([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{4})$'
        substitutions:
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.xml'
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.jp2'
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.jpg'
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.tif'
```

This makes it possible to read files from (historically) heterogeneous organized file storage.

For even more flexible resolving, it is possible to use a wildcard pattern in the filename part of the uris:

- Example for fixed mimetype: `file:///..../*.xml`
- Example for any mimetype: `file:///..../*`

Example configuration (via a Spring Boot `application.yml`):

```yml
resourceRepository:
  resolved:
    patterns:
      # resolving based on a given FileResource-UUID
      - pattern: '^([0-9a-f]{4})([0-9a-f]{4})-([0-9a-f]{4})-([1-5][0-9a-f]{3})-([89ab][0-9a-f]{3})-([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{4})$'
        substitutions:
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/*'
```

The substitution-Pattern matches all files in the UUID-specific directory if no specific mimetype is given for lookup:

```java
FileResource fileResource = fileResourceService.find(identifier, MimeType.MIME_WILDCARD);
```

Further customization of fileresource resolving is possible by implementing the `IdentifierToFileResourceUriResolver`-interface's with you own resolving logic, e.g. lookup access URIs in a database by using the identifier as key, and adding it to `FileResouceRepositoryImpl`. Own resolvers will be added additionally on top of the default implementation. So if you do not configure regex-patterns, the default resolver won't resolve any identifier and your custom resolvers have to handle resolving.

## Configuration

### Add library to your application

#### Maven project

`pom.xml`

```xml
<dependency>
  <groupId>de.digitalcollections.commons</groupId>
  <artifactId>dc-commons-file</artifactId>
  <version>${version.dc-commons-file}</version>
</dependency>
```

### Spring Environment

#### Configuration of ApplicationContext

Import `dc-commons-file` Spring configuration into your Spring configuration:

Example:

```java
...
import de.digitalcollections.commons.file.config.SpringConfigCommonsFile;
...

@Configuration
@Import(SpringConfigCommonsFile.class)
public class YourSpringConfig {
```

#### Configuration of URI resolving over regex patterns

##### Spring Boot

If you use `dc-commons-file` in a Spring Boot application, the place to configure URI resolving is the central `application.yml`(or `application.properties`) configuration file of your application.

Example resolving (in `application.yml`):

```yml
resourceRepository:
  resolved:
    patterns:
      # resolving based on an identifier of a given FileResource
      - pattern: '^(\w{5})$'
        substitutions:
          - 'classpath:/$1.xml'
          - 'classpath:/$1.json'

      # resolving based on a given FileResource-UUID
      - pattern: '^([0-9a-f]{4})([0-9a-f]{4})-([0-9a-f]{4})-([1-5][0-9a-f]{3})-([89ab][0-9a-f]{3})-([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{4})$'
        substitutions:
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.xml'
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.jp2'
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.jpg'
          - 'file:///mnt/DATA/repository/$1/$2/$3/$4/$5/$6/$7/$8/$0.tif'
```

## Usage

### Spring Environment

#### Use FileResourceService

For using `dc-commons-file` for accessing your file resources, get the `FileResourceService` bean from the Spring application context and use its methods.

Example:

```java
@Autowired
private FileResourceService fileResourceService;

@Override
public Manifest getManifest(String identifier) throws ResolvingException, ResourceNotFoundException, InvalidDataException {
  FileResource fileResource;
  try {
    fileResource = fileResourceService.find(identifier, MimeType.MIME_APPLICATION_JSON);
  } catch (ResourceIOException ex) {
    LOGGER.error("Error getting manifest for identifier {}", identifier, ex);
    throw new ResolvingException("No manifest for identifier " + identifier);
  }
```

The `find` method returns a `FileResource` model object containing information about the file resource and an access uri based on an identifier to URI resolving. For accessing the file content, use the following methods:

- `fileResourceService.getInputStream(fileResource)`
- `fileResourceService.getAsDocument(fileResource)` (convenience method on top of `getInputStream` to get XML document)
- `fileResourceService.getAsString(fileResource)` (convenience method on top of `getInputStream`)

# Migration Guides

## from version 4 to 5

In version 5 managed and resolved fileresource handling were merged to one way of fileresource handling.
The managed way of using UUID identifier to file path resolving was incorporated into resolved fileresource handling using pattern based resolving.
The heavily used resolved fileresource handling is now the only fileresource handling mechanism.

The `readOnly` flag for indicating readonly/writable handling has been removed from Service/Repository methods. Developers have to handle it for themselves. It is still available in the model object `FileResource`.

Customization of fileresource resolving was improved by introducing the IdentifierToFileResourceUriResolver-interface to be implemented, making it possible to inject own implementations.
By default the `IdentifierPatternToFileResourceUriResolverImpl` resolver is used, being configured over identifier-regex to filepath-patterns. Own resolvers will be added additionally. So if you do not configure patterns, the default resolver won't resolve any identifier and your custom resolvers have to handle resolving.

Migration steps:

- Upgrade `dc-commons-file` version in `pom.xml` to `5.x.y`.

### Migrate Resolved FileResource Service/Repository

- replace `ResolvedFileResourceServiceImpl` with `FileResourceService`:

Example:

```java
@Autowired
private ResolvedFileResourceServiceImpl resourceService;
```

changes to

```java
@Autowired
private FileResourceService resourceService;
```

- replace `ResolvedFileResourceServiceImpl.findKeys(pattern)` with `IdentifierPatternToFileResourceUriResolvingUtil.findKeys(pattern)`:

Example:

```java
List<String> allKeys = new ArrayList(fileResourceService.findKeys(pattern));
```

changes to

```java
import de.digitalcollections.commons.file.backend.impl.IdentifierPatternToFileResourceUriResolvingUtil;
...
@Autowired
IdentifierPatternToFileResourceUriResolvingUtil identifierPatternToFileResourceUriResolvingUtil;
...
List<String> allKeys = new ArrayList(identifierPatternToFileResourceUriResolvingUtil.findKeys(pattern));
```

### Migrate Managed FileResource Service/Repository
In version 5 the namespace configuration parameter has been removed, in favor of appending it to the folderpath (if needed).

Example `application.yml`:

```yml
resourceRepository:
  managed:
    namespace: 'dico'
    folderpath: '/local/resourceRepository'
```

changes to

```yml
resourceRepository:
  managed:
    folderpath: '/local/resourceRepository/dico'
```

## from version 3 to 4

### Separated managed and resolved implementations

In version 3 only one implementation class for FileResourceService and FileResourceRepository existed. This has been split in version 4 into two file storage specific implementations:

- ManagedFileResourceServiceImpl and ManagedFileResourceRepositoryImpl
- ResolvedFileResourceServiceImpl and ResolvedFileResourceRepositoryImpl

To avoid ambiguous Spring Beans replace interface specified autowiring with dedicated (e.g. ManagedFileResourceServiceImpl) implementation autowiring:

Example:

```java
@Autowired
FileResourceService resourceService;
```

changes to

```java
@Autowired
ResolvedFileResourceServiceImpl resourceService;
```

Method names and arguments also have changed. Change method calls accordingly to what has been used before.

### Refactored configuration

In version 3 configuration was done in two configuration files `multiPatternResolving-<env>.yml` and `SpringConfigCommonsFile-<env>.properties`.

Merge contained managed and resolved file storage configuration parameters into the central `application.yml` configuration file. Afterwards delete version 3 configuration files.

Example:

`multiPatternResolving-PROD.yml`:

```yml
- pattern: ^([^.]*?)$
  # just examples, change the filepath to your setup
  substitutions:
    - 'file:/bsbmultimedia/$1.mp4'
    - 'file:/bsbmultimedia/$1.mp3'
    - 'file:/bsbmultimedia/$1.ogg'
    - 'file:/bsbmultimedia/$1.pdf'
    - 'file:/bsbmultimedia/$1.txt'
    - 'file:/bsbmultimedia/$1.xml'
```

moved to `application.yml` section `resourceRepository.resolved.patterns`:

```yml
resourceRepository:
  resolved:
    patterns:
      - pattern: ^([^.]*?)$
        # just examples, change the filepath to your setup
        substitutions:
          - 'file:/bsbmultimedia/$1.mp4'
          - 'file:/bsbmultimedia/$1.mp3'
          - 'file:/bsbmultimedia/$1.ogg'
          - 'file:/bsbmultimedia/$1.pdf'
          - 'file:/bsbmultimedia/$1.txt'
          - 'file:/bsbmultimedia/$1.xml'
```

`SpringConfigCommonsFile-PROD.properties`:

```ini
resourceRepository.managedPathFactory.folderpath=/local/resourceRepository
resourceRepository.managedPathFactory.namespace=dico
```

moved to `application.yml` section `resourceRepository.managed`:

```yml
resourceRepository:
  managed:
    namespace: 'dico'
    folderpath: '/local/resourceRepository'
```
