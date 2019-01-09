package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.backend.impl.handler.ResolvedResourcePersistenceTypeHandler;
import de.digitalcollections.commons.file.backend.impl.handler.ResourcePersistenceTypeHandler;
import de.digitalcollections.commons.file.config.SpringConfigCommonsFile;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType.RESOLVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfigCommonsFile.class})
public class FileResourceRepositoryImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceRepositoryImplTest.class);

  @Autowired
  private FileResourceRepository resourceRepository;

  @Autowired
  private ResourceLoader resourceLoader;

  @BeforeAll
  public static void setUpClass() {
    System.setProperty("spring.profiles.active", "TEST");
  }

  @Test
  public void testReadXMLDocument() throws ResourceIOException, ResourceNotFoundException {
    String key = "snafu";
    FileResourcePersistenceType resourcePersistenceType = RESOLVED;
    Document document = resourceRepository.getDocument(key, resourcePersistenceType);
    Node rootElement = document.getElementsByTagName("rootElement").item(0);
    String textContent = rootElement.getTextContent();

    assertThat("SNAFU").isEqualTo(textContent);
  }

  /**
   * Test of create method, of class ResourceRepositoryImpl.
   * @throws java.lang.Exception
   */
  @Test
  public void testCreate() throws Exception {
    // test managed
    String key = "a30cf362-5992-4f5a-8de0-61938134e721";
    FileResourcePersistenceType resourcePersistenceType = FileResourcePersistenceType.MANAGED;
    FileResource resource = resourceRepository.create(key, resourcePersistenceType, "xml");
    URI expResult = URI.create("file:///src/test/resources/repository/dico/a30c/f362/5992/4f5a/8de0/6193/8134/e721/a30cf362-5992-4f5a-8de0-61938134e721.xml");
    URI result = resource.getUri();
    assertThat(expResult).isEqualTo(result);

    // test resolved
    key = "bsb00001000";
    resourcePersistenceType = RESOLVED;
    resource = resourceRepository.create(key, resourcePersistenceType, "xml");
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
    assertThat(resource.isReadonly()).isFalse();

    // test referenced
    key = "bsb00001000";
    resourcePersistenceType = FileResourcePersistenceType.REFERENCED;
    resource = resourceRepository.create(key, resourcePersistenceType, "xml");
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
    assertThat(resource.isReadonly()).isTrue();
  }

  /**
   * Test of find method, of class ResourceRepositoryImpl.
   * @throws java.lang.Exception
   */
  @Test
  public void testFind() throws Exception {
    String key = "snafu";
    FileResourcePersistenceType resourcePersistenceType = RESOLVED;
    FileResource resource = resourceRepository.find(key, resourcePersistenceType, MimeType.MIME_APPLICATION_XML);

    URI expResult = URI.create("classpath:/snafu.xml");
    URI result = resource.getUri();
    assertThat(expResult).isEqualTo(result);

    long expSize = 71;
    long size = resource.getSizeInBytes();
    assertThat(expSize).isEqualTo(size);

    LocalDateTime lastModified = resource.getLastModified();
    assertThat(lastModified.getDayOfMonth() > 0).isTrue();
  }

  @Test
  public void testFindMimeWildcard() throws Exception {
    FileResource res = resourceRepository.find("snafu", RESOLVED, MimeType.MIME_WILDCARD);
    assertThat(res.getUri()).isEqualTo(URI.create("classpath:/snafu.xml"));
  }

  @Test
  public void findValidKeys() throws Exception {
    List<ResourcePersistenceTypeHandler> resourcePersistenceTypeHandlers = new ArrayList<>();
    resourcePersistenceTypeHandlers.add(new ResolvedResourcePersistenceTypeHandler());

    FileResourceRepositoryImpl resourceRepo = new FileResourceRepositoryImpl(resourcePersistenceTypeHandlers, resourceLoader);

    List<Path> paths = new ArrayList<>();
    paths.add(Paths.get("/opt/news/news_$1.md"));

    List<ResourcePersistenceTypeHandler> resolvers = new ArrayList<>();
    ResolvedResourcePersistenceTypeHandler mockMultiPatternsFileNameResolver = mock(ResolvedResourcePersistenceTypeHandler.class);
    when(mockMultiPatternsFileNameResolver.getResourcePersistenceType()).thenReturn(FileResourcePersistenceType.RESOLVED);
    when(mockMultiPatternsFileNameResolver.getPathsForPattern("news_(\\d{8})")).thenReturn(paths);
    resolvers.add(mockMultiPatternsFileNameResolver);

    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {Paths.get("/opt/news/news_12345678.md"), Paths.get("/opt/news/news_23456789.md"),
                        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));
    resourceRepo.overrideDirectoryStream(mockDirectoryStream);

    resourceRepo.setResourcePersistenceHandlers(resolvers);
    Set<String> keys = resourceRepo.findKeys("news_(\\d{8})", RESOLVED);
    assertThat(keys).containsExactly("news_12345678", "news_23456789");
  }

  @Test
  public void findValidKeysForExtendedPattern() throws Exception {
    List<ResourcePersistenceTypeHandler> resourcePersistenceTypeHandlers = new ArrayList();
    resourcePersistenceTypeHandlers.add(new ResolvedResourcePersistenceTypeHandler());

    FileResourceRepositoryImpl resourceRepo = new FileResourceRepositoryImpl(resourcePersistenceTypeHandlers, resourceLoader);

    List<Path> paths = new ArrayList<>();
    paths.add(Paths.get("/opt/news/news_$1$2.md"));

    List<ResourcePersistenceTypeHandler> resolvers = new ArrayList<>();
    ResolvedResourcePersistenceTypeHandler mockMultiPatternsFileNameResolver = mock(ResolvedResourcePersistenceTypeHandler.class);
    when(mockMultiPatternsFileNameResolver.getResourcePersistenceType()).thenReturn(FileResourcePersistenceType.RESOLVED);
    when(mockMultiPatternsFileNameResolver.getPathsForPattern("news_(\\d{6})(\\d{2})")).thenReturn(paths);
    resolvers.add(mockMultiPatternsFileNameResolver);

    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {Paths.get("/opt/news/news_12345678.md"), Paths.get("/opt/news/news_23456789.md"),
                        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));
    resourceRepo.overrideDirectoryStream(mockDirectoryStream);

    resourceRepo.setResourcePersistenceHandlers(resolvers);
    Set<String> keys = resourceRepo.findKeys("news_(\\d{6})(\\d{2})", RESOLVED);
    assertThat(keys).containsExactly("news_12345678", "news_23456789");
  }

  @Test
  public void assertNonexistingFile() {
    assertThatThrownBy(() -> {
      FileResource nonexistingResource = new FileResourceImpl();
      nonexistingResource.setUri(new URI("file:/tmp/nonexistant"));
      nonexistingResource.setMimeType(MimeType.MIME_WILDCARD);
      resourceRepository.assertReadability(nonexistingResource);
    }).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  public void assertNonReadableFile() {
    assertThatThrownBy(() -> {
      FileResource nonReadableResource = new FileResourceImpl();
      // TODO this is a system dependent test (only linux)
      nonReadableResource.setUri(new URI("file:/vmlinuz"));
      nonReadableResource.setMimeType(MimeType.MIME_WILDCARD);
      resourceRepository.assertReadability(nonReadableResource);
    }).isInstanceOf(ResourceIOException.class);
  }

  @Test
  public void assertZeroByteFile() {
    assertThatThrownBy(() -> {
      FileResource zeroByteLengthResource = new FileResourceImpl();
      zeroByteLengthResource.setUri(new URI("file:/proc/uptime"));
      zeroByteLengthResource.setMimeType(MimeType.MIME_WILDCARD);
      resourceRepository.assertReadability(zeroByteLengthResource);
    }).isInstanceOf(ResourceIOException.class);
  }

  // TODO: Currently, there's no native TemporaryFolder support in JUnit5, so we disable the following test cases.
  // FIXME: As soon there's a proper support TemporaryFolder, enable the following test cases.
//  @Test
//  public void assertExistingFile() throws ResourceIOException, URISyntaxException, IOException, ResourceNotFoundException {
//    String newResourceFilename = "test_file.txt";
//    TempDirectory folder = new TempDirectory();
//    File newCreatedResource = folder.newFile(newResourceFilename);
//    String data = "Test data";
//    final Path filePath = newCreatedResource.toPath();
//    Files.write(filePath, data.getBytes());
//
//    FileResource existingResource = new FileResourceImpl();
//    existingResource.setUri(new URI("file:" + filePath.toString()));
//    existingResource.setMimeType(MimeType.MIME_WILDCARD);
//    resourceRepository.assertReadability(existingResource);
//  }
//
//  @Test
//  public void testWriteFile() throws Exception {
//    String newResourceFilename = "write_stream.txt";
//    TempDirectory folder = new TempDirectory();
//    File newCreatedResource = folder.newFile(newResourceFilename);
//    String newResourceContent = "Hopfenzeitung";
//
//    FileResource newResource = new FileResourceImpl();
//    newResource.setUri(new URI("file:" + newCreatedResource.getAbsolutePath()));
//    newResource.setMimeType(MimeType.fromFilename(newCreatedResource.getName()));
//
//    InputStream inputStream = new ByteArrayInputStream(newResourceContent.getBytes());
//    Long actualFileSize = resourceRepository.write(newResource, inputStream);
//
//    assertThat(actualFileSize).isEqualTo(13L);
//  }
//
//  @Test
//  public void testWriteString() throws Exception {
//    String newResourceFilename = "write_string.txt";
//    TempDirectory folder = new TempDirectory();
//    File newCreatedResource = folder.newFile(newResourceFilename);
//    String newResourceContent = "Hopfenzeitungen";
//
//    FileResource newResource = new FileResourceImpl();
//    newResource.setUri(new URI("file:" + newCreatedResource.getAbsolutePath()));
//    newResource.setMimeType(MimeType.fromFilename(newCreatedResource.getName()));
//
//    Long actualFileSize = resourceRepository.write(newResource, newResourceContent);
//
//    assertThat(actualFileSize).isEqualTo(15L);
//  }
}
