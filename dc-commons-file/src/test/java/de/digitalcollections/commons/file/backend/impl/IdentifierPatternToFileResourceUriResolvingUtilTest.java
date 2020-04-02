package de.digitalcollections.commons.file.backend.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class IdentifierPatternToFileResourceUriResolvingUtilTest {

  public IdentifierPatternToFileResourceUriResolvingUtilTest() {}

  @Test
  public void findValidKeys() throws Exception {
    @SuppressWarnings("unchecked")
    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {
      Paths.get("file:///opt/news/news_12345678.md"),
          Paths.get("file:///opt/news/news_23456789.md"),
      Paths.get("README.md"), Paths.get("/opt/news/news_123.md")
    };
    // when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));
    when(mockDirectoryStream.iterator()).thenReturn(Arrays.stream(mockFiles).iterator());

    IdentifierPatternToFileResourceUriResolvingConfig resolvedFileResourcesConfig =
        new IdentifierPatternToFileResourceUriResolvingConfig();
    IdentifierPatternToFileResourceUriResolverImpl patternFileNameResolverImpl =
        new IdentifierPatternToFileResourceUriResolverImpl(
            "news_(\\d{8})", "file:///opt/news/news_$1.md");
    resolvedFileResourcesConfig.setPatterns(Arrays.asList(patternFileNameResolverImpl));

    IdentifierPatternToFileResourceUriResolvingUtil util =
        new IdentifierPatternToFileResourceUriResolvingUtil(resolvedFileResourcesConfig);
    util.overrideDirectoryStream(mockDirectoryStream);

    Set<String> keys = util.findKeys("news_(\\d{8})");
    assertThat(keys).containsExactly("news_12345678", "news_23456789");
  }

  @Test
  public void findValidKeysForExtendedPattern() throws Exception {
    @SuppressWarnings("unchecked")
    DirectoryStream<Path> mockDirectoryStream = mock(DirectoryStream.class);
    Path[] mockFiles = {
      Paths.get("file:///opt/news/news_12345678.md"),
          Paths.get("file:///opt/news/news_23456789.md"),
      Paths.get("README.md"), Paths.get("/opt/news/news_123.md")
    };
    // when(mockDirectoryStream.spliterator()).then(invocation -> Arrays.spliterator(mockFiles));
    when(mockDirectoryStream.iterator()).thenReturn(Arrays.stream(mockFiles).iterator());

    IdentifierPatternToFileResourceUriResolvingConfig resolvedFileResourcesConfig =
        new IdentifierPatternToFileResourceUriResolvingConfig();
    IdentifierPatternToFileResourceUriResolverImpl patternFileNameResolverImpl =
        new IdentifierPatternToFileResourceUriResolverImpl(
            "news_(\\d{6})(\\d{2})", "file:///opt/news/news_$1$2.md");
    resolvedFileResourcesConfig.setPatterns(Arrays.asList(patternFileNameResolverImpl));

    IdentifierPatternToFileResourceUriResolvingUtil util =
        new IdentifierPatternToFileResourceUriResolvingUtil(resolvedFileResourcesConfig);
    util.overrideDirectoryStream(mockDirectoryStream);

    Set<String> keys = util.findKeys("news_(\\d{6})(\\d{2})");
    assertThat(keys).containsExactly("news_12345678", "news_23456789");
  }

  @Test
  public void testSplitEqually() {
    String[] expectedResult = new String[] {"Theq", "uick", "brow", "nfox", "jump", "s"};
    String[] result =
        IdentifierPatternToFileResourceUriResolvingUtil.splitEqually("Thequickbrownfoxjumps", 4);
    assertThat(result).containsExactly(expectedResult);
  }

  @Test
  public void testGetSplittedUuidPath() {
    String expectedResult = "c30c/f362/5992/4f5a/8de0/6193/8134/e721";
    String result =
        IdentifierPatternToFileResourceUriResolvingUtil.getSplittedUuid(
            "c30cf362-5992-4f5a-8de0-61938134e721");
    assertThat(result).isEqualTo(expectedResult);
  }
}
