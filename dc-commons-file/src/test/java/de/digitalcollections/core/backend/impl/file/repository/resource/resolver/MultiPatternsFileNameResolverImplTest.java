package de.digitalcollections.core.backend.impl.file.repository.resource.resolver;

import de.digitalcollections.commons.file.backend.impl.resolver.FileNameResolver;
import de.digitalcollections.commons.file.backend.impl.resolver.MultiPatternsFileNameResolverImpl;
import de.digitalcollections.commons.file.config.SpringConfigCommonsFile;
import static de.digitalcollections.model.api.identifiable.resource.MimeType.MIME_APPLICATION_JSON;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from the static inner SpringConfigBackendFile class
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class MultiPatternsFileNameResolverImplTest {

  @Autowired
  private FileNameResolver fileNameResolver;

  private URI xmlUri;
  private URI jsonUri;

  @BeforeClass
  public static void setupClass() {
    System.setProperty("spring.profiles.active", "TEST");
  }

  @Before
  public void setup() {
    xmlUri = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    jsonUri = URI.create("http://iiif.digitale-sammlungen.de/presentation/v2/bsb00001000/manifest.json");
  }

  /**
   * Test of getString method, of class MultiPatternsFileNameResolverImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetStrings() throws Exception {
    System.out.println("getString");
    String fileName = "bsb00001000_00001";
    String expResult = "http://rest.digitale-sammlungen.de/data/bsb00001000_00001.jpg";
    List<String> results = fileNameResolver.getStrings(fileName);
    assertThat(results).hasSize(1);
    assertThat(results.get(0)).isEqualTo(expResult);
  }

  /**
   * Test of getURI method, of class MultiPatternsFileNameResolverImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetURIWithoutMime() throws Exception {
    String identifier = "bsb00001000";
    List<URI> results = fileNameResolver.getUris(identifier);
    assertThat(results).hasSize(2);
    assertThat(results).containsExactly(xmlUri, jsonUri);
  }

  @Test
  public void testGetURIWithMime() throws Exception {
    String identifier = "bsb00001000";
    List<URI> jsonResults = fileNameResolver.getUris(identifier, MIME_APPLICATION_JSON);
    assertThat(jsonResults).hasSize(1);
    assertThat(jsonResults).containsExactly(jsonUri);
  }

  /**
   * Test of isResolvable method, of class MultiPatternsFileNameResolverImpl.
   */
  @Test
  public void testIsResolvable() {
    System.out.println("isResolvable");
    String identifier = "bsb00001000";
    assertThat(fileNameResolver.isResolvable(identifier)).isTrue();
  }

  @Configuration
  static class SpringConfigBackendFileTest extends SpringConfigCommonsFile {

    @Bean
    public FileNameResolver fileNameResolver() {
      return new MultiPatternsFileNameResolverImpl();
    }
  }
}
