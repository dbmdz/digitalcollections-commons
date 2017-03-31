package de.digitalcollections.commons.springmvc.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter implements ResponseBodyAdvice<Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessingTimeInterceptor.class);

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter mp, MediaType mt, Class<? extends HttpMessageConverter<?>> type, ServerHttpRequest request, ServerHttpResponse response) {
    long startTime = (Long) ((ServletServerHttpRequest) request).getServletRequest().getAttribute("startTime");
    final long duration = System.currentTimeMillis() - startTime;
    response.getHeaders().add("x-execution-duration", duration + " ms");
    return body;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    long startTime = System.currentTimeMillis();
    LOGGER.debug("Request URL::" + request.getRequestURL().toString() + ":: Start Time=" + System.currentTimeMillis());
    request.setAttribute("startTime", startTime);
    //if returned false, we need to make sure 'response' is sent
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
          throws Exception {
    LOGGER.debug("Request URL::" + request.getRequestURL().toString() + " Sent to Handler :: Current Time="
            + System.currentTimeMillis());
    //we can add attributes in the modelAndView and use that in the view page
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
          throws Exception {
    long startTime = (Long) request.getAttribute("startTime");
    LOGGER.debug("Request URL::" + request.getRequestURL().toString() + ":: End Time="
            + System.currentTimeMillis());
    final long duration = System.currentTimeMillis() - startTime;
    LOGGER.info("Request URL::" + request.getRequestURL().toString() + ":: Time Taken=" + duration + " ms");
  }

  @Override
  public boolean supports(MethodParameter mp, Class<? extends HttpMessageConverter<?>> type) {
    return true;
  }

}
