/*
 * Copyright (c) Nmote d.o.o. 2003. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmote.smpp.AbstractPDU;
import com.nmote.smpp.BasicBindingAuthorizer;
import com.nmote.smpp.Command;
import com.nmote.smpp.DeliverSmPDU;
import com.nmote.smpp.DispatchProcessor;
import com.nmote.smpp.Link;
import com.nmote.smpp.PDU;
import com.nmote.smpp.Processor;
import com.nmote.smpp.SMPPAddress;
import com.nmote.smpp.SMSCSession;
import com.nmote.smpp.ServerTCPLinkFactory;
import com.nmote.smpp.Session;
import com.nmote.smpp.SubmitSmPDU;
import com.nmote.smpp.SubmitSmRespPDU;
import com.nmote.smpp.util.DCS;

/**
 * This example demonstrates creating server for SMPP communication. Servers are
 * created by creating a link with ServerTCPLinkFactory and starting
 * SMSCSession-s.
 *
 * @author Vjekoslav Nesek
 */
public class Server {

	public static void main(String[] args) throws Exception {
		int runs;
		try {
			runs = Integer.parseInt(args[0]);
		} catch (Exception e) {
			runs = 100;
		}

		Samples.init();
		final Logger log = LoggerFactory.getLogger(SendMessage.class);

		// Create a server
		ServerTCPLinkFactory server = new ServerTCPLinkFactory();
		server.setLog(log);
		// server.setTimeout(3000);

		// Accept connections
		server.setAccept(true);

		SMSCSession s;
		do {
			Link link = server.createLink();

			// Configure an SMSC session
			s = new SMSCSession(link);
			// s.setExecutor(new NewThreadExecutor());

			// Setup access control
			BasicBindingAuthorizer bbc = new BasicBindingAuthorizer();
			bbc.setPassword("password");
			s.setAuthorizer(bbc);

			// Install a processor that processes submit requests
			// Delegate all other commands to a standard session processor
			DispatchProcessor dp = new DispatchProcessor(s.getIncomingProcessor());
			dp.setProcessor(PDU.SUBMIT_SM, new Processor() {
				public void process(Command cmd, Session session) {
					AbstractPDU request = cmd.getRequest();
					if (request.getCommandId() == PDU.SUBMIT_SM) {
						SubmitSmPDU submit = (SubmitSmPDU) request;
						log.info("Received SMS: " + DCS.toUnicode(submit.getShortMessage()));
						SubmitSmRespPDU resp = (SubmitSmRespPDU) request.createResponse();
						resp.setMessageId("msg001");
						cmd.respond(resp);
					}
				}
			});
			s.setIncomingProcessor(dp);

			// Open session. PDU processing starts
			s.open();

			long started = System.currentTimeMillis();
			for (int i = 0; i < 3; ++i) {
				Thread.sleep(1500);
				DeliverSmPDU deliver = new DeliverSmPDU();
				deliver.setSourceAddr(new SMPPAddress("1", SMPPAddress.TON_INTERNATIONAL, SMPPAddress.NPI_ISDN));
				deliver.setDestAddr(new SMPPAddress("2", SMPPAddress.TON_INTERNATIONAL, SMPPAddress.NPI_ISDN));
				deliver.setShortMessage(DCS.toGSM("Demo text message 9-" + i));

				Command cmd = new Command(deliver);
				s.execute(cmd);
				// cmd.waitForResponse(0);
			}
			long elapsed = System.currentTimeMillis() - started;
			log.info("Elapsed " + elapsed);

			--runs;
		} while (runs > 0);

		// Close session
		s.close();
	}
}
