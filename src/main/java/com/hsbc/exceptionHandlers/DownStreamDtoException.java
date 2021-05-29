package com.hsbc.exceptionHandlers;

public class DownStreamDtoException extends RuntimeException {

	public DownStreamDtoException(int id) {
	    super("Could not find DownStreamDto " + id);
	  }
	}
