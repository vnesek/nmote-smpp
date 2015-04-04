/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream used to output SMPP PDUs. Buffering is done internaly so it is
 * not necessary to wrap underlying OutputStream in a BufferedOutputStream.
 * Methods of this class arren't synchronized, it is clients responsibility to
 * synchronize access from multiple threads.
 *
 * @author Vjekoslav Nesek
 */
class SMPPOutputStream extends FilterOutputStream {
	private static final String DEFAULT_CHARSET = "iso-8859-1";

	/**
	 * Creates a new SMPPOutputStream with buffer size of 512 bytes.
	 *
	 * @param out
	 *            underlying OutputStream.
	 */
	public SMPPOutputStream(OutputStream out) {
		this(out, 8192);
	}

	/**
	 * Creates a new SMPPOutputStream.
	 *
	 * @param out
	 *            underlying OutputStream.
	 * @param size
	 *            the buffer size.
	 * @exception IllegalArgumentException
	 *                if size <= 0.
	 */
	public SMPPOutputStream(OutputStream out, int size) {
		super(out);
		if (size <= 0) {
			throw new IllegalArgumentException("Buffer size is less than 0");
		}
		buffer = new byte[size];
	}

	/**
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		super.close();
	}

	/**
	 * Flushes this buffered output stream. This forces any buffered output
	 * bytes to be written out to the underlying output stream.
	 *
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public void flush() throws IOException {
		flushBuffer();
		out.flush();
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at
	 * offset <code>off</code> to this buffered output stream.
	 *
	 * @param b
	 *            the data.
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		System.arraycopy(b, off, buffer, bytesWritten, len);
		bytesWritten += len;
	}

	/**
	 * Writes the specified byte to this buffered output stream.
	 *
	 * @param b
	 *            the byte to be written.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public void write(int b) throws IOException {
		buffer[bytesWritten++] = (byte) b;
	}

	public void writeBoolean(boolean b) throws IOException {
		writeInteger1(b ? 1 : 0);
	}

	/**
	 * Writes the specified string as ASCII 0-terminated string.
	 *
	 * @param s
	 *            the string to be written.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public void writeCString(String s) throws IOException {
		if (s != null) {
			write(s.getBytes(DEFAULT_CHARSET));
		}
		write(0);
	}

	public void writeInteger1(int b) throws IOException {
		write(b);
	}

	public void writeInteger2(int v) throws IOException {
		int idx = bytesWritten;
		buffer[idx + 0] = (byte) ((v >>> 8) & 0xFF);
		buffer[idx + 1] = (byte) ((v >>> 0) & 0xFF);

		bytesWritten += 2;
	}

	/**
	 * Writes an <code>int</code> to the underlying output stream as four bytes,
	 * high byte first.
	 *
	 * @param v
	 *            an <code>int</code> to be written.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public final void writeInteger4(int v) throws IOException {
		int idx = bytesWritten;
		buffer[idx + 0] = (byte) ((v >>> 24) & 0xFF);
		buffer[idx + 1] = (byte) ((v >>> 16) & 0xFF);
		buffer[idx + 2] = (byte) ((v >>> 8) & 0xFF);
		buffer[idx + 3] = (byte) ((v >>> 0) & 0xFF);
		bytesWritten += 4;
	}

	public void writeOctetString(byte[] b) throws IOException {
		if (b != null) {
			System.arraycopy(b, 0, buffer, bytesWritten, b.length);
			bytesWritten += b.length;
		}
	}

	/**
	 * Writes pdu to underlying output stream. Flushes data out after complete
	 * PDU is written.
	 *
	 * @param pdu
	 *            PDU to be written
	 * @throws IOException
	 */
	public synchronized void writePDU(AbstractPDU pdu) throws IOException {
		startPDU(pdu.getCommandId(), pdu.getStatus(), pdu.getSequence());
		pdu.writeParameters(this);
		endPDU();

		// flushBuffer();
		flush();
	}

	public void writePString(byte[] data) throws IOException {
		if (data != null) {
			write(data.length);
			write(data);
		} else {
			write(0);
		}
	}

	public void writeSMPPAddress(SMPPAddress a) throws IOException {
		if (a != null) {
			writeInteger1(a.getTon());
			writeInteger1(a.getNpi());
			writeCString(a.getAddress());
		} else {
			writeInteger1(0);
			writeInteger1(0);
			writeCString(null);
		}
	}

	public void writeSMPPTime(SMPPTime t) throws IOException {
		if (t != null) {
			writeCString(t.toString());
		} else {
			writeCString(null);
		}
	}

	public void writeString(String s) throws IOException {
		if (s != null)
			write(s.getBytes(DEFAULT_CHARSET));
	}

	public void writeString(String s, int length) throws IOException {
		if (s != null) {
			write(s.getBytes(DEFAULT_CHARSET));
			length -= s.length();
		}

		while (length-- > 0)
			write(0x32);
	}

	/**
	 * Completes writting of a PDU.
	 *
	 * @throws IOException
	 */
	private void endPDU() throws IOException {
		int v = bytesWritten;

		buffer[0] = (byte) ((v >>> 24) & 0xFF);
		buffer[1] = (byte) ((v >>> 16) & 0xFF);
		buffer[2] = (byte) ((v >>> 8) & 0xFF);
		buffer[3] = (byte) ((v >>> 0) & 0xFF);
	}

	/**
	 * Flushes the internal buffer.
	 */
	private void flushBuffer() throws IOException {
		if (bytesWritten > 0) {
			out.write(buffer, 0, bytesWritten);
			bytesWritten = 0;
		}
	}

	/**
	 * Starts writting of a PDU.
	 *
	 * @param commandId
	 * @param status
	 * @param sequence
	 * @throws IOException
	 */
	private void startPDU(int commandId, int status, int sequence) throws IOException {
		if (bytesWritten > 0) {
			throw new IOException("Previous PDU not finished");
		}
		writeInteger4(0);
		writeInteger4(commandId);
		writeInteger4(status);
		writeInteger4(sequence);
	}

	/** The internal buffer where data is stored. */
	private byte[] buffer;

	/** Number of bytes written into buffer so far. */
	private int bytesWritten;
}