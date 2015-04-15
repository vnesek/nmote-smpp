/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * Thrown when command is executed on a closed session.
 */
public class SessionClosedException extends SessionException {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	public SessionClosedException() {
		super();
	}

	public SessionClosedException(String message) {
		super(message);
	}

	public SessionClosedException(String message, int status) {
		super(message, status);
	}

}
