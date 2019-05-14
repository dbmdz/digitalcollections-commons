package de.digitalcollections.commons.file.backend.impl.managed;

import de.digitalcollections.commons.file.config.SpringConfigCommonsFile;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.net.URI;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringConfigCommonsFile.class})
@ActiveProfiles("TEST")
public class ManagedFileResourceRepositoryImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ManagedFileResourceRepositoryImplTest.class);

  @Autowired
  private ManagedFileResourceRepositoryImpl resourceRepository;

  @Autowired
  private ResourceLoader resourceLoader;

  @Test
  public void testCreate() throws Exception {
    // test managed
    FileResource resource = resourceRepository.create(MimeType.MIME_APPLICATION_XML, "test.xml");
    resource.setUuid(UUID.fromString("a30cf362-5992-4f5a-8de0-61938134e721"));
    resource.setUri(resourceRepository.createUri(resource.getUuid(), resource.getMimeType()));
    URI expResult = URI.create("file:///src/test/resources/repository/dico/a30c/f362/5992/4f5a/8de0/6193/8134/e721/a30cf362-5992-4f5a-8de0-61938134e721.xml");
    URI result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
  }

  @Test
  public void testSplitEqually() {
    String[] expectedResult = new String[]{"Theq", "uick", "brow", "nfox", "jump", "s"};
    String[] result = ManagedFileResourceRepositoryImpl.splitEqually("Thequickbrownfoxjumps", 4);
    assertThat(result).containsExactly(expectedResult);
  }

  @Test
  public void testGetSplittedUuidPath() {
    String expectedResult = "c30c/f362/5992/4f5a/8de0/6193/8134/e721";
    String result = resourceRepository.getSplittedUuidPath("c30cf362-5992-4f5a-8de0-61938134e721");
    assertThat(result).isEqualTo(expectedResult);
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
