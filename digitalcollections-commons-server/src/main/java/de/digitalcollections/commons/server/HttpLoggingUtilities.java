package de.digitalcollections.commons.server;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Location;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import net.logstash.logback.marker.LogstashMarker;
import static net.logstash.logback.marker.Markers.append;
import static net.logstash.logback.marker.Markers.appendArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpLoggingUtilities {
  private static Logger LOGGER = LoggerFactory.getLogger(HttpLoggingUtilities.class);
  private static DatabaseReader geoIpDatabase = null;

  static {
    try {
      InputStream dbStream = HttpLoggingUtilities.class.getResourceAsStream("/geolite2/GeoLite2-City.mmdb");
      geoIpDatabase = new DatabaseReader.Builder(dbStream).build();
    } catch (IOException e) {
      LOGGER.error("Could not open GeoIP database", e);
    }
  }

  /** From http://codereview.stackexchange.com/a/65072 **/
  private static boolean isValidPublicIp(String ip) {
    Inet4Address address;
    try {
      address = (Inet4Address) InetAddress.getByName(ip);
    } catch (UnknownHostException exception) {
      return false; // assuming no logging, exception handling required
    }
    return !(address.isSiteLocalAddress()
        || address.isAnyLocalAddress()
        || address.isLinkLocalAddress()
        || address.isLoopbackAddress()
        || address.isMulticastAddress());
  }

  public static LogstashMarker makeRequestLoggingMarker(HttpServletRequest request) {
    String protocol = request.getHeader("X-Forwarded-Proto");
    if (protocol == null) {
      protocol = request.getProtocol();
    }
    String ipString = request.getHeader("X-Forwarded-For");
    if (ipString == null) {
      ipString = request.getRemoteAddr();
    }
    LogstashMarker marker = appendArray("anonymizedClientIp",
        ipString.replaceAll("(\\d+)\\.(\\d+)\\..*", "\1.\2"))
            .and(append("userAgent", request.getHeader("User-Agent")))
            .and(append("protocol", protocol))
            .and(append("referer", request.getHeader("Referer")));

    if (isValidPublicIp(ipString)) {
      try {
        InetAddress clientIp = InetAddress.getByName(ipString);
        final Location clientLocation = geoIpDatabase.city(clientIp).getLocation();
        marker.and(append("ipLatitude", clientLocation.getLatitude()))
            .and(append("ipLongitude", clientLocation.getLongitude()));
      } catch (GeoIp2Exception | IOException e) {
        LOGGER.warn("Could not retrieve geo information for IP {}", ipString);
      }
    }
    return marker;
  }

}
