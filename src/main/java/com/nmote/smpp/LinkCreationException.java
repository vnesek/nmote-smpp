/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * LinkCreationException is throwed when LinkFactory cannot instantiate Link
 * instance.
 *
 * @author Vjekoslav Nesek
 */
public class LinkCreationException extends Exception {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	public LinkCreationException() {
		super();
	}

	public LinkCreationException(String message) {
		super(message);
	}

	public LinkCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public LinkCreationException(Throwable cause) {
		super(cause);
	}
}
