/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.util;

import java.io.UnsupportedEncodingException;

import com.nmote.util.Hex;

/**
 * DCS (Data Coding Scheme) is utility class for converting between character
 * strings used in SMS messages and Unicode Java Strings. GSM, 8BIT and UCS2
 * encodings can be converted to and from Java Unicode.
 */
public class DCS {

	public static final int CHARSET_GSM = 0;
	public static final int CHARSET_8BIT = 1;
	public static final int CHARSET_UCS2 = 2;
	public static final int CHARSET_RESERVED = 3;

	public static int decodeDCS(int dcs) {
		int result = CHARSET_8BIT;
		if (dcs == 0x00) {
			result = CHARSET_GSM;
		} else if ((dcs & 0xC0) == 0) {
			// General data coding indication
			switch (dcs & 0x0C) {
			case 0x00:
				result = CHARSET_GSM;
				break;
			case 0x04:
				result = CHARSET_8BIT;
				break;
			case 0x08:
				result = CHARSET_UCS2;
				break;
			case 0x0C:
				result = CHARSET_RESERVED;
				break;
			}
		} else if ((dcs & 0xF0) == 0xF0) {
			// Data coding + message class
			switch (dcs & 0x02) {
			case 0x00:
				result = CHARSET_GSM;
				break;
			case 0x02:
				result = CHARSET_8BIT;
				break;
			}
		}

		return result;
	}

	/**
	 * Converts Java String into GSM bytes according to default (=0) DCS.
	 *
	 * @param s
	 *            Unicode String
	 * @return GSM characters
	 */
	public static byte[] toGSM(String s) {
		return toGSM(s, 0);
	}

	/**
	 * Converts Java String into GSM bytes according to passed <code>dcs</code>.
	 *
	 * @param s
	 *            Unicode String
	 * @param dcs
	 *            data coding scheme
	 * @return GSM characters
	 */
	public static byte[] toGSM(String s, int dcs) {
		byte[] result;
		try {
			switch (decodeDCS(dcs)) {
			case CHARSET_GSM:
				result = GSMCharset.toGSM(s);
				break;
			case CHARSET_UCS2:
				result = s.getBytes("ucs2");
				break;
			default:
				result = s.getBytes("iso-8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			assert false : "Unsupported encoding" + e;
			result = null;
		}
		return result;
	}

	/**
	 * Converts characters represented as byte array into Java String. Default
	 * DCS (=0) is assumed
	 *
	 * @param data
	 *            characters from SMS message
	 * @return Unicode String
	 */
	public static String toUnicode(byte[] data) {
		return toUnicode(data, 0);
	}

	/**
	 * Converts characters represented as byte array into Java String.
	 *
	 * @param data
	 *            characters from SMS message
	 * @param dcs
	 *            data coding scheme
	 * @return Unicode String
	 */
	public static String toUnicode(byte[] data, int dcs) {
		String result;
		try {
			switch (decodeDCS(dcs)) {
			case CHARSET_GSM:
				result = GSMCharset.toUnicode(data);
				break;
			case CHARSET_UCS2:
				result = new String(data, "utf-16");
				break;
			default:
				result = new String(data, "iso-8859-1");
			}
		} catch (Throwable t) {
			result = Hex.encode(data);
		}
		return result;
	}
}
