/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * ConnectTimedOutException
 */
public class ConnectTimedOutException extends LinkCreationException {
	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	public ConnectTimedOutException() {
		super();

	}

	public ConnectTimedOutException(String message) {
		super(message);
	}

	public ConnectTimedOutException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectTimedOutException(Throwable cause) {
		super(cause);
	}

}
