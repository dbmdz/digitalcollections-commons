package de.digitalcollections.commons.server;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Location;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class HttpLoggingUtilities {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpLoggingUtilities.class);
  private static boolean LOOKUP_LOCATION = true;
  private static DatabaseReader GEO_IP_DATABASE = null;

  /**
   * From http://codereview.stackexchange.com/a/65072
   *
   * @param ip IP to check
   * @return true if IP is a valid public IP
   */
  protected static boolean isValidPublicIp(String ip) {
    InetAddress address;
    try {
      address = InetAddress.getByName(ip);
    } catch (UnknownHostException exception) {
      return false; // assuming no logging, exception handling required
    }
    return !(address.isSiteLocalAddress()
            || address.isAnyLocalAddress()
            || address.isLinkLocalAddress()
            || address.isLoopbackAddress()
            || address.isMulticastAddress());
  }

  protected static String anonymizeIp(String ip) {
    return ip.replaceAll("(\\d+)\\.(\\d+)\\..*", "$1.$2");
  }

  /**
   * Puts http request infos (client infos) to MDC logging context. Make sure to
   * clear MDC after logging! Using MDC instead of LogstashMarker, because
   * otherwise we drag in logging implementation logstash as dependency. We care
   * for having no other logging dependency than slf4j.
   *
   * Usage example:
   * <pre>
   * <b>HttpLoggingUtilities.addRequestClientInfoToMDC(request);
   * MDC.put("collection name", name);</b>
   * try {
   *   Collection collection = presentationService.getCollection(name);
   *   LOGGER.info("Serving collection for {}", name);
   *   return collection;
   * } catch (NotFoundException e) {
   *   LOGGER.info("Did not find collection for {}", name);
   *   throw e;
   * } catch (InvalidDataException e) {
   *   LOGGER.info("Bad data for {}", name);
   *   throw e;
   * } finally {
   *   <b>MDC.clear();</b>
   * }
   * </pre>
   *
   * @param request http request object containing info
   */
  public static void addRequestClientInfoToMDC(HttpServletRequest request) {
    String protocol = request.getHeader("X-Forwarded-Proto");
    if (protocol == null) {
      protocol = request.getProtocol();
    }
    MDC.put("protocol", protocol);

    String ipString = request.getHeader("X-Forwarded-For");
    if (ipString == null) {
      ipString = request.getRemoteAddr();
    }
    MDC.put("anonymizedClientIp", anonymizeIp(ipString));

    MDC.put("userAgent", request.getHeader("User-Agent"));
    MDC.put("referer", request.getHeader("Referer"));

    if (LOOKUP_LOCATION) {
      if (GEO_IP_DATABASE == null) {
        try {
          InputStream dbStream = HttpLoggingUtilities.class.getResourceAsStream("/geolite2/GeoLite2-City.mmdb");
          GEO_IP_DATABASE = new DatabaseReader.Builder(dbStream).build();
        } catch (Throwable e) {
          LOOKUP_LOCATION = false;
          LOGGER.error("Could not open GeoIP database", e);
        }
      }
      if (isValidPublicIp(ipString)) {
        try {
          InetAddress clientIp = InetAddress.getByName(ipString);

          final Location clientLocation = GEO_IP_DATABASE.city(clientIp).getLocation();

          MDC.put("ipLatitude", String.valueOf(clientLocation.getLatitude()));
          MDC.put("ipLongitude", String.valueOf(clientLocation.getLongitude()));
        } catch (GeoIp2Exception | IOException e) {
          LOGGER.warn("Could not retrieve geo information for IP {}", ipString);
        }
      }
    }
  }
}
