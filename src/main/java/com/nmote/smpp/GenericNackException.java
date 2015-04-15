/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * GenericNackException is thrown when GenerickNackPDU is received instead of
 * expected PDU type.
 *
 * @author Vjekoslav Nesek
 */
public class GenericNackException extends SessionException {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	public GenericNackException() {
		super();
	}

	public GenericNackException(int status) {
		super(null, status);
	}
}