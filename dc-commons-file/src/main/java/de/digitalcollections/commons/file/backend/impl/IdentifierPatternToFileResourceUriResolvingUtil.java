package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IdentifierPatternToFileResourceUriResolvingUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdentifierPatternToFileResourceUriResolvingUtil.class);

  private final IdentifierPatternToFileResourceUriResolvingConfig config;
  private DirectoryStream<Path> overriddenDirectoryStream;      // only for testing purposes

  @Autowired
  public IdentifierPatternToFileResourceUriResolvingUtil(IdentifierPatternToFileResourceUriResolvingConfig config) {
    this.config = config;
  }

  public Set<String> findKeys(String keyPattern) throws ResourceIOException {
    // The pattern for valid keys is the original pattern without any brackets inside, but surrounded with one bracket.
    // news_(\\d{8}) -> (news_\\d{8})
    Pattern validKeysPattern = Pattern.compile("(" + keyPattern.replace("(", "").replace(")", "").replace("^", "").replace("$", "") + ")");

    Set<String> keys = new HashSet<>();

    // For all handlers: Retrieve all substition paths for the given key pattern
    Set<Path> paths = getPathsByPattern(keyPattern);

    for (Path p : paths) {
      Path basePath = p.getParent();    // Only in this directory, we search for the matching keys

      // The pattern for valid filenames is the filename of the path, where all backreferences are replaced by a wildcard regexp.
      // The whole pattern is finally surrounded by start and end regexp characters.
      String filenameAsKeyWithWildcards = p.getFileName().toString().replaceAll("\\$\\d+", ".*");
      Pattern validFilenamesPattern = Pattern.compile("^" + filenameAsKeyWithWildcards + "$");

      if (basePath == null || "".equals(basePath.toString())) {
        basePath = Paths.get("/");
      }

      // We must stay within the given path. So, no path substitutions outside are allowed!
      if (basePath.normalize().toString().contains("$")) {
        throw new ResourceIOException("Cannot find keys for substitutions with references in paths");
      }

      // Ensure, the basePath does not start with "file:"
      if (basePath.toString().startsWith("file:")) {
        basePath = Paths.get(basePath.toString().substring(5));
      }

      // Retrieve all files in the substitution path and filter out the non matching ones.
      // "Matching" means, match the filename of the substitution and match the key pattern
      // Finally map them onto the keys
      try (Stream<Path> stream = getDirectory(basePath).stream()) {
        keys.addAll(stream.map(path -> path.getFileName().normalize().toString())
          .filter(filename -> matchesPattern(validFilenamesPattern, filename))
          .filter(filename -> matchesPattern(validKeysPattern, filename))
          .map(filename -> mapToPattern(validKeysPattern, filename))
          .collect(Collectors.toSet()));
      } catch (IOException e) {
        LOGGER.error("Cannot traverse directory " + basePath + ": " + e, e);
      }
    }

    return keys;
  }

  public Set<Path> getPathsByPattern(String pattern) throws ResourceIOException {
    Set<Path> paths = new HashSet<>();
    for (IdentifierPatternToFileResourceUriResolverImpl resolver : config.getPatterns()) {
      if (resolver.getPattern().equals(pattern)) {
        paths.addAll(resolver.getPaths());
      }
    }
    return paths;
  }

  private List<Path> getDirectory(Path basePath) throws IOException {
    List<Path> ret = new ArrayList<>();

    // The overriddenDirectoryStream is only used for testing
    try (DirectoryStream<Path> directoryStream = overriddenDirectoryStream == null ? Files.newDirectoryStream(basePath) : overriddenDirectoryStream) {
      Iterator<Path> it = directoryStream.iterator();
      if (it != null) {
        while (it.hasNext()) {
          Path path = it.next();
          if ((overriddenDirectoryStream != null) || Files.isRegularFile(path)) {
            ret.add(path);
          }
        }
      }
    }

    return ret;
  }

  private String mapToPattern(Pattern pattern, String filename) {
    Matcher m = pattern.matcher(filename);
    if (m.find()) {
      return m.group(1);
    } else {
      return null;
    }
  }

  private boolean matchesPattern(Pattern pattern, String filename) {
    Matcher m = pattern.matcher(filename);
    return m.find();
  }

  protected void overrideDirectoryStream(DirectoryStream<Path> overriddenDirectoryStream) {
    this.overriddenDirectoryStream = overriddenDirectoryStream;
  }

  /**
   * Convert "Thequickbrownfoxjumps" to String[] {"Theq","uick","brow","nfox","jump","s"}
   *
   * @param text text to split
   * @param partLength length of parts
   * @return array of text parts
   */
  public static String[] splitEqually(String text, int partLength) {
    if (StringUtils.isEmpty(text) || partLength == 0) {
      return new String[]{text};
    }

    int textLength = text.length();

    // Number of parts
    int numberOfParts = (textLength + partLength - 1) / partLength;
    String[] parts = new String[numberOfParts];

    // Break into parts
    int offset = 0;
    int i = 0;
    while (i < numberOfParts) {
      parts[i] = text.substring(offset, Math.min(offset + partLength, textLength));
      offset += partLength;
      i++;
    }

    return parts;
  }

  /**
  @param uuid an uuid, e.g. "a30cf362-5992-4f5a-8de0-61938134e721"
  @return filepath with uuid splitted in 4 character junks as directories, e.g. "a30c/f362/5992/4f5a/8de0/6193/8134/e721"
   */
  public static String getSplittedUuid(String uuid) {
    String uuidWithoutDashes = uuid.replaceAll("-", "");
    String[] pathParts = splitEqually(uuidWithoutDashes, 4);
    String splittedUuidPath = String.join(File.separator, pathParts);
    return splittedUuidPath;
  }
}
