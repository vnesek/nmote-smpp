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

	/**
	 * @param in
	 */
	public CloseableInputStream(InputStream in) {
		super(in);
	}

	/**
	 * @see java.io.InputStream#available()
	 */
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

	/**
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		closed = true;
	}

	/**
	 * @see java.io.InputStream#read()
	 */
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

	/**
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return super.read(b, 0, b.length);
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
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

	/**
	 * @see java.io.InputStream#skip(long)
	 */
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