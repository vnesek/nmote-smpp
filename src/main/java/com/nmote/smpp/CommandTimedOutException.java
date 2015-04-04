/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * CommandTimedOutException is throwed from synchronous Session calss if command
 * execution isn't completed in a specified time.
 *
 * @author Vjekoslav Nesek
 */
public class CommandTimedOutException extends SessionException {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	/**
	 * Constructor for CommandTimedOutException.
	 */
	public CommandTimedOutException() {
		super();
	}

	/**
	 * Constructor for CommandTimedOutException.
	 *
	 * @param message
	 *            error message
	 */
	public CommandTimedOutException(String message) {
		super(message);
	}

}
