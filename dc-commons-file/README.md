# DigitalCollections: Commons File

This library ("DC Commons File") contains Services for accessing and working comfortably and flexibly with files.

Originally it has been developed for usage with Spring Framework. Thus it has `@Service` and `@Repository` annotated classes being well known to Spring developers. It has been extended for an usage outside a Spring environment.

With this library you are able to access files via different protocols (as part of URIs):

- `file://`
- `classpath://`
- `http(s)://` (readonly)

are supported.

Files (`FileResources`) are usually stored following a systematic pattern.

## Usage

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

#### Use FileResourceService

For using `dc-commons-file`for accessing your file resources, get the `FileResourceService` bean from the Spring application context and use its methods.

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

The `find` method returns a `FileResource` model object containing iformation about the file resource and an access uri based on an identifier to URI resolving. For accessing the file content, use the following methods:

- `fileResourceService.getInputStream(fileResource)`
- `fileResourceService.getAsDocument(fileResource)` (convenience method on top of `getInputStream` to get XML document)
- `fileResourceService.getAsString(fileResource)` (convenience method on top of `getInputStream`)

## Managed file storage

Supports: `file://`

The managed file storage uses an UUID as unique identifier for a file resource.

### Storing of newly created managed file resources

Whenever you create a new file resource a random UUID is assigned to it.
The managed file storage uses this UUID as basis for storing and finding file resources stored on a local filesystem (`file://`).

A managed file storage takes one configuration parameters (see [ManagedFileResourceRepositoryConfig.java](./src/main/java/de/digitalcollections.commons.file.backend.impl.managed.ManagedFileResourceRepositoryConfig.java)):

- folderpath: The root directory, where to store all file resources (e.g. `/local/repository`)

The configuration of `folderpath` is bound to the application property / environment variable `resourceRepository.managed.folderpath`.

Example `application.yml`of a Spring Boot webapp:

```yml
resourceRepository:
  managed:
    folderpath: '/local/repository'
```

Above example results in the repository path `/local/repository/`.

The UUID of a file resource is used to construct the sub-directories under the repository path.
The managed file storage splits the UUID in 4-character long parts and creates corresponding subdirectories.
The filename is created depending on what additionally is given beside the UUID.

- If a mimetype (e.g. `application/xml`) or file extension is given, the corresponding file extension will be appended to the UUID with `.`-separator
- If no mimetype is given, no file extension will be appended.

Example: A file resource defined with filename `1.jpg` and UUID `a30cf362-5992-4f5a-8de0-61938134e721` results in the file resource directory `/local/repository/a30c/f362/5992/4f5a/8de0/6193/8134/e721/` containing a file `a30cf362-5992-4f5a-8de0-61938134e721.jpg`

### Retrieving of existing file resources

A managed file resource can be retrieved by its UUID. It is not necessary to give the mimetype as additional information.
This will be looked up by inspecting the file extension in the folder corresponding to the UUID.

## Resolved file storage

Supports: `file://`, `classpath://` and `http(s)://`

The resolved file storage uses a String as unique identifier for a file resource.
The resolved file storage uses this identifier as key for resolving an unique URI via a configured list of URIs bound to configured identifier-patterns (using regular expressions).

A resolved file storage configuration takes a list of regular-expression-patterns each containing a list of uri-templates (`substitutions`) with dynamic parts filled by using regular expression matching groups.

The configuration is bound to the application property / environment variable `resourceRepository.resolved.patterns`.

Example `application.yml` of a Spring Boot webapp:

```yml
resourceRepository:
  resolved:
    patterns:
      - pattern: '^(\w{5})$'
        substitutions:
          - 'classpath:/$1.xml'
          - 'classpath:/$1.json'

      - pattern: '^(\w{3})(\d{4})(\d{4})$'
        substitutions:
          - 'http://rest.digitale-sammlungen.de/data/$1$2$3.xml'
          - 'http://iiif.digitale-sammlungen.de/presentation/v2/$1$2$3/manifest.json'

      - pattern: '^(\w{3})(\d{4})(\d{4})_(\d{5})'
        substitutions:
          - 'http://rest.digitale-sammlungen.de/data/$1$2$3_$4.jpg'
```

A pattern can have a list of substitutions. In this case the substitution is chosen that matches the mimetype requested. The sequence of the substitution-entries is also considered in two ways:

- mimetype matching: The first matching mimetype from top down is selected
- existing check: If file resource of first selected match (uri) does not exist, the next matching uri is tested. Finally the first uri matching and existing is returned for the given identifier

# Migration Guides

## from version 4 to 5


In version 5 manged and resolved fileresource handling were merged to one way of fileresource handling.
The manged way of using UUID identifier to file path resolving was incorporated into resolved fileresource handling using pattern base resolving.
The heavily used resolved fileresource handling is now the only fileresource handling mechanism.

The `readOnly` flag for indicating readonly/writable handling has been removed from Service/Repository methods. Developers have to handle it for themselves. It is still available in the model object `FileResource`.

Customization of fileresource resolving was improved by introducing the IdentifierToFileResourceUriResolver-interface to be implemented, making it possible to inject own implementations.
By default the `IdentifierPatternToFileResourceUriResolverImpl` resolver is used, being configured over identifier-regex to filepath-patterns. Own resolvers will be added additionally. So if you do not configure patterns, dthe default resolver won't resolve any identifier and your custom resolvers have to handle resolving.

Migration steps:

- Upgrade `dc-commons-file` version in `pom.xml` to `5.x.y`.

### Migrate Resolved FileResource Service/Repository

- replace `ResolvedFileResourceServiceImpl` with `FileResourceService`:

Example:

```java
@Autowired
private ResolvedFileResourceServiceImpl resourceService;
```

changed to

```java
@Autowired
private FileResourceService resourceService;
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
