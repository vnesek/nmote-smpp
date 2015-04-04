/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * SessionException is throwed by Session's methods if commands cannot be
 * executed.
 *
 * @author Vjekoslav Nesek
 */
public class SessionException extends Exception {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	public SessionException() {
		super();
	}

	public SessionException(String message) {
		super(message);
	}

	public SessionException(String message, int status) {
		super(message);
		this.status = status;
	}

	@Override
	public String getMessage() {
		String result = super.getMessage();
		if (status != 0) {
			if (result != null) {
				result += " " + ESMEStatus.toString(status) + " (" + status + ")";
			} else {
				result = ESMEStatus.toString(status) + " (" + status + ")";
			}
		}
		return result;
	}

	/**
	 * Returns the status.
	 *
	 * @return int ESME status
	 */
	public int getStatus() {
		return status;
	}

	private int status;
}
