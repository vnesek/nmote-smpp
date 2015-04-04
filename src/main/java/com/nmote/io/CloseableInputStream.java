/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * CloseableInputStream can ba programatically closed without closing underlying
 * InputStream.
 *
 * @author Vjekoslav Nesek
 */
public class CloseableInputStream extends FilterInputStream {

	public CloseableInputStream(InputStream in) {
		super(in);
	}

	@Override
	public int available() throws IOException {
		int a;
		if (closed) {
			a = 0;
		} else {
			a = super.available();
		}
		return a;
	}

	@Override
	public void close() throws IOException {
		closed = true;
	}

	@Override
	public int read() throws IOException {
		int r;
		if (closed) {
			r = -1;
		} else {
			r = super.read();
		}
		return r;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return super.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int r;
		if (closed) {
			r = -1;
		} else {
			r = super.read(b, off, len);
		}
		return r;
	}

	@Override
	public long skip(long n) throws IOException {
		long r;
		if (closed) {
			r = 0;
		} else {
			r = super.skip(n);
		}
		return r;
	}

	private boolean closed = false;
}