package org.tc.demo.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class ParameterException extends ServiceException {
  public ParameterException(String msg, Throwable reason) {
    super("Request parameters are not allowed: " + msg, reason);
  }
  
  public ParameterException(String msg) {
    super("Request parameters are not allowed: " + msg);
  }
}
