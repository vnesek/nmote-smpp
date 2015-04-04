/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream that reads elements of a SMPP PDU per client's request.
 * Buffering is done internally.
 *
 * @author Vjekoslav Nesek
 */
class SMPPInputStream extends FilterInputStream {

	/**
	 * Creates new SMPPInputStream
	 *
	 * @param in
	 *            underlying InputStream object
	 */
	public SMPPInputStream(InputStream in) {
		super(in);
	}

	@Override
	public synchronized void close() throws IOException {
		super.close();
	}

	public boolean readBoolean() throws IOException {
		return readInteger1() != 0;
	}

	public String readCString() throws IOException {
		if (bytesLeft == 0) {
			return null;
		}

		char[] temp = stringBuffer;

		int p = 0;
		for (;;) {
			int r = in.read();
			if (r > 0) {
				temp[p] = (char) r;
				++p;
			} else if (r == 0) {
				break;
			} else {
				throw new EOFException();
			}
		}

		bytesLeft -= p + 1;
		return p > 0 ? new String(temp, 0, p) : null;
	}

	public int readInteger1() throws IOException {
		int r = in.read();
		if (r == -1)
			throw new EOFException();

		--bytesLeft;
		return r;
	}

	public int readInteger2() throws IOException {
		int i1 = in.read();
		int i2 = in.read();

		if ((i1 | i2) < 0) {
			throw new EOFException();
		}

		bytesLeft -= 2;
		return i1 << 8 | i2;
	}

	public int readInteger4() throws IOException {
		int i1 = in.read();
		int i2 = in.read();
		int i3 = in.read();
		int i4 = in.read();

		if ((i1 | i2 | i3 | i4) < 0) {
			throw new EOFException();
		}

		bytesLeft -= 4;
		return i1 << 24 | i2 << 16 | i3 << 8 | i4;
	}

	public byte[] readOctetString(int length) throws IOException {
		byte[] result = new byte[length];
		for (int pos = 0; pos < length;) {
			int r = in.read(result, pos, length - pos);
			if (r == -1) {
				throw new EOFException();
			}
			pos += r;
		}
		bytesLeft -= length;
		return result;
	}

	/**
	 * Reads PDU from underlying input stream.
	 */
	public synchronized AbstractPDU readPDU() throws IOException {
		if (bytesLeft != 0) {
			assert false : "PDU not read completely";
		}

		// Read size of PDU
		// TODO Handle SocketTimeoutException
		bytesLeft = readInteger4() - 4;

		// Create PDU
		int id = readInteger4();
		AbstractPDU pdu = factory.createPDU(id);

		if (pdu != null) {
			// Read status
			pdu.status = readInteger4();

			// Read sequence number
			pdu.sequence = readInteger4();

			// Let PDU read its parameters
			pdu.readParameters(this);

			return pdu;
		} else {
			// Skip this PDU
			skipPDU();
			return null;
		}
	}

	public byte[] readPString() throws IOException {
		int length = in.read();
		--bytesLeft;
		byte[] result;
		if (length > 0) {
			result = new byte[length];
			int pos = 0;
			while (pos < length) {
				int r = in.read(result, pos, length - pos);
				if (r != -1) {
					pos += r;
				} else {
					throw new EOFException();
				}
			}
			bytesLeft -= length;
		} else {
			result = null;
		}
		return result;
	}

	public SMPPAddress readSMPPAddress() throws IOException {
		int ton = readInteger1();
		int npi = readInteger1();
		String address = readCString();
		SMPPAddress result;
		if (address != null) {
			result = new SMPPAddress(address, ton, npi);
		} else {
			result = null;
		}
		return result;
	}

	public SMPPTime readSMPPTime() throws IOException {
		String time = readCString();
		SMPPTime result;
		if (time != null) {
			// This was probably a bug... fixing it bellow
			// result = new SMPPTime(readCString());
			try {
				result = new SMPPTime(time);
			} catch (Exception e) {
				System.out.println("Invalid SMPP time '" + time + "'");
				e.printStackTrace(System.err);
				result = null;
			}
		} else {
			result = null;
		}
		return result;
	}

	public String readString(int length) throws IOException {
		if (length == 0) {
			return null;
		}

		char[] temp = stringBuffer;
		for (int i = 0; i < length; ++i) {
			int r = in.read();
			if (r >= 0) {
				temp[i] = (char) r;
			} else {
				throw new EOFException();
			}
		}

		bytesLeft -= length;
		return new String(temp, 0, length);
	}

	boolean hasMoreOptionalParameters() {
		return bytesLeft > 0;
	}

	synchronized void skipPDU() throws IOException {
		while (bytesLeft > 0) {
			long skipped = skip(bytesLeft);
			if (skipped < 0) {
				throw new IOException("Skipping PDU failed, negative result: " + skipped);
			} else {
				bytesLeft -= skipped;
			}
		}
	}

	private char[] stringBuffer = new char[32767];
	private PDUFactory factory = new PDUFactory();
	private int bytesLeft;
}