/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * ConcatenationIE
 */
public class ConcatenationIE extends IE {

	private static final long serialVersionUID = 1L;

	public ConcatenationIE(int identifier) {
		super(identifier);
		if (identifier == 0) {
			eightBit = true;
		} else if (identifier == 8) {
			eightBit = false;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @see com.nmote.ems.IE#getIdentifier()
	 */
	@Override
	public byte getIdentifier() {
		return (byte) (eightBit ? 0 : 8);
	}

	/**
	 * @see com.nmote.ems.IE#getLength()
	 */
	@Override
	public int getLength() {
		return eightBit ? 3 : 4;
	}

	/**
	 * @return
	 */
	public int getMaximum() {
		return maximum;
	}

	/**
	 * @return
	 */
	public int getReference() {
		return reference;
	}

	/**
	 * @return
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @see com.nmote.ems.IE#load(java.io.InputStream)
	 */
	@Override
	public void load(InputStream in) throws IOException {
		setReference(eightBit ? readAndThrowEOF(in) : read16AndThrowEOF(in));
		setMaximum(readAndThrowEOF(in));
		setSequence(readAndThrowEOF(in));
	}

	/**
	 * @see com.nmote.ems.IE#save(java.io.OutputStream)
	 */
	@Override
	public void save(OutputStream out) throws IOException {
		if (eightBit) {
			out.write(reference);
		} else {
			write16(out, reference);
		}
		out.write(maximum);
		out.write(sequence);
	}

	/**
	 * @param i
	 */
	public void setMaximum(int i) {
		if (i < 1 || i > 255) {
			throw new IllegalArgumentException();
		}
		maximum = i;
	}

	/**
	 * @param i
	 */
	public void setReference(int i) {
		if (i < 0 || i > 65535) {
			throw new IllegalArgumentException();
		}
		reference = i;
		eightBit = reference <= 255;
	}

	/**
	 * @param i
	 */
	public void setSequence(int i) {
		if (i < 1 || i > 255) {
			throw new IllegalArgumentException();
		}
		sequence = i;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", sequence).append("max", maximum).append("ref", reference)
				.toString();
	}

	private int maximum;
	private int sequence;
	private int reference;
	private boolean eightBit;
}
