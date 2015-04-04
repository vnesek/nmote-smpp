/*
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * Unathorized use of this file is prohibited by law.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.samples;

import com.nmote.smpp.Binding;
import com.nmote.smpp.Command;
import com.nmote.smpp.ESMESession;
import com.nmote.smpp.LinkFactory;
import com.nmote.smpp.SMPPAddress;
import com.nmote.smpp.SubmitSmPDU;
import com.nmote.smpp.SubmitSmRespPDU;
import com.nmote.smpp.TCPLinkFactory;
import com.nmote.smpp.util.CommandStateAdapter;
import com.nmote.smpp.util.DCS;
import com.nmote.smpp.util.DebugSessionStateListener;

/**
 * This example demonstrates setting up a link and a
 * Session and issuing submit_sm Command asynchronously.
 * Also usage of CommandStateAdapter is demonstrated.
 * Sample will deliver SMS to a SMSC.
 *
 * @see com.nmote.smpp.LinkFactory
 * @see com.nmote.smpp.Session
 * @see com.nmote.smpp.Command
 * @see com.nmote.smpp.util.CommandStateAdapter
 * @author Vjekoslav Nesek
 */
public class AsyncSendMessage {

	public static void main(String[] args) throws Exception {
		Samples.init();

		// Create a link factory to MC
		LinkFactory linkFactory = new TCPLinkFactory("localhost", 2775);

		// Create a session
		ESMESession s = new ESMESession(linkFactory);
//		s.setTimeout(20000);
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

		// Set a CommandStateListener that will be called when
		// command completes execution
		submit.setCommandStateListener(new CommandStateAdapter() {
			public void executed(Command cmd) {
				SubmitSmRespPDU resp = (SubmitSmRespPDU) cmd.getResponse();
				System.out.println("Status " + resp.getStatus());
				System.out.println("Message ID " + resp.getMessageId());
			}
		});

		// Execute command asynchronously
		s.executeAsync(submit);

		// Unbind session
		s.unbind();

		// Close session
		s.close();
	}
}