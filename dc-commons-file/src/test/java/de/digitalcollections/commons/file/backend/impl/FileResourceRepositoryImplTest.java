package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.impl.resolved.PatternFileNameResolverImpl;
import de.digitalcollections.commons.file.backend.impl.resolved.ResolvedFileResourcesConfig;
import de.digitalcollections.commons.file.config.SpringConfigCommonsFile;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringConfigCommonsFile.class})
@ActiveProfiles("TEST")
public class FileResourceRepositoryImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceRepositoryImplTest.class);

  @Autowired
  private FileResourceRepositoryImpl resourceRepository;

  @Autowired
  private ResourceLoader resourceLoader;

  @Test
  public void testReadResolvedXMLDocument() throws ResourceIOException, ResourceNotFoundException {
    FileResource fileResource = resourceRepository.createResolved("snafu", MimeType.MIME_APPLICATION_XML, true);
    Document document = resourceRepository.getDocument(fileResource);
    Node rootElement = document.getElementsByTagName("rootElement").item(0);
    String textContent = rootElement.getTextContent();

    assertThat("SNAFU").isEqualTo(textContent);
  }

  @Test
  public void testCreate() throws Exception {
    // test managed
    FileResource resource = resourceRepository.createManaged(MimeType.MIME_APPLICATION_XML, "test.xml");
    resource.setUuid(UUID.fromString("a30cf362-5992-4f5a-8de0-61938134e721"));
    resource.setUri(resourceRepository.createUriForManagedFileResource(resource.getUuid(), resource.getMimeType(), resource.getFilename()));
    URI expResult = URI.create("file:///src/test/resources/repository/dico/a30c/f362/5992/4f5a/8de0/6193/8134/e721/a30cf362-5992-4f5a-8de0-61938134e721_test.xml");
    URI result = resource.getUri();
    assertThat(expResult).isEqualTo(result);

    // test resolved
    String identifier = "bsb00001000";
    resource = resourceRepository.createResolved(identifier, MimeType.MIME_APPLICATION_XML, false);
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
    assertThat(resource.isReadonly()).isFalse();

    // test referenced
    identifier = "bsb00001000";
    resource = resourceRepository.createResolved(identifier, MimeType.MIME_APPLICATION_XML, true);
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
    assertThat(resource.isReadonly()).isTrue();
  }

  @Test
  public void testFind() throws Exception {
    FileResource resource = resourceRepository.findResolved("snafu", MimeType.MIME_APPLICATION_XML, true);

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
    FileResource res = resourceRepository.findResolved("snafu", MimeType.MIME_WILDCARD, true);
    assertThat(res.getUri()).isEqualTo(URI.create("classpath:/snafu.xml"));
  }

  @Test
  public void testSplitEqually() {
    String[] expectedResult = new String[]{"Theq", "uick", "brow", "nfox", "jump", "s"};
    String[] result = FileResourceRepositoryImpl.splitEqually("Thequickbrownfoxjumps", 4);
    assertThat(result).containsExactly(expectedResult);
  }

  @Test
  public void testGetSplittedUuidPath() {
    FileResourceRepositoryImpl impl = new FileResourceRepositoryImpl(null, null, null);
    String expectedResult = "c30c/f362/5992/4f5a/8de0/6193/8134/e721";
    String result = impl.getSplittedUuidPath("c30cf362-5992-4f5a-8de0-61938134e721");
    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  public void findValidKeys() throws Exception {
    @SuppressWarnings("unchecked")
    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {Paths.get("file:///opt/news/news_12345678.md"), Paths.get("file:///opt/news/news_23456789.md"),
                        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));

    ResolvedFileResourcesConfig resolvedFileResourcesConfig = new ResolvedFileResourcesConfig();
    PatternFileNameResolverImpl patternFileNameResolverImpl = new PatternFileNameResolverImpl("news_(\\d{8})", "file:///opt/news/news_$1.md");
    resolvedFileResourcesConfig.setPatterns(Arrays.asList(patternFileNameResolverImpl));

    FileResourceRepositoryImpl fileResourceRepository = new FileResourceRepositoryImpl(null, resolvedFileResourcesConfig, resourceLoader);
    fileResourceRepository.overrideDirectoryStream(mockDirectoryStream);

    Set<String> keys = fileResourceRepository.findKeysForResolvedFileResources("news_(\\d{8})");
    assertThat(keys).containsExactly("news_12345678", "news_23456789");
  }

  @Test
  public void findValidKeysForExtendedPattern() throws Exception {
    @SuppressWarnings("unchecked")
    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {Paths.get("file:///opt/news/news_12345678.md"), Paths.get("file:///opt/news/news_23456789.md"),
                        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));

    ResolvedFileResourcesConfig resolvedFileResourcesConfig = new ResolvedFileResourcesConfig();
    PatternFileNameResolverImpl patternFileNameResolverImpl = new PatternFileNameResolverImpl("news_(\\d{6})(\\d{2})", "file:///opt/news/news_$1$2.md");
    resolvedFileResourcesConfig.setPatterns(Arrays.asList(patternFileNameResolverImpl));

    FileResourceRepositoryImpl fileResourceRepository = new FileResourceRepositoryImpl(null, resolvedFileResourcesConfig, resourceLoader);
    fileResourceRepository.overrideDirectoryStream(mockDirectoryStream);

    Set<String> keys = fileResourceRepository.findKeysForResolvedFileResources("news_(\\d{6})(\\d{2})");
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
