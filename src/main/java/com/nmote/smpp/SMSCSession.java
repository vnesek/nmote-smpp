/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * SMSCSession is a session running at SMSC (more probably SMPP gateway or
 * proxy) and communicating with ESME.
 *
 * @see com.nmote.smpp.Session
 * @author Vjekoslav Nesek
 */
public class SMSCSession extends Session {

	/**
	 * Constructor for SMSCSession.
	 */
	public SMSCSession() {
		super();
		customizeProcessor();
	}

	/**
	 * Instantiates a new SMSCSession over a link
	 *
	 * @param link
	 */
	public SMSCSession(Link link) {
		super(link);
		customizeProcessor();
	}

	/**
	 * Constructor for Session.
	 */
	public SMSCSession(LinkFactory linkFactory) {
		super(linkFactory);
		customizeProcessor();
	}

	/**
	 * Returns BindingAuthorizer for authorizing bind requests.
	 *
	 * @return BindingAuthorizer
	 */
	public BindingAuthorizer getAuthorizer() {
		return authorizer;
	}

	public void outbind() throws SessionException {
		if (getState() != OPEN) {
			throw new SessionException("Outbind can only be issed while in OPEN state");
		}

		OutbindPDU outbind = new OutbindPDU();
		Command cmd = new Command(outbind);
		execute(cmd);
	}

	/**
	 * @param authorizer
	 */
	public void setAuthorizer(BindingAuthorizer authorizer) {
		this.authorizer = authorizer;
	}

	private void customizeProcessor() {
		DispatchProcessor ss = (DispatchProcessor) getIncomingProcessor();
		BindProcessor bp = new BindProcessor();
		ss.setProcessor(PDU.BIND_RECEIVER, bp);
		ss.setProcessor(PDU.BIND_TRANSMITTER, bp);
		ss.setProcessor(PDU.BIND_TRANSCEIVER, bp);
	}

	private BindingAuthorizer authorizer;

}
