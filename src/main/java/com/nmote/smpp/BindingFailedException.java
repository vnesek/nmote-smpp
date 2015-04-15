/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * BindingFailedException is thrown when binding process fails for some
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
	 *            SMPP status
	 */
	public BindingFailedException(int status) {
		super(null, status);
	}

	/**
	 * Constructor for BindingFailedException.
	 *
	 * @param message
	 *            error message
	 */
	public BindingFailedException(String message) {
		super(message);
	}

	/**
	 * Constructor for BindingFailedException.
	 *
	 * @param message
	 *            error message
	 * @param status
	 *            SMPP status
	 */
	public BindingFailedException(String message, int status) {
		super(message, status);
	}
}