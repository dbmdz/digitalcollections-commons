package de.digitalcollections.commons.file.backend.impl.resolved;

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
public class ResolvedFileResourceRepositoryImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolvedFileResourceRepositoryImplTest.class);

  @Autowired
  private ResolvedFileResourceRepositoryImpl resourceRepository;

  @Autowired
  private ResourceLoader resourceLoader;

  @Test
  public void testReadResolvedXMLDocument() throws ResourceIOException, ResourceNotFoundException {
    FileResource fileResource = resourceRepository.create("snafu", MimeType.MIME_APPLICATION_XML, true);
    Document document = resourceRepository.getDocument(fileResource);
    Node rootElement = document.getElementsByTagName("rootElement").item(0);
    String textContent = rootElement.getTextContent();

    assertThat("SNAFU").isEqualTo(textContent);
  }

  @Test
  public void testCreate() throws Exception {
    // test resolved
    String identifier = "bsb00001000";
    FileResource resource = resourceRepository.create(identifier, MimeType.MIME_APPLICATION_XML, false);
    URI expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    URI result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
    assertThat(resource.isReadonly()).isFalse();

    // test referenced
    identifier = "bsb00001000";
    resource = resourceRepository.create(identifier, MimeType.MIME_APPLICATION_XML, true);
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
    assertThat(resource.isReadonly()).isTrue();
  }

  @Test
  public void testFind() throws Exception {
    FileResource resource = resourceRepository.find("snafu", MimeType.MIME_APPLICATION_XML, true);

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
    FileResource res = resourceRepository.find("snafu", MimeType.MIME_WILDCARD, true);
    assertThat(res.getUri()).isEqualTo(URI.create("classpath:/snafu.xml"));
  }

  @Test
  public void findValidKeys() throws Exception {
    @SuppressWarnings("unchecked")
    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {Paths.get("file:///opt/news/news_12345678.md"), Paths.get("file:///opt/news/news_23456789.md"),
                        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));

    ResolvedFileResourceRepositoryConfig resolvedFileResourcesConfig = new ResolvedFileResourceRepositoryConfig();
    PatternFileNameResolverImpl patternFileNameResolverImpl = new PatternFileNameResolverImpl("news_(\\d{8})", "file:///opt/news/news_$1.md");
    resolvedFileResourcesConfig.setPatterns(Arrays.asList(patternFileNameResolverImpl));

    ResolvedFileResourceRepositoryImpl fileResourceRepository = new ResolvedFileResourceRepositoryImpl(resolvedFileResourcesConfig, resourceLoader);
    fileResourceRepository.overrideDirectoryStream(mockDirectoryStream);

    Set<String> keys = fileResourceRepository.findKeys("news_(\\d{8})");
    assertThat(keys).containsExactly("news_12345678", "news_23456789");
  }

  @Test
  public void findValidKeysForExtendedPattern() throws Exception {
    @SuppressWarnings("unchecked")
    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {Paths.get("file:///opt/news/news_12345678.md"), Paths.get("file:///opt/news/news_23456789.md"),
                        Paths.get("README.md"), Paths.get("/opt/news/news_123.md")};
    when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));

    ResolvedFileResourceRepositoryConfig resolvedFileResourcesConfig = new ResolvedFileResourceRepositoryConfig();
    PatternFileNameResolverImpl patternFileNameResolverImpl = new PatternFileNameResolverImpl("news_(\\d{6})(\\d{2})", "file:///opt/news/news_$1$2.md");
    resolvedFileResourcesConfig.setPatterns(Arrays.asList(patternFileNameResolverImpl));

    ResolvedFileResourceRepositoryImpl fileResourceRepository = new ResolvedFileResourceRepositoryImpl(resolvedFileResourcesConfig, resourceLoader);
    fileResourceRepository.overrideDirectoryStream(mockDirectoryStream);

    Set<String> keys = fileResourceRepository.findKeys("news_(\\d{6})(\\d{2})");
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
}
