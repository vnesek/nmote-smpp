/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.util;

import java.io.UnsupportedEncodingException;

import com.nmote.util.Hex;

/**
 * StringUtils complement Jakarte commons String utils with a few methods.
 *
 * @author Vjekoslav Nesek
 */
public class StringUtils {

	/**
	 * Checks if byte array contains only ASCII printable characters.
	 *
	 * @param s
	 *            array of bytes containing potential printable string.
	 * @return true if s is printable.
	 */
	public static boolean containsOnlyPrintableAscii(byte[] s) {
		boolean result = true;
		if (s != null) {
			for (int i = 0; i < s.length; ++i) {
				int c = s[i];
				if (c < 31 || c > 127) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	public static String toStringOrHex(byte[] s) {
		StringBuffer result = new StringBuffer(256);
		if (s != null) {
			for (int i = 0; i < s.length; ++i) {
				char c = (char) s[i];
				if (Character.isLetter(c)) {
					result.append("\\x");
					result.append(toHexDigit((c >> 4) & 0xf));
					result.append(toHexDigit(c & 0xf));
				} else {
					result.append(c);
				}
			}
		}
		return result.toString();
	}

	/**
	 * If byte array contains only printable characters according to
	 * <code>containsOnlyPrintableAscii</code> returns s converted to ASCII
	 * surrounded with quotes. Otherwise returns hex encoded string prefixed
	 * with text <code>binary(</code>.
	 *
	 * @param s
	 *            byte array to encode
	 * @return print safe encoded s
	 */
	public static String toStringOrHex2(byte[] s) {
		String result;
		if (s != null) {
			if (containsOnlyPrintableAscii(s)) {
				try {
					result = "\"" + new String(s, "iso-8859-1") + "\"";
				} catch (UnsupportedEncodingException e) {
					result = null;
					assert false;
				}
			} else {
				result = "Binary(" + Hex.encode(s) + ")";
			}
		} else {
			result = null;
		}
		return result;
	}

	private static char toHexDigit(int d) {
		switch (d) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return (char) ('0' + d);
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			return (char) ('a' + d - 10);
		default:
			throw new IllegalArgumentException("Not a hex digit: " + d);
		}
	}
}
