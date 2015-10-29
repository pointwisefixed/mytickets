package com.mytickets.rest.service.exceptions;

public class HoldExpiredException extends Exception {

	private static final long serialVersionUID = 0L;

	public HoldExpiredException(String message) {
		super(message);
	}

}
