package com.stephenschafer.ami.service;

import java.io.Serializable;

public class EmailConfigException extends Exception implements Serializable {
	private static final long serialVersionUID = 1L;

	public EmailConfigException(final String message) {
		super(message);
	}

	public EmailConfigException(final String message, final Exception cause) {
		super(message, cause);
	}
}