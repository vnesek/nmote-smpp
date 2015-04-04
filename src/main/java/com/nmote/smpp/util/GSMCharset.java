/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.util;

/**
 * GSMCharset contains methods for converting between Java unicode string
 * objects and byte[] encoded as GSM 03.38 default charset.
 *
 * @author Vjekoslav Nesek
 */
public class GSMCharset {

	/**
	 * GSM default alphabet table according to GSM 03.38. See
	 * http://www.unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT
	 */
	private static final char GSM_DEFAULT_CHARSET[] = {
			// 0 '@', '?', '$', '?', '?', '?', '?', '?',
			'@', 163, '$', 165, 232, 233, 249, 236,
			// 8 '?', '?', LF, '?', '?', CR, '?', '?',
			242, 199, 10, 216, 248, 13, 197, 229,
			// 16 'delta', '_', 'phi', 'gamma', 'lambda', 'omega', 'pi', 'psi',
			0x394, '_', 0x3a6, 0x393, 0x39b, 0x3a9, 0x3a0, 0x3a8,
			// 24 'sigma', 'theta', 'xi', 'EXT', '?', '?', '?', '?',
			0x3a3, 0x398, 0x39e, 0xa0, 198, 230, 223, 201,
			// 32 ' ', '!', '"', '#', '?', '%', '&', ''',
			' ', '!', '"', '#', 164, '%', '&', '\'',
			// 40 '(', ')', '*', '+', ',', '-', '.', '/',
			'(', ')', '*', '+', ',', '-', '.', '/',
			// 48 '0', '1', '2', '3', '4', '5', '6', '7',
			'0', '1', '2', '3', '4', '5', '6', '7',
			// 56 '8', '9', ':', ';', '<', '=', '>', '?',
			'8', '9', ':', ';', '<', '=', '>', '?',
			// 64 '?', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			161, 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			// 72 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			// 80 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			// 88 'X', 'Y', 'Z', '?', '?', '?', '?', '?',
			'X', 'Y', 'Z', 196, 214, 209, 220, 167,
			// 96 '?', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			191, 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			// 104 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
			// 112 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			// 120 'x', 'y', 'z', '?', '?', '?', '?', '?',
			'x', 'y', 'z', 228, 246, 241, 252, 224 };

	/**
	 * Some alternative character encodings.
	 *
	 * The table is encoded as pairs with unicode value and gsm charset value. <br>
	 * Ex:
	 *
	 * <pre>
	 * char unicode = GSM_DEFAULT_ALPHABET_ALTERNATIVES[i * 2];
	 * char gsm = GSM_DEFAULT_ALPHABET_ALTERNATIVES[i * 2 + 1];
	 * </pre>
	 *
	 * See http://www.unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT
	 */
	private static final char GSM_DEFAULT_ALPHABET_ALTERNATIVES[] = {
			// LATIN CAPITAL LETTER C WITH CEDILLA (see note above)
			0x00c7, 0x09,
			// GREEK CAPITAL LETTER ALPHA
			0x0391, 0x41,
			// GREEK CAPITAL LETTER BETA
			0x0392, 0x42,
			// GREEK CAPITAL LETTER ETA
			0x0397, 0x48,
			// GREEK CAPITAL LETTER IOTA
			0x0399, 0x49,
			// GREEK CAPITAL LETTER KAPPA
			0x039a, 0x4b,
			// GREEK CAPITAL LETTER MU
			0x039c, 0x4d,
			// GREEK CAPITAL LETTER NU
			0x039d, 0x4e,
			// GREEK CAPITAL LETTER OMICRON
			0x039f, 0x4f,
			// GREEK CAPITAL LETTER RHO
			0x03a1, 0x50,
			// GREEK CAPITAL LETTER TAU
			0x03a4, 0x54,
			// GREEK CAPITAL LETTER UPSILON
			0x03a5, 0x55,
			// GREEK CAPITAL LETTER CHI
			0x03a7, 0x58,
			// GREEK CAPITAL LETTER ZETA
			0x0396, 0x5a };

	/*
	 * public static void main(String[] args) { String s =
	 * "4b616a2074d9206e6520756964653f20"; byte[] t = Hex.decode(s);
	 * System.out.println(GSM_DEFAULT_CHARSET.length);
	 * System.out.println(toUnicode(t)); }
	 */

	/**
	 * Converts an unicode char to a GSM char
	 *
	 * @param uch
	 *            unicode char to convert
	 * @return GSM representation of the unicode char
	 */
	public static final byte toGSM(char uch) {
		// First check through the GSM charset table
		int len = GSM_DEFAULT_CHARSET.length;
		for (int i = 0; i < len; ++i) {
			if (GSM_DEFAULT_CHARSET[i] == uch) {
				// Found the correct char
				return (byte) i;
			}
		}

		// Alternative chars
		for (int i = 0; i < GSM_DEFAULT_ALPHABET_ALTERNATIVES.length / 2; i += 2) {
			if (GSM_DEFAULT_ALPHABET_ALTERNATIVES[i * 2] == uch) {
				return (byte) (GSM_DEFAULT_ALPHABET_ALTERNATIVES[i * 2 + 1] & 0x7f);
			}
		}

		// Couldn't find a valid char
		return '?';
	}

	/**
	 * Converts an unicode string to the GSM default charset encoded byte array.
	 *
	 * @param s
	 *            String to convert to GSM charset
	 * @return GSM encoded string
	 */
	public static final byte[] toGSM(String s) {
		byte[] result;
		if (s != null) {
			int len = s.length();
			byte[] gsmBytes = new byte[len];

			for (int i = 0; i < len; ++i) {
				gsmBytes[i] = toGSM(s.charAt(i));
			}

			result = gsmBytes;
		} else {
			result = null;
		}

		return result;
	}

	// public static final char EXT_TABLE_PREFIX = 0x1B;

	/**
	 * Converts GSM default charset char to an unicode char.
	 *
	 * @param ch
	 *            GSM char to convert
	 * @return Unicode representation of the given GSM char
	 */
	public static final char toUnicode(byte ch) {
		return GSM_DEFAULT_CHARSET[(0x7f) & ch];
	}

	/**
	 * Converts from GSM default charset encoded byte array into unicode String
	 *
	 * @param b
	 * @return Unicode string representing b
	 */
	public static String toUnicode(byte[] b) {
		String result;
		if (b != null) {
			int len = b.length;
			char[] r = new char[len];
			for (int i = 0; i < len; ++i) {
				r[i] = toUnicode(b[i]);
			}
			result = new String(r);
		} else {
			result = null;
		}
		return result;
	}
}