/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;

/**
 * Utility class that logs hex and plain ascii dumps of data read from
 * InputStream to a JakartaCommons log.
 *
 * @author vjeko
 */
public class LoggingInputStream extends FilterInputStream {

	private final static String PREFIX = "<< ";

	public LoggingInputStream(InputStream in, Logger log) {
		this(in, log, 16);
	}

	public LoggingInputStream(InputStream in, Logger log, int size) {
		this(in, log, size, true);
	}

	public LoggingInputStream(InputStream in, Logger log, int size, boolean dumpPlainText) {
		super(in);
		support = new LoggingStreamSupport(log, PREFIX, size, dumpPlainText);
	}

	@Override
	public synchronized void close() throws IOException {
		closeSupport();
		super.close();
	}

	@Override
	public synchronized int read() throws IOException {
		int b = super.read();
		if (b != -1) {
			support.logByte(b);
		} else {
			closeSupport();
		}

		return b;
	}

	private void closeSupport() {
		if (support != null) {
			support.close();
			support = null;
		}
	}

	private LoggingStreamSupport support;
}
