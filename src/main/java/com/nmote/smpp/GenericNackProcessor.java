/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * GenericNackProcessor return GenericNackRespPDU on any request.
 *
 * @author Vjekoslav Nesek
 */
public class GenericNackProcessor implements Processor {

	public static final GenericNackProcessor INSTANCE = new GenericNackProcessor();

	/**
	 * @see com.nmote.smpp.Processor#process(com.nmote.smpp.Command,
	 *      com.nmote.smpp.Session)
	 */
	@Override
	public void process(Command command, Session session) {
		command.respond(command.getRequest().createGenericNackResponse());
	}
}
