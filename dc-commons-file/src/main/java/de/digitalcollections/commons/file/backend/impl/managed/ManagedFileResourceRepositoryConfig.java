package de.digitalcollections.commons.file.backend.impl.managed;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("resource-repository.managed")
public class ManagedFileResourceRepositoryConfig {

  private String namespace;
  private String folderpath;

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getFolderpath() {
    return folderpath;
  }

  public void setFolderpath(String folderpath) {
    this.folderpath = folderpath.replace("~", System.getProperty("user.home"));
  }
}
