package com.natymorgs.backEndSpring.services.exceptions;

public class ObjecNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ObjecNotFoundException(String msg) {
		super(msg);
	}
	
	public ObjecNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
