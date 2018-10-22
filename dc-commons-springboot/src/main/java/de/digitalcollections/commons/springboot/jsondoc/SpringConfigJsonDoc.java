package de.digitalcollections.commons.springboot.jsondoc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jsondoc.spring.boot.starter.EnableJSONDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJSONDoc
public class SpringConfigJsonDoc implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfigJsonDoc.class);

  @Value("${server.port}")
  int serverPort;

  @Override
  public void afterPropertiesSet() throws Exception {
    String hostName = "";
    try {
      InetAddress addr = InetAddress.getLocalHost();
      hostName = addr.getCanonicalHostName();
      if (!hostName.contains(".")) {
        hostName = "localhost";
      }
    } catch (UnknownHostException e) {
      LOGGER.warn("Cannot determine local hostname: " + e, e);
      hostName = "localhost";
    }

    System.setProperty("jsondoc.basePath", "http://" + hostName + ":" + serverPort);
    LOGGER.info("jsondoc.basePath=" + System.getProperty("jsondoc.basePath"));
  }
}
