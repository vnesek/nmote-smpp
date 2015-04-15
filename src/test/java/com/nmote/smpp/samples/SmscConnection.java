/*
 * Copyright (c) Nmote d.o.o. 2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.samples;

import java.util.Collections;

import com.nmote.smpp.Binding;
import com.nmote.smpp.Command;
import com.nmote.smpp.DeliverSmPDU;
import com.nmote.smpp.DispatchProcessor;
import com.nmote.smpp.ESMESession;
import com.nmote.smpp.ESMEStatus;
import com.nmote.smpp.EnquireLinkProcessor;
import com.nmote.smpp.GenericNackProcessor;
import com.nmote.smpp.LinkCreationException;
import com.nmote.smpp.PDU;
import com.nmote.smpp.Parameter;
import com.nmote.smpp.Processor;
import com.nmote.smpp.SMPPAddress;
import com.nmote.smpp.Session;
import com.nmote.smpp.SessionException;
import com.nmote.smpp.SubmitSmPDU;
import com.nmote.smpp.SubmitSmRespPDU;
import com.nmote.smpp.TCPLinkFactory;
import com.nmote.smpp.Tag;
import com.nmote.smpp.util.DCS;
import com.nmote.smpp.util.MessageState;

/**
 * SMSC connection abstraction, suitable for use with Spring or similar IoC
 * container. Call send() to send message. Override receive() to handle received
 * messages and receipt() to handle message delivery/failure receipts.
 *
 * Session is created by calling open() and closed by calling close(). It's
 * declared as version 3.4 and uses transceiver mode to send and receive
 * messages over single socket.
 *
 * Use set*() methods to set connection/binding parameters.
 */
public class SmscConnection {

	public static void main(String[] args) throws Exception {
		SmscConnection c = new SmscConnection();
		c.setHostPort("127.0.0.1:2775");
		c.setSystemId("some-system-id");
		c.setPassword("some-password");

		// Open session
		c.open();

		// Send test message
		try {
			String messageId = c.send("+9876543210", "Test message");
			System.out.println("SENT " + messageId);
		} catch (SessionException e){
			System.out.println("SENDING FAILED " + e);
		}

		Thread.sleep(100000);

		// Unbind session
		c.close();
	}

	public SmscConnection() {
		processor.setDefaultProcessor(GenericNackProcessor.INSTANCE);
		processor.setProcessor(PDU.ENQUIRE_LINK, EnquireLinkProcessor.INSTANCE);
		processor.setProcessor(PDU.DELIVER_SM, new Processor() {
			@Override
			public void process(Command command, Session session) {
				DeliverSmPDU pdu = (DeliverSmPDU) command.getRequest();

				// Check if this is delivery receipt
				String text = DCS.toUnicode(pdu.getShortMessage(), pdu.getDataCoding());
				if (pdu.getParameter(Tag.RECEIPTED_MESSAGE_ID) == null) {
					// Normal message
					received("+" + pdu.getSourceAddr().getPhoneNumber(), text);
				} else {
					// Delivery receipt
					String receiptedMessageId = pdu.getParameter(Tag.RECEIPTED_MESSAGE_ID).getString();
					int messageState = pdu.getParameter(Tag.MESSAGE_STATE).getInt();
					receipt(receiptedMessageId, messageState == MessageState.DELIVERED);
				}

				// Ok... respond now
				command.respond(pdu.createResponse());
			}
		});
	}

	public synchronized void close() throws SessionException {
		try {
			session.unbind(5000);
		} finally {
			session.close();
		}
	}

	public int getShortCodeTon() {
		return shortCode.getTon();
	}

	public synchronized void open() throws LinkCreationException, SessionException {
		if (session == null) {
			// Create session
			session = new ESMESession(linkFactory);
			session.setBinding(binding);
			session.setAutoConnect(true);
			session.setAutoEnquireLink(60000);
			session.setLocalVersion(34);
			session.setIncomingProcessor(processor);
		}

		session.open();
		session.bind();
	}

	/**
	 * Sends a message.
	 *
	 * @param to
	 *            receipient in international phone number format
	 * @param text
	 *            message text (unicode)
	 * @return message id
	 * @throws SessionException
	 *             if message sending failed
	 */
	public String send(String to, String text) throws SessionException {
		if (!to.startsWith("+")) {
			throw new IllegalArgumentException("expected international phone number with '+' prefix: " + to);
		}
		if (session == null) {
			throw new IllegalStateException("open() connection before send()ing");
		}

		SubmitSmPDU req = new SubmitSmPDU();
		req.setDestAddr(new SMPPAddress(to.substring(1), SMPPAddress.TON_INTERNATIONAL, SMPPAddress.NPI_ISDN));
		req.setSourceAddr(shortCode);
		req.setDataCoding(DCS.CHARSET_UCS2);
		req.setRegisteredDelivery(1);
		byte[] payload = DCS.toGSM(text, DCS.CHARSET_UCS2);
		req.setParameters(Collections.singletonList(new Parameter(Tag.MESSAGE_PAYLOAD, payload)));

		Command submit = new Command(req);

		// Execute command
		session.execute(submit);
		SubmitSmRespPDU resp = (SubmitSmRespPDU) submit.getResponse();

		// Check response status
		if (resp.getStatus() != ESMEStatus.OK) {
			throw new SessionException("message sending failed", resp.getStatus());
		}

		// Return message id
		return resp.getMessageId();
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public void setHostPort(String hostPort) {
		int colon = hostPort.indexOf(':');
		if (colon != -1) {
			linkFactory.setHost(hostPort.substring(0, colon));
			linkFactory.setPort(Integer.parseInt(hostPort.substring(colon + 1)));
		} else {
			linkFactory.setHost(hostPort);
		}
	}

	public void setNpi(int npi) {
		shortCode.setNpi(npi);
	}

	public void setPassword(String password) {
		binding.setPassword(password);
	}

	public void setShortCode(String address) {
		shortCode.setAddress(address);
	}

	public void setShortCodeTon(int ton) {
		shortCode.setTon(ton);
	}

	public void setSystemId(String systemId) {
		binding.setSystemId(systemId);
	}

	public void setSystemType(String systemType) {
		binding.setSystemType(systemType);
	}

	/**
	 * Handles delivery/failure receipt for sent message.
	 *
	 * @param messageId
	 *            message id of previously sent message
	 * @param delivered
	 *            true if message was delivered, false otherwise
	 */
	protected void receipt(String messageId, boolean delivered) {
		// TODO override me
		System.out.println((delivered ? "DELIVERED " : "FAILED ") + messageId);
	}

	/**
	 * Handles received message.
	 *
	 * @param from
	 *            sender's phone number in international format
	 * @param text
	 *            message text
	 */
	protected void received(String from, String text) {
		// TODO override me
		System.out.println("RECEIVED " + from + ": " + text);
	}

	private Binding binding = new Binding();
	private TCPLinkFactory linkFactory = new TCPLinkFactory();
	private DispatchProcessor processor = new DispatchProcessor();
	private ESMESession session;
	private SMPPAddress shortCode = new SMPPAddress("test", SMPPAddress.TON_ALPHANUMERIC, SMPPAddress.NPI_UNKNOWN);
}