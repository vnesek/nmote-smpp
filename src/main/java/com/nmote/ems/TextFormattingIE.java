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

	public TextFormattingIE(int identifier) {
		super(identifier);
		if (identifier != getIdentifier()) {
			throw new IllegalArgumentException();
		}
	}

	public int getEnd() {
		return end;
	}

	@Override
	public byte getIdentifier() {
		return 0x0a;
	}

	@Override
	public int getLength() {
		return 3;
	}

	public int getStart() {
		return start;
	}

	public int getStyle() {
		return style;
	}

	@Override
	public void load(InputStream in) throws IOException {
		setStart(readAndThrowEOF(in));
		setEnd(start + readAndThrowEOF(in));
		setStyle(readAndThrowEOF(in));
	}

	@Override
	public void save(OutputStream out) throws IOException {
		out.write(start);
		out.write(end - start);
		out.write(style);
	}

	public void setEnd(int i) {
		end = i;
	}

	public void setStart(int i) {
		start = i;
	}

	public void setStyle(int i) {
		style = i;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("start", start).append("end", end).append("style", style).toString();
	}

	private int start;
	private int end;
	private int style;
}