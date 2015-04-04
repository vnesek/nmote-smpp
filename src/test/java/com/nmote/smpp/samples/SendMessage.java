/*
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmote.smpp.Binding;
import com.nmote.smpp.Command;
import com.nmote.smpp.ESMESession;
import com.nmote.smpp.LinkFactory;
import com.nmote.smpp.SMPPAddress;
import com.nmote.smpp.SessionException;
import com.nmote.smpp.SubmitSmPDU;
import com.nmote.smpp.SubmitSmRespPDU;
import com.nmote.smpp.TCPLinkFactory;
import com.nmote.smpp.util.DCS;
import com.nmote.smpp.util.DebugSessionStateListener;

/**
 * This example demonstrates setting up a link and a
 * Session and issuing submit_sm Command. Sample will deliver
 * SMS to a SMSC.
 *
 * @see com.nmote.smpp.LinkFactory
 * @see com.nmote.smpp.Session
 * @see com.nmote.smpp.Command
 * @author Vjekoslav Nesek
 */
public class SendMessage {

	public static void main(String[] args) throws Exception {
		Samples.init();
		Logger log = LoggerFactory.getLogger(SendMessage.class);

		// Create a link factory to MC
		LinkFactory linkFactory = new TCPLinkFactory("localhost", 2775);

		// Create a session
		ESMESession s = new ESMESession(linkFactory);
//		s.setTimeout(20000);
		s.setAutoConnect(true);
		s.addSessionStateListener(new DebugSessionStateListener());

		// Bind as a transmitter
		Binding b = new Binding();
		b.setSystemId("user");
		b.setPassword("password");
		b.setSystemType("sysType");
		b.setTransmitter(true);
		s.setBinding(b);
		s.open();
		s.bind();

		// Create a submit_sm command
		SubmitSmPDU req = new SubmitSmPDU();
		req.setDestAddr(new SMPPAddress("438893248", SMPPAddress.TON_INTERNATIONAL, SMPPAddress.NPI_ISDN));
		req.setSourceAddr(new SMPPAddress("438876544", SMPPAddress.TON_INTERNATIONAL, SMPPAddress.NPI_ISDN));
		req.setShortMessage(DCS.toGSM("This is a sample text"));

		Command submit = new Command(req);

		// Execute command
		try {
			s.execute(submit);
			SubmitSmRespPDU resp = (SubmitSmRespPDU) submit.getResponse();
			log.info("Status " + resp.getStatus());
			log.info("Message ID " + resp.getMessageId());
		} catch (SessionException se) {
			log.error("Error " + se.toString());
		}

		//Thread.sleep(100000);

		// Unbind session
		s.unbind();

		// Close session
		s.close();

		Thread.sleep(100000);
	}
}