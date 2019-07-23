package org.tc.demo.utils;

@SuppressWarnings("serial")
public abstract class ServiceException extends RuntimeException {
  public ServiceException(String msg, Throwable ex) {
    super(msg, ex);
  }

  public ServiceException(String msg) {
    super(msg);
  }
}
