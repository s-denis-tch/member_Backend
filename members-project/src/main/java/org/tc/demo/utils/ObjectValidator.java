package org.tc.demo.utils;

public class ObjectValidator {

  public static <T> T checkFound(T object, String msgIfNull) {
    if(object == null) {
      throw new NotFoundException(msgIfNull);
    }
    
    return object;
  }
}
