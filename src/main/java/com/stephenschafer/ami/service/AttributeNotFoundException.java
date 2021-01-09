package com.stephenschafer.ami.service;

public class AttributeNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public AttributeNotFoundException(final String message) {
		super(message);
	}
}
