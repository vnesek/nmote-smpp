/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * BindingFailedException is throwed when binding process failes for some
 * reason.
 *
 * @author Vjekoslav Nesek
 */
public class BindingFailedException extends SessionException {
	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	/**
	 * Constructor for BindingFailedException.
	 */
	public BindingFailedException() {
	}

	/**
	 * Constructor for BindingFailedException.
	 *
	 * @param status
	 */
	public BindingFailedException(int status) {
		super(null, status);
	}

	/**
	 * Constructor for BindingFailedException.
	 *
	 * @param message
	 */
	public BindingFailedException(String message) {
		super(message);
	}

	/**
	 * Constructor for BindingFailedException.
	 *
	 * @param message
	 * @param status
	 */
	public BindingFailedException(String message, int status) {
		super(message, status);
	}
}