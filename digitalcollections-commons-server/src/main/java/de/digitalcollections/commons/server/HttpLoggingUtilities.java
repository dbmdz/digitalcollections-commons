package de.digitalcollections.commons.server;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Location;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import javax.servlet.http.HttpServletRequest;
import net.logstash.logback.marker.LogstashMarker;
import static net.logstash.logback.marker.Markers.append;
import static net.logstash.logback.marker.Markers.appendArray;

public class HttpLoggingUtilities {
  private static DatabaseReader geoIpDatabase = null;

  static {
    try {
      InputStream dbStream = HttpLoggingUtilities.class.getResourceAsStream("/geolite2/GeoLite2-City.mmdb");
      geoIpDatabase = new DatabaseReader.Builder(dbStream).build();
    } catch (IOException e) {
      // NOP, Leave at null
    }
  }

  public static LogstashMarker makeRequestLoggingMarker(HttpServletRequest request) throws IOException {
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

    InetAddress clientIp = InetAddress.getByName(ipString);
    try {
      final Location clientLocation = geoIpDatabase.city(clientIp).getLocation();
      marker.and(append("ipLatitude", clientLocation.getLatitude()))
          .and(append("ipLongitude", clientLocation.getLongitude()));
    } catch (GeoIp2Exception e) {
      // NOP, do nothing
    }

    return marker;
  }

}
