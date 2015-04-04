/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DefaultIE extends IE {

	private static final long serialVersionUID = 1L;

	public DefaultIE(byte id, byte[] data) {
		super(id);
		this.id = id;
		this.data = data;
	}

	/**
	 * @see com.nmote.ems.IE#getIdentifier()
	 */
	@Override
	public byte getIdentifier() {
		return id;
	}

	/**
	 * @see com.nmote.ems.IE#getLength()
	 */
	@Override
	public int getLength() {
		return data.length;
	}

	/**
	 * @see com.nmote.ems.IE#load(java.io.InputStream)
	 */
	@Override
	public void load(InputStream in) throws IOException {
		throw new IOException("not implemented");
	}

	/**
	 * @see com.nmote.ems.IE#save(java.io.OutputStream)
	 */
	@Override
	public void save(OutputStream out) throws IOException {
		out.write(data);
	}

	private byte id;
	private byte[] data;
}