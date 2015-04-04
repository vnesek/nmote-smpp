/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.util;

/**
 * Utility class for converting byte[]-s and Strings to a hex format and back.
 *
 * @author Vjekoslav Nesek
 */
public class Hex {

	public static byte[] decode(String hex) {
		if ((hex.length() % 2) == 1) {
			throw new IllegalArgumentException();
		}
		hex = hex.toLowerCase();
		int len = hex.length() / 2;
		byte[] b = new byte[len];
		for (int i = 0; i < len; ++i) {
			try {
				b[i] = (byte) (Integer.parseInt(hex.substring(i * 2, (i + 1) * 2), 16));
			} catch (Exception e) {
			}
		}
		return b;
	}

	public static String encode(byte[] b) {
		if (b != null) {
			return encode(b, 0, b.length);
		} else {
			return "";
		}
	}

	public static String encode(byte[] b, int offset, int size) {
		StringBuffer result = new StringBuffer();
		int m = offset + size;
		for (int i = offset; i < m; ++i) {
			result.append(toHexDigit((0xf0 & b[i]) >> 4));
			result.append(toHexDigit((0x0f & b[i]) >> 0));
		}
		return result.toString();
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
