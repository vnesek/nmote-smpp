/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * UnbindProcessor.java
 *
 * @author Vjekoslav Nesek
 */
public class UnbindProcessor implements Processor {

	public static final Processor INSTANCE = new UnbindProcessor();

	@Override
	public void process(Command command, Session session) {
		AbstractPDU request = command.getRequest();
		if (request.getCommandId() == PDU.UNBIND) {
			command.respond(request.createResponse());
			session.setState(Session.UNBOUND);
		}
	}
}
