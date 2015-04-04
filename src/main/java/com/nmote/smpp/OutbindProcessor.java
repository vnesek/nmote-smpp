/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OutbindProcessor processes outbind requests. It replies with a bind() call on
 * ESMESession. If used with SMSCSession it has no effects. Add it to an
 * ESMESession if you want to support outbind SMPP operation.
 *
 * @author Vjekoslav Nesek
 */
public class OutbindProcessor implements Processor {

	private static Logger log = LoggerFactory.getLogger(OutbindProcessor.class);

	/**
	 * @see com.nmote.smpp.Processor#process(Command, Session)
	 */
	@Override
	public void process(Command command, final Session session) {
		AbstractPDU request = command.getRequest();
		if (request.getCommandId() == PDU.OUTBIND && session instanceof ESMESession) {
			session.setState(Session.OUTBOUND);
			command.respond();

			// Binding have to be executed in a separate thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						((ESMESession) session).bind();
					} catch (SessionException e) {
						log.error("Binding failed", e);
					}
				}
			}).start();
		}
	}
}
