/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * EnquireLinkProcessor processes enquire link requests and responds with
 * enquire_link_resp PDUs. Add an instance of EnquireLink processor to Session
 * if you want to process enquire_link requests.
 *
 * @see com.nmote.smpp.EnquireLinkPDU
 * @see com.nmote.smpp.EnquireLinkRespPDU
 * @author Vjekoslav Nesek
 */
public class EnquireLinkProcessor implements Processor {

	public static final Processor INSTANCE = new EnquireLinkProcessor();

	/**
	 * If command is an enquire_link responds with an enquire_link_response.
	 *
	 * @see com.nmote.smpp.Processor#process(Command, Session)
	 */
	@Override
	public void process(Command command, Session session) {
		AbstractPDU request = command.getRequest();
		if (request.getCommandId() == PDU.ENQUIRE_LINK) {
			command.respond(request.createResponse());
		}
	}
}
