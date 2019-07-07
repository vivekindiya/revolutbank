package com.revolut.fundtransfer.exception;
//TODO implement error codes
public class HSQLDBException extends Exception {

	private static final long serialVersionUID = 1L;

	public HSQLDBException(String msg) {
		super(msg);
	}

	public HSQLDBException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
