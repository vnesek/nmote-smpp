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
	public static final int CHARSET_IA5 = 1;
	public static final int CHARSET_8BIT = 2;
	public static final int CHARSET_LATIN1 = 3;
	public static final int CHARSET_8BIT_ALT = 4;
	public static final int CHARSET_JIS = 5;
	public static final int CHARSET_CYRILIC = 6;
	public static final int CHARSET_LATIN_HEBREW = 7;
	public static final int CHARSET_UCS2 = 8;
	public static final int CHARSET_PICTOGRAM = 9;
	public static final int CHARSET_ISO_2022_JP = 10;
	public static final int CHARSET_EXTENDED_KANJI = 13;
	public static final int CHARSET_KS_C_5601 = 14;

	public static int decodeDCS(int dcs) {
		return dcs & 0x0F;
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
				result = s.getBytes("utf-16be");
				break;
			case CHARSET_CYRILIC:
				result = s.getBytes("iso-8859-5");
				break;
			case CHARSET_LATIN_HEBREW:
				result = s.getBytes("iso-8859-8");
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
				result = new String(data, "utf-16be");
				break;
			case CHARSET_CYRILIC:
				result = new String(data, "iso-8859-5");
				break;
			case CHARSET_LATIN_HEBREW:
				result = new String(data, "iso-8859-8");
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
