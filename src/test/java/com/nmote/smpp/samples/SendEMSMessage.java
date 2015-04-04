/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.samples;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmote.ems.Style;
import com.nmote.ems.UserDataBuilder;
import com.nmote.smpp.Binding;
import com.nmote.smpp.Command;
import com.nmote.smpp.ESMESession;
import com.nmote.smpp.LinkFactory;
import com.nmote.smpp.SMPPAddress;
import com.nmote.smpp.SessionException;
import com.nmote.smpp.SubmitSmPDU;
import com.nmote.smpp.SubmitSmRespPDU;
import com.nmote.smpp.TCPLinkFactory;
import com.nmote.smpp.util.DebugSessionStateListener;

/**
 * This example demonstrates setting up a link and a Session and issuing
 * submit_sm Command. Sample will deliver EMS encoded message to a SMSC. EMS
 * message is generated with UserDataBuilder helper class.
 *
 * @see com.nmote.smpp.LinkFactory
 * @see com.nmote.smpp.Session
 * @see com.nmote.smpp.Command
 * @author Vjekoslav Nesek
 */
public class SendEMSMessage {

	public static void main(String[] args) throws Exception {
		Samples.init();
		Logger log = LoggerFactory.getLogger(SendEMSMessage.class);

		// Create a link factory to MC
		LinkFactory linkFactory = new TCPLinkFactory("localhost", 2775);

		// Create a session
		ESMESession s = new ESMESession(linkFactory);
		// s.setTimeout(20000);
		s.addSessionStateListener(new DebugSessionStateListener());

		// Bind as a transmitter
		Binding b = new Binding();
		b.setSystemId("user");
		b.setPassword("password");
		b.setSystemType("sysType");
		b.setTransmitter(true);
		b.setReceiver(false);
		s.setBinding(b);
		s.open();
		s.bind();

		// Build ESM message data
		UserDataBuilder builder = new UserDataBuilder().appendText("Some text").setBold(true).appendText(" Bold text ")
				.setBold(false).setSize(Style.LARGE).appendText(" Large text ").setSize(Style.NORMAL).setItalic(true)
				.appendText(" and sound for the end").appendPredefinedSound(0).appendPredefinedSound(1)
				.appendPredefinedSound(2).appendPredefinedSound(3).appendPredefinedSound(4).appendPredefinedSound(5)
				.appendPredefinedSound(6).appendPredefinedSound(7).appendPredefinedSound(8).appendPredefinedSound(9);
		List<byte[]> data = builder.toByteArrayList();

		// Send messages
		try {
			for (byte[] payload : data) {

				// Create a submit_sm command
				SubmitSmPDU req = new SubmitSmPDU();
				req.setDestAddr(new SMPPAddress("438893248", SMPPAddress.TON_INTERNATIONAL, SMPPAddress.NPI_ISDN));
				req.setSourceAddr(new SMPPAddress("438876544", SMPPAddress.TON_INTERNATIONAL, SMPPAddress.NPI_ISDN));
				req.setDataCoding(0xf5);
				req.setEsmClass(0x40);
				req.setShortMessage(payload);

				Command submit = new Command(req);

				// Execute command
				s.execute(submit);
				SubmitSmRespPDU resp = (SubmitSmRespPDU) submit.getResponse();
				log.info("Status " + resp.getStatus());
				log.info("Message ID " + resp.getMessageId());
			}
		} catch (SessionException se) {
			log.error("Error " + se.toString());
		}

		// Unbind session
		s.unbind();

		// Close session
		s.close();
	}
}