/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default SMPPAddress converter implementation
 */
public class DefaultSMPPAddressConverter implements SMPPAddressConverter, Serializable {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	private static int ABBR_LEN = getPropertyInt("abbr_len", 5);
	private static int ABBR_NPI = getPropertyInt("abbr_npi", SMPPAddress.NPI_UNKNOWN);
	private static int ABBR_TON = getPropertyInt("abbr_ton", SMPPAddress.TON_ABBREVIATED);
	private static int ALPHA_NPI = getPropertyInt("alpha_npi", SMPPAddress.NPI_UNKNOWN);
	private static int ALPHA_TON = getPropertyInt("alpha_ton", SMPPAddress.TON_ALPHANUMERIC);
	private static int INT_NPI = getPropertyInt("int_npi", SMPPAddress.NPI_ISDN);
	private static String INT_PREFIX = getPropertyString("int_prefix", "+");
	private static String INT_PREFIX_ZERO = getPropertyString("int_prefix", "00");
	private static int INT_TON = getPropertyInt("int_ton", SMPPAddress.TON_INTERNATIONAL);
	private static int NAT_NPI = getPropertyInt("nat_npi", SMPPAddress.NPI_ISDN);
	private static String NAT_PREFIX_ZERO = getPropertyString("nat_prefix", "0");
	private static int NAT_TON = getPropertyInt("nat_ton", SMPPAddress.TON_NATIONAL);

	private static final char[] PHONE_DIGITS = "+0123456789*#ab".toCharArray();
	private static Logger log = LoggerFactory.getLogger(DefaultSMPPAddressConverter.class);

	private static int getPropertyInt(String key, int def) {
		key = "com.nmote.smpp." + key;
		String s = System.getProperty(key);
		int result = def;
		if (s != null) {
			try {
				result = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				log.warn("Expected integer in system property " + key + ": " + s);
			}
		}
		// System.out.println("XX " + key + "=" + result + "/" + def + " s=" +
		// s);
		return result;
	}

	private static String getPropertyString(String key, String def) {
		key = "com.nmote.smpp." + key;
		String result = System.getProperty(key, def);
		return result;
	}

	@Override
	public SMPPAddress toSMPPAddress(String address) {
		int ton = SMPPAddress.TON_UNKNOWN;
		int npi = SMPPAddress.NPI_UNKNOWN;
		int len = address.length();
		if (len > 0) {
			boolean allPhoneDigits = StringUtils.containsOnly(address, PHONE_DIGITS);

			if (allPhoneDigits) {
				if (len <= ABBR_LEN) {
					ton = ABBR_TON;
					npi = ABBR_NPI;
				} else if (address.startsWith(INT_PREFIX)) {
					ton = INT_TON;
					npi = INT_NPI;
					address = address.substring(INT_PREFIX.length());
				} else if (address.startsWith(INT_PREFIX_ZERO)) {
					ton = INT_TON;
					npi = INT_NPI;
					address = address.substring(INT_PREFIX_ZERO.length());
				} else if (address.startsWith(NAT_PREFIX_ZERO)) {
					ton = NAT_TON;
					npi = NAT_NPI;
					address = address.substring(NAT_PREFIX_ZERO.length());
				}
			} else {
				ton = ALPHA_TON;
				npi = ALPHA_NPI;
			}
		}
		return new SMPPAddress(address, ton, npi);
	}

	@Override
	public String toString(SMPPAddress adr) {
		return adr.toString();
	}
}
