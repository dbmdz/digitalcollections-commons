package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.backend.impl.handler.ResolvedResourcePersistenceTypeHandler;
import de.digitalcollections.commons.file.backend.impl.handler.ResourcePersistenceTypeHandler;
import de.digitalcollections.commons.file.config.SpringConfigCommonsFile;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType.RESOLVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfigCommonsFile.class})
public class FileResourceRepositoryImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceRepositoryImplTest.class);

  @Autowired
  private FileResourceRepository resourceRepository;

  @Autowired
  private ResourceLoader resourceLoader;

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  public FileResourceRepositoryImplTest() {
  }

  @BeforeClass
  public static void setUpClass() {
    System.setProperty("spring.profiles.active", "TEST");
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testReadXMLDocument() throws ResourceIOException {
    String key = "snafu";
    FileResourcePersistenceType resourcePersistenceType = RESOLVED;
    Document document = resourceRepository.getDocument(key, resourcePersistenceType);
    Node rootElement = document.getElementsByTagName("rootElement").item(0);
    String textContent = rootElement.getTextContent();
    Assert.assertEquals("SNAFU", textContent);
  }

  /**
   * Test of create method, of class ResourceRepositoryImpl.
   */
  @Test
  public void testCreate() throws Exception {
    // test managed
    String key = "a30cf362-5992-4f5a-8de0-61938134e721";
    FileResourcePersistenceType resourcePersistenceType = FileResourcePersistenceType.MANAGED;
    FileResource resource = resourceRepository.create(key, resourcePersistenceType, "xml");
    URI expResult = URI.create("file:///src/test/resources/repository/dico/a30c/f362/5992/4f5a/8de0/6193/8134/e721/a30cf362-5992-4f5a-8de0-61938134e721.xml");
    URI result = resource.getUri();
    Assert.assertEquals(expResult, result);

    // test resolved
    key = "bsb00001000";
    resourcePersistenceType = RESOLVED;
    resource = resourceRepository.create(key, resourcePersistenceType, "xml");
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    Assert.assertEquals(expResult, result);
    Assert.assertFalse(resource.isReadonly());

    // test referenced
    key = "bsb00001000";
    resourcePersistenceType = FileResourcePersistenceType.REFERENCED;
    resource = resourceRepository.create(key, resourcePersistenceType, "xml");
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    Assert.assertEquals(expResult, result);
    Assert.assertTrue(resource.isReadonly());
  }

  /**
   * Test of find method, of class ResourceRepositoryImpl.
   */
  @Test
  public void testFind() throws Exception {
    String key = "snafu";
    FileResourcePersistenceType resourcePersistenceType = RESOLVED;
    FileResource resource = resourceRepository.find(key, resourcePersistenceType, MimeType.MIME_APPLICATION_XML);

    URI expResult = URI.create("classpath:/snafu.xml");
    URI result = resource.getUri();
    Assert.assertEquals(expResult, result);

    long expSize = 71;
    long size = resource.getSizeInBytes();
    Assert.assertEquals(expSize, size);

    LocalDateTime lastModified = resource.getLastModified();
    Assert.assertTrue(lastModified.getDayOfMonth() > 0);
  }

  @Test
  public void testFindMimeWildcard() throws Exception {
    FileResource res = resourceRepository.find("snafu", RESOLVED, MimeType.MIME_WILDCARD);
    assertThat(res.getUri()).isEqualTo(URI.create("classpath:/snafu.xml"));
  }

  @Test
  public void findValidKeys() throws Exception {
    List<ResourcePersistenceTypeHandler> resourcePersistenceTypeHandlers = new ArrayList();
    resourcePersistenceTypeHandlers.add(new ResolvedResourcePersistenceTypeHandler());

    FileResourceRepositoryImpl resourceRepository = new FileResourceRepositoryImpl(resourcePersistenceTypeHandlers, resourceLoader);

    List<Path> paths = new ArrayList<>();
    paths.add(Paths.get("/opt/news/news_$1.md"));

    List<ResourcePersistenceTypeHandler> resolvers = new ArrayList<>();
    ResolvedResourcePersistenceTypeHandler mockMultiPatternsFileNameResolver = mock(ResolvedResourcePersistenceTypeHandler.class);
    when(mockMultiPatternsFileNameResolver.getResourcePersistenceType()).thenReturn(FileResourcePersistenceType.RESOLVED);
    when(mockMultiPatternsFileNameResolver.getPathsForPattern("news_(\\d{8})")).thenReturn(paths);
    resolvers.add(mockMultiPatternsFileNameResolver);

    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = { Paths.get("/opt/news/news_12345678.md"), Paths.get("/opt/news/news_23456789.md"),
        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));
    resourceRepository.overrideDirectoryStream(mockDirectoryStream);

    resourceRepository.setResourcePersistenceHandlers(resolvers);
    Set<String> keys = resourceRepository.findKeys("news_(\\d{8})", RESOLVED);
    assertThat(keys).containsExactly("news_12345678","news_23456789");
  }

  @Test
  public void findValidKeysForExtendedPattern() throws Exception {
    List<ResourcePersistenceTypeHandler> resourcePersistenceTypeHandlers = new ArrayList();
    resourcePersistenceTypeHandlers.add(new ResolvedResourcePersistenceTypeHandler());

    FileResourceRepositoryImpl resourceRepository = new FileResourceRepositoryImpl(resourcePersistenceTypeHandlers, resourceLoader);

    List<Path> paths = new ArrayList<>();
    paths.add(Paths.get("/opt/news/news_$1$2.md"));

    List<ResourcePersistenceTypeHandler> resolvers = new ArrayList<>();
    ResolvedResourcePersistenceTypeHandler mockMultiPatternsFileNameResolver = mock(ResolvedResourcePersistenceTypeHandler.class);
    when(mockMultiPatternsFileNameResolver.getResourcePersistenceType()).thenReturn(FileResourcePersistenceType.RESOLVED);
    when(mockMultiPatternsFileNameResolver.getPathsForPattern("news_(\\d{6})(\\d{2})")).thenReturn(paths);
    resolvers.add(mockMultiPatternsFileNameResolver);

    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = { Paths.get("/opt/news/news_12345678.md"), Paths.get("/opt/news/news_23456789.md"),
        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));
    resourceRepository.overrideDirectoryStream(mockDirectoryStream);

    resourceRepository.setResourcePersistenceHandlers(resolvers);
    Set<String> keys = resourceRepository.findKeys("news_(\\d{6})(\\d{2})", RESOLVED);
    assertThat(keys).containsExactly("news_12345678","news_23456789");
  }

  @Test(expected = ResourceIOException.class)
  public void assertNonexistingFile() throws ResourceIOException, URISyntaxException {
    FileResource nonexistingResource = new FileResourceImpl();
    nonexistingResource.setUri(new URI("file:/tmp/nonexistant"));
    nonexistingResource.setMimeType(MimeType.MIME_WILDCARD);
    resourceRepository.assertReadability(nonexistingResource);
  }

  @Test(expected = ResourceIOException.class)
  public void assertNonReadableFile() throws ResourceIOException, URISyntaxException {
    FileResource nonReadableResource = new FileResourceImpl();
    nonReadableResource.setUri(new URI("file:/vmlinuz"));
    nonReadableResource.setMimeType(MimeType.MIME_WILDCARD);
    resourceRepository.assertReadability(nonReadableResource);
  }

  @Test(expected = ResourceIOException.class)
  public void assertZeroByteFile() throws ResourceIOException, URISyntaxException {
    FileResource zeroByteLengthResource = new FileResourceImpl();
    zeroByteLengthResource.setUri(new URI("file:/proc/uptime"));
    zeroByteLengthResource.setMimeType(MimeType.MIME_WILDCARD);
    resourceRepository.assertReadability(zeroByteLengthResource);
  }

  @Test
  public void assertExistingFile() throws ResourceIOException, URISyntaxException {
    FileResource existingResource = new FileResourceImpl();
    existingResource.setUri(new URI("file:/var/log/wtmp"));
    existingResource.setMimeType(MimeType.MIME_WILDCARD);
    resourceRepository.assertReadability(existingResource);
  }

  @Test
  public void testWriteFile() throws Exception {
    String newResourceFilename = "write_stream.txt";
    File newCreatedResource = folder.newFile(newResourceFilename);
    String newResourceContent = "Hopfenzeitung";

    FileResource newResource = new FileResourceImpl();
    newResource.setUri(new URI("file:" + newCreatedResource.getAbsolutePath()));
    newResource.setMimeType(MimeType.fromFilename(newCreatedResource.getName()));

    InputStream inputStream = new ByteArrayInputStream(newResourceContent.getBytes());
    Long actualFileSize = resourceRepository.write(newResource, inputStream);

    assertThat(actualFileSize).isEqualTo(13L);
  }

  @Test
  public void testWriteString() throws Exception {
    String newResourceFilename = "write_string.txt";
    File newCreatedResource = folder.newFile(newResourceFilename);
    String newResourceContent = "Hopfenzeitungen";

    FileResource newResource = new FileResourceImpl();
    newResource.setUri(new URI("file:" + newCreatedResource.getAbsolutePath()));
    newResource.setMimeType(MimeType.fromFilename(newCreatedResource.getName()));

    Long actualFileSize = resourceRepository.write(newResource, newResourceContent);

    assertThat(actualFileSize).isEqualTo(15L);
  }
}
