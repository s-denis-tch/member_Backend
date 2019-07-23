package org.tc.demo.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when a resource is not found.
 * 
 * @author rkv
 *
 */
@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends ServiceException {

  public NotFoundException(Class<?> objClass, Long objId) {
    super(composeErrorMsg(objClass, objId));
  }

  public NotFoundException(String msg) {
    super(msg);
  }

  private static String composeErrorMsg(Class<?> objClass, Long objId) {
    return String.format("Object %s (id=%d) is not found", objClass.getName(), objId);
  }

}
