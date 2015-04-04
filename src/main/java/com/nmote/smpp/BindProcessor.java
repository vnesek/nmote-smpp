/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * BindProcessor is a processor that handles binding requests. Bindings are
 * extracted from bind_receiver, bind_transmitter and bind_transceiver PDUs and
 * checked against configurated BindingAuthorizer. If there is no
 * BindingAuthorizer all sessions are established regardless of supplied
 * credentials.
 *
 * @see com.nmote.smpp.Binding
 * @see com.nmote.smpp.BindTransceiverPDU
 * @see com.nmote.smpp.BindReceiverPDU
 * @see com.nmote.smpp.BindTransmitterPDU
 * @author Vjekoslav Nesek
 */
public class BindProcessor implements Processor {

	/**
	 * Instantiates BindProcessor without setting BindingAuthorizer. Usable for
	 * subclasses that override getAuthorizer method.
	 */
	public BindProcessor() {
	}

	/**
	 * Instantiates new BindProcessor and sets authorizer.
	 *
	 * @param authorizer
	 */
	public BindProcessor(BindingAuthorizer authorizer) {
		setAuthorizer(authorizer);
	}

	/**
	 * Returns BindingAuthorizer that is used for authorizing Bindings.
	 * Subclasses can safely override this method.
	 *
	 * @return BindingAuthorizer
	 */
	public BindingAuthorizer getAuthorizer() {
		return authorizer;
	}

	public void handleBindReceiver(Command cmd, Session session) {
		BindReceiverPDU bind = (BindReceiverPDU) cmd.getRequest();
		Binding b = new Binding(bind.getSystemId(), bind.getSystemType(), bind.getPassword(), true, false);
		int status = auth(b, session);
		if (status == ESMEStatus.OK) {
			session.setState(Session.BOUND_RX);
		}
		AbstractPDU resp = bind.createResponse();
		resp.setStatus(status);
		cmd.respond(resp);
	}

	public void handleBindTransceiver(Command cmd, Session session) {
		BindTransceiverPDU bind = (BindTransceiverPDU) cmd.getRequest();
		Binding b = new Binding(bind.getSystemId(), bind.getSystemType(), bind.getPassword(), true, true);
		int status = auth(b, session);
		if (status == ESMEStatus.OK) {
			session.setState(Session.BOUND_TRX);
		}
		AbstractPDU resp = bind.createResponse();
		resp.setStatus(status);
		cmd.respond(resp);
	}

	public void handleBindTransmitter(Command cmd, Session session) {
		BindTransmitterPDU bind = (BindTransmitterPDU) cmd.getRequest();
		Binding b = new Binding(bind.getSystemId(), bind.getSystemType(), bind.getPassword(), false, true);
		int status = auth(b, session);
		if (status == ESMEStatus.OK) {
			session.setState(Session.BOUND_TX);
		}
		AbstractPDU resp = bind.createResponse();
		resp.setStatus(status);
		cmd.respond(resp);
	}

	@Override
	public void process(Command cmd, Session session) {
		AbstractPDU request = cmd.getRequest();
		switch (request.getCommandId()) {
		case PDU.BIND_RECEIVER:
			handleBindReceiver(cmd, session);
			break;

		case PDU.BIND_TRANSMITTER:
			handleBindTransmitter(cmd, session);
			break;

		case PDU.BIND_TRANSCEIVER:
			handleBindTransceiver(cmd, session);
			break;
		}
	}

	/**
	 * Sets the authorizer. If authorizer is null no checking is done and
	 * binding always sucessedes.
	 *
	 * @param authorizer
	 *            The authorizer to set
	 */
	public void setAuthorizer(BindingAuthorizer authorizer) {
		this.authorizer = authorizer;
	}

	private int auth(Binding b, Session session) {
		int result;
		int state = session.getState();
		if (state == Session.OPEN || state == Session.OUTBOUND) {
			BindingAuthorizer a = getAuthorizer();

			if (a == null && session instanceof SMSCSession) {
				a = ((SMSCSession) session).getAuthorizer();
			}

			if (a != null) {
				try {
					a.check(b);
					session.setBinding(b);
					result = ESMEStatus.OK;
				} catch (BindingFailedException e) {
					result = e.getStatus();
					if (result == ESMEStatus.OK) {
						result = ESMEStatus.BINDFAIL;
					}
				}
			} else {
				session.setBinding(b);
				result = ESMEStatus.OK;
			}
		} else {
			result = ESMEStatus.ALYBND;
		}
		return result;
	}

	private BindingAuthorizer authorizer;
}
