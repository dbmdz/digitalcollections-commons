package de.digitalcollections.commons.springboot.contributor;

import de.digitalcollections.commons.springboot.monitoring.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class VersionInfoContributor implements InfoContributor {

  @Autowired
  VersionInfo versionInfo;

  @Override
  public void contribute(Info.Builder bldr) {
    bldr.withDetail("version", versionInfo.getArtifactVersions());
  }

}
