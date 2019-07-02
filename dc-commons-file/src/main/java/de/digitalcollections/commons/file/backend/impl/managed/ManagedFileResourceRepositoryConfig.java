package de.digitalcollections.commons.file.backend.impl.managed;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("resource-repository.managed")
public class ManagedFileResourceRepositoryConfig {

  private String folderpath;

  public String getFolderpath() {
    return folderpath;
  }

  public void setFolderpath(String folderpath) {
    this.folderpath = folderpath.replace("~", System.getProperty("user.home"));
  }
}
