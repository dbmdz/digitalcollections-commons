package de.digitalcollections.commons.springmvc.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.logstash.logback.marker.LogstashMarker;
import static net.logstash.logback.marker.Markers.append;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
/**
 * Usage: register it as interceptor in your Spring config and add it as ControllerAdvice to Spring context, e.g. by
 * instantiating it through ComponentScan:
 * <pre>
 * @ComponentScan(basePackages = {
 *   "de.digitalcollections.commons.springmvc.interceptors"
 * })
 * ...
 * public class SpringConfigWeb extends WebMvcConfigurerAdapter {
 *   ...
 *   @Override public void addInterceptors(InterceptorRegistry registry) {
 *     RequestProcessingTimeInterceptor requestProcessingTimeInterceptor = new RequestProcessingTimeInterceptor();
 *     registry.addInterceptor(requestProcessingTimeInterceptor);
 *   }
 *   ...
 * }
 * </pre>
 */
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter implements ResponseBodyAdvice<Object> {

  private static final Logger LOGGER = LoggerFactory.
          getLogger(RequestProcessingTimeInterceptor.class);

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter mp, MediaType mt, Class<? extends HttpMessageConverter<?>> type, ServerHttpRequest request, ServerHttpResponse response) {
    long startTime = (Long) ((ServletServerHttpRequest) request).getServletRequest().
            getAttribute("startTime");
    final long duration = System.currentTimeMillis() - startTime;
    response.getHeaders().add("x-execution-duration", duration + " ms");
    return body;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
          throws Exception {
    long startTime = System.currentTimeMillis();
    LOGGER.debug("request URL={} :: Start Time={}",
            request.getRequestURL().toString(), System.currentTimeMillis());
    request.setAttribute("startTime", startTime);
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
          throws Exception {
    LOGGER.debug("request URL={} :: Sent to Handler :: Current Time={}",
            request.getRequestURL().toString(), System.currentTimeMillis());
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
          throws Exception {
    long startTime = (Long) request.getAttribute("startTime");
    LOGGER.debug("request URL={} :: End Time={}",
            request.getRequestURL().toString(), System.currentTimeMillis());

    final long duration = System.currentTimeMillis() - startTime;
    LogstashMarker marker = append("request_url", request.getRequestURL().toString())
            .and(append("processing_time", duration));
    LOGGER.info(marker, "request URL={} :: processing time={} ms",
            request.getRequestURL().toString(), duration);
  }

  @Override
  public boolean supports(MethodParameter mp, Class<? extends HttpMessageConverter<?>> type) {
    return true;
  }

}
