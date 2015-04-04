/* 
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * Unathorized use of this file is prohibited by law.
 * See LICENSE.txt for licensing information.
 */
 
package com.nmote.smpp.samples;

import com.nmote.smpp.AbstractPDU;
import com.nmote.smpp.Binding;
import com.nmote.smpp.Command;
import com.nmote.smpp.DeliverSmPDU;
import com.nmote.smpp.DeliverSmRespPDU;
import com.nmote.smpp.DispatchProcessor;
import com.nmote.smpp.ESMESession;
import com.nmote.smpp.ESMEStatus;
import com.nmote.smpp.LinkFactory;
import com.nmote.smpp.PDU;
import com.nmote.smpp.Processor;
import com.nmote.smpp.Session;
import com.nmote.smpp.TCPLinkFactory;
import com.nmote.smpp.util.DCS;
import com.nmote.smpp.util.DebugSessionStateListener;

/**
 * This example demonstrates setting up a link and a
 * Session and receiving deliver_sm Commands. Sample will receive
 * SMS messages from a SMSC.
 * 
 * @see com.nmote.smpp.LinkFactory
 * @see com.nmote.smpp.Session
 * @see com.nmote.smpp.Command
 * @author Vjekoslav Nesek
 */
public class ReceiveMessage {

	public static void main(String[] args) throws Exception {
		Samples.init();
				
		// Create a link factory to MC
		LinkFactory linkFactory = new TCPLinkFactory("localhost", 2775);
		
		// Create a session
		ESMESession s = new ESMESession(linkFactory);
//		s.setTimeout(20000);
		s.addSessionStateListener(new DebugSessionStateListener());
		
		// Install a processor that processes submit requests
		// Delegate all other commands to a standard session processor
		DispatchProcessor dp = new DispatchProcessor(s.getIncomingProcessor());
		dp.setProcessor(PDU.DELIVER_SM, new Processor() {
			public void process(Command cmd, Session session) {
				AbstractPDU request = cmd.getRequest();
				if (request.getCommandId() == PDU.DELIVER_SM) {
					DeliverSmPDU submit = (DeliverSmPDU) request;
					System.out.println("Received SMS: " + submit.getSourceAddr() + " -> " + submit.getDestAddr() + " " + DCS.toUnicode(submit.getShortMessage()));
					DeliverSmRespPDU resp = (DeliverSmRespPDU) request.createResponse();
					resp.setStatus(ESMEStatus.OK);
					cmd.respond(resp);
				}
			}
		});
		s.setIncomingProcessor(dp);
			
		// Bind as a receiver
		Binding b = new Binding();
		b.setSystemId("pero");
		b.setPassword("test");
		b.setSystemType("smpp");
		b.setReceiver(true);
		s.setBinding(b);
		s.open();
		s.bind();
		
		// Receive messages for 100 seconds
		Thread.sleep(100 * 1000);
		
		// Unbind session
		s.unbind();
		
		// Close session
		s.close();
	}
}