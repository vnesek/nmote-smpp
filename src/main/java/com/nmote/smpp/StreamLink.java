/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmote.io.LoggingInputStream;
import com.nmote.io.LoggingOutputStream;

/**
 * StreamLink implements Link interface using a pair of input and output
 * streams.
 *
 * @see com.nmote.smpp.Link
 * @author Vjekoslav Nesek
 */
public class StreamLink implements Link {

	private static final boolean DEBUG = false;

	private static Logger log = LoggerFactory.getLogger(StreamLink.class);

	/**
	 * Constructor for StreamLink.
	 */
	public StreamLink(InputStream in, OutputStream out) {
		this(in, out, null);
	}

	public StreamLink(InputStream in, OutputStream out, Logger log) {
		if (log != null) {
			in = new LoggingInputStream(in, log);
			out = new LoggingOutputStream(out, log);
		}
		input = new SMPPInputStream(in);
		output = new SMPPOutputStream(out);
	}

	/**
	 * @see com.nmote.smpp.Link#close()
	 */
	@Override
	public void close() throws IOException {
		IOException ioe = null;
		try {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				log.debug("Closing input stream failed", e);
				ioe = e;
			}

			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				log.debug("Closing output stream failed", e);
				ioe = e;
			}
		} finally {
			input = null;
			output = null;
		}

		if (ioe != null) {
			throw new IOException("Closing SMPP streams failed: " + ioe);
		}
	}

	/**
	 * @see com.nmote.smpp.Link#receive()
	 */
	@Override
	public AbstractPDU receive() throws IOException {
		AbstractPDU pdu = input.readPDU();
		if (DEBUG) {
			log.debug("Received " + pdu);
		}
		return pdu;
	}

	/**
	 * @see com.nmote.smpp.Link#send(AbstractPDU)
	 */
	@Override
	public void send(AbstractPDU pdu) throws IOException {
		if (DEBUG) {
			log.debug("Sending " + pdu);
		}
		output.writePDU(pdu);
	}

	private SMPPInputStream input;
	private SMPPOutputStream output;
}