/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.util;

import java.util.Properties;

import com.nmote.smpp.Binding;
import com.nmote.smpp.ESMESession;
import com.nmote.smpp.SMPPAddress;
import com.nmote.smpp.TCPLinkFactory;

/**
 * PropertiesSessionConfigurator push.smpp.autoEnquireLink =
 * push.smpp.autoConnect = push.smpp.connectRetry = push.smpp.addressRange =
 * push.smpp.maxConcurrent =
 */
public class PropertiesSessionConfigurator {

	public static ESMESession createESMESession(String id, Properties p) {
		int version;
		try {
			version = Integer.parseInt(getOptional(p, id + ".smpp.version", "34"), 16);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("invalid number format: " + e);
		}
		Binding binding = new Binding(getMandatory(p, id + ".smpp.sysId"), getMandatory(p, id + ".smpp.sysType"),
				getMandatory(p, id + ".smpp.password"), "true".equals(getOptional(p, id + ".smpp.receiver", "true")),
				"true".equals(getOptional(p, id + ".smpp.transmitter", "true")));
		String adr = getOptional(p, id + ".smpp.addressRange", null);
		if (adr != null) {
			binding.setAddressRange(new SMPPAddress(adr, 0, 0));
		}
		if (!binding.isReceiver() && !binding.isTransmitter()) {
			throw new IllegalArgumentException("session isn't receiver nor transmitter");
		}
		TCPLinkFactory link;
		link = new TCPLinkFactory(getMandatory(p, id + ".smpp.host"), Integer.parseInt(getMandatory(p, id
				+ ".smpp.port")));

		ESMESession session = new ESMESession(link);
		session.setBinding(binding);
		session.setAutoConnect("true".equals(getOptional(p, id + ".smpp.autoConnect",
				Boolean.toString(session.isAutoConnect()))));
		session.setConnectRetry(Long.parseLong(getOptional(p, id + ".smpp.connectRetry",
				Long.toString(session.getConnectRetry()))));
		session.setMaxConcurrentCommands(Integer.parseInt(getOptional(p, id + ".smpp.maxConcurrentCommands",
				Integer.toString(session.getMaxConcurrentCommands()))));
		session.setLocalVersion(version);
		return session;
	}

	private static String getMandatory(Properties properties, String name) {
		String r = (String) properties.get(name);
		if (r == null) {
			throw new IllegalArgumentException("missing mandatory parameter: " + name);
		}
		return r;
	}

	private static String getOptional(Properties properties, String name, String def) {
		String r = (String) properties.get(name);
		if (r == null) {
			r = def;
		}
		return r;
	}
}
