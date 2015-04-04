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
 * PredefinedSoundIE
 */
public class PredefinedSoundIE extends MediaIE {

	private static final long serialVersionUID = 1L;

	/**
	 * @param identifier
	 */
	public PredefinedSoundIE(int identifier) {
		super(identifier);
		if (identifier != getIdentifier()) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @see com.nmote.ems.IE#getIdentifier()
	 */
	@Override
	public byte getIdentifier() {
		return 0x0b;
	}

	/**
	 * @see com.nmote.ems.IE#getLength()
	 */
	@Override
	public int getLength() {
		return 2;
	}

	/**
	 * @return
	 */
	public int getSound() {
		return sound;
	}

	/**
	 * @see com.nmote.io.Loadable#load(java.io.InputStream)
	 */
	@Override
	public void load(InputStream in) throws IOException {
		setPosition(readAndThrowEOF(in));
		setSound(readAndThrowEOF(in));
	}

	/**
	 * @see com.nmote.io.Saveable#save(java.io.OutputStream)
	 */
	@Override
	public void save(OutputStream out) throws IOException {
		out.write(getPosition());
		out.write(sound);
	}

	/**
	 * @param i
	 */
	public void setSound(int i) {
		sound = i;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("pos", getPosition()).append("sound", sound).toString();
	}

	private int sound;
}