/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;

/**
 * Utility class that logs hex and plain ascii dumps of data written to
 * OutputStream to a JakartaCommons log.
 *
 * @author vjeko
 */
public class LoggingOutputStream extends FilterOutputStream {

	private final static String PREFIX = ">> ";

	public LoggingOutputStream(OutputStream in, Logger log) {
		this(in, log, 16);
	}

	public LoggingOutputStream(OutputStream in, Logger log, int size) {
		this(in, log, size, true);
	}

	public LoggingOutputStream(OutputStream in, Logger log, int size, boolean dumpPlainText) {
		super(in);
		support = new LoggingStreamSupport(log, PREFIX, size, dumpPlainText);
	}

	@Override
	public synchronized void close() throws IOException {
		closeSupport();
		super.close();
	}

	@Override
	public synchronized void flush() throws IOException {
		support.flush();
		super.flush();
	}

	@Override
	public void write(int b) throws IOException {
		support.logByte(b);
		super.write(b);
	}

	private void closeSupport() {
		if (support != null) {
			support.close();
			support = null;
		}
	}

	private LoggingStreamSupport support;
}
