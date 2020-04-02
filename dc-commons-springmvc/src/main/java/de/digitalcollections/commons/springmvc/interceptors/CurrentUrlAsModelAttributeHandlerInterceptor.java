package de.digitalcollections.commons.springmvc.interceptors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Adds current request url as model attribute "currentUrl" for usage in views. Also deletes given
 * params from query string.
 */
public class CurrentUrlAsModelAttributeHandlerInterceptor extends HandlerInterceptorAdapter {

  List<String> paramsToBeDeleted = new ArrayList<>();

  public void deleteParams(String... params) {
    if (params != null) {
      paramsToBeDeleted = Arrays.asList(params);
    }
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
    if (modelAndView != null) {
      String currentUrl = getCurrentUrl(request);
      modelAndView.addObject("currentUrl", currentUrl);
    }
  }

  private String getCurrentUrl(HttpServletRequest request) {
    String currentUrl = request.getRequestURI();
    if (request.getQueryString() != null) {
      currentUrl = currentUrl + "?" + request.getQueryString();
    }
    String contextPath = request.getContextPath();
    if (contextPath.length() > 1) {
      currentUrl = currentUrl.replaceFirst(contextPath, "");
    }
    for (String param : paramsToBeDeleted) {
      String paramValue = request.getParameter(param);
      currentUrl = deleteParam(param, paramValue, currentUrl);
    }
    return currentUrl;
  }

  protected String deleteParam(String paramName, String paramValue, String currentUrl) {
    if (paramName != null) {
      if (paramValue == null) {
        paramValue = "";
      }
      currentUrl =
          currentUrl.replaceAll(
              "&" + paramName + "=" + paramValue, ""); // there are other params before given param
      currentUrl =
          currentUrl.replaceAll(
              "\\?" + paramName + "=" + paramValue + "&",
              "?"); // there are other params after given param
      currentUrl =
          currentUrl.replaceAll(
              "\\?" + paramName + "=" + paramValue, ""); // given param was the only param
    }
    return currentUrl;
  }
}
