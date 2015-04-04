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
 * TextFormattingIE
 */
public class TextFormattingIE extends IE {

	private static final long serialVersionUID = 1L;

	/**
	 * @param identifier
	 */
	public TextFormattingIE(int identifier) {
		super(identifier);
		if (identifier != getIdentifier()) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @return
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @see com.nmote.ems.IE#getIdentifier()
	 */
	@Override
	public byte getIdentifier() {
		return 0x0a;
	}

	/**
	 * @see com.nmote.ems.IE#getLength()
	 */
	@Override
	public int getLength() {
		return 3;
	}

	/**
	 * @return
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * @see com.nmote.io.Loadable#load(java.io.InputStream)
	 */
	@Override
	public void load(InputStream in) throws IOException {
		setStart(readAndThrowEOF(in));
		setEnd(start + readAndThrowEOF(in));
		setStyle(readAndThrowEOF(in));
	}

	/**
	 * @see com.nmote.io.Saveable#save(java.io.OutputStream)
	 */
	@Override
	public void save(OutputStream out) throws IOException {
		out.write(start);
		out.write(end - start);
		out.write(style);
	}

	/**
	 * @param i
	 */
	public void setEnd(int i) {
		end = i;
	}

	/**
	 * @param i
	 */
	public void setStart(int i) {
		start = i;
	}

	/**
	 * @param i
	 */
	public void setStyle(int i) {
		style = i;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("start", start).append("end", end).append("style", style).toString();
	}

	private int start;
	private int end;
	private int style;
}