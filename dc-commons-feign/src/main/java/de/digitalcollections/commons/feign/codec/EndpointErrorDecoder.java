package de.digitalcollections.commons.feign.codec;

import de.digitalcollections.core.model.api.http.exceptions.HttpException;
import de.digitalcollections.core.model.api.http.exceptions.client.ForbiddenException;
import de.digitalcollections.core.model.api.http.exceptions.client.HttpClientException;
import de.digitalcollections.core.model.api.http.exceptions.client.ImATeapotException;
import de.digitalcollections.core.model.api.http.exceptions.client.ResourceNotFoundException;
import de.digitalcollections.core.model.api.http.exceptions.client.UnauthorizedException;
import de.digitalcollections.core.model.api.http.exceptions.client.UnavailableForLegalReasonsException;
import de.digitalcollections.core.model.api.http.exceptions.server.BadGatewayException;
import de.digitalcollections.core.model.api.http.exceptions.server.GatewayTimeOutException;
import de.digitalcollections.core.model.api.http.exceptions.server.HttpServerException;
import de.digitalcollections.core.model.api.http.exceptions.server.HttpVersionNotSupportedException;
import de.digitalcollections.core.model.api.http.exceptions.server.NotImplementedException;
import de.digitalcollections.core.model.api.http.exceptions.server.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class EndpointErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    final int status = response.status();
    final String request = response.request().toString();
    HttpException httpException = new HttpException(methodKey, status, request);

    if (400 <= status && status < 500) {
      return clientException(httpException);
    } else if (500 <= status && status < 600) {
      return serverException(httpException);
    } else {
      return httpException;
    }
  }

  private HttpClientException clientException(HttpException httpException) {
    switch (httpException.getStatus()) {
      case 401:
        return (UnauthorizedException) httpException;
      case 403:
        return (ForbiddenException) httpException;
      case 404:
        return (ResourceNotFoundException) httpException;
      case 413:
        return (ImATeapotException) httpException;
      case 451:
        return (UnavailableForLegalReasonsException) httpException;
      default:
        return (HttpClientException) httpException;
    }
  }

  private HttpServerException serverException(HttpException httpException) {
    switch (httpException.getStatus()) {
      case 501:
        return (NotImplementedException) httpException;
      case 502:
        return (BadGatewayException) httpException;
      case 503:
        return (ServiceUnavailableException) httpException;
      case 504:
        return (GatewayTimeOutException) httpException;
      case 505:
        return (HttpVersionNotSupportedException) httpException;
      default:
        return (HttpServerException) httpException;
    }
  }
}
