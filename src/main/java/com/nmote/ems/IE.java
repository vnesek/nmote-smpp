/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.nmote.io.Loadable;
import com.nmote.io.Saveable;

/**
 * IE represents 3GPP EMS InformationElement
 */
public abstract class IE implements Loadable, Saveable, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Helper method that reads 16 bit unsingned int from input stream and
	 * throws EOFException if EOF is reached.
	 *
	 * @param in
	 * @return byte from a stream
	 * @throws IOException
	 */
	final static int read16AndThrowEOF(InputStream in) throws IOException {
		int i1 = in.read();
		int i0 = in.read();
		if ((i0 | i1) == -1) {
			throw new EOFException();
		}
		return i1 << 8 | i0;
	}

	/**
	 * Helper method that reads byte from input stream and throws EOFException
	 * if EOF is reached.
	 *
	 * @param in
	 * @return byte from a stream
	 * @throws IOException
	 */
	final static int readAndThrowEOF(InputStream in) throws IOException {
		int i = in.read();
		if (i == -1) {
			throw new EOFException();
		}
		return i;
	}

	final static void write16(OutputStream out, int val) throws IOException {
		out.write((val >> 8) & 0xff);
		out.write(val & 0xff);
	}

	public IE(int identifier) {
	}

	/**
	 * Returns identifier of this IE
	 *
	 * @return
	 */
	public abstract byte getIdentifier();

	/**
	 * Returns length of this IE
	 *
	 * @return
	 */
	public abstract int getLength();

	@Override
	public abstract void load(InputStream in) throws IOException;

	@Override
	public abstract void save(OutputStream out) throws IOException;
}