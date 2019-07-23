package org.tc.demo.utils;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus( value = HttpStatus.CONFLICT )
public class AppLogicException extends ServiceException {

	public AppLogicException(String msg, Throwable ex) {
		super(msg, ex);
	}
	
	public AppLogicException(String msg) {
		super(msg);
	}
}
