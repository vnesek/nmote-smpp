/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.nmote.io.IOUtils;

/**
 * PictureIE
 */
public class PictureIE extends MediaIE {

	private static final long serialVersionUID = 1L;

	/**
	 * Inverts bitmap represented as byte[] in place.
	 *
	 * @param bitmap to invert
	 */
	public static void invertBitmap(byte[] bitmap) {
		for (int i = 0; i < bitmap.length; i++) {
			bitmap[i] ^= 0xff;
		}
	}

	public PictureIE(int identifier) {
		super(identifier);
		if (identifier < 0x10 || identifier > 0x12) {
			throw new IllegalArgumentException();
		}
	}

	public byte[] getBitmap() {
		return bitmap;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public byte getIdentifier() {
		byte identifier;
		if (isLarge()) {
			identifier = 0x10;
		} else if (isSmall()) {
			identifier = 0x11;
		} else {
			identifier = 0x12;
		}
		return identifier;
	}

	@Override
	public int getLength() {
		int len;
		if (isSmall()) {
			len = 1 + 16 * 16 / 8;
		} else if (isLarge()) {
			len = 1 + 32 * 32 / 8;
		} else {
			len = 3 + width * height / 8;
		}
		return len;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public void load(InputStream in) throws IOException {
		setPosition(readAndThrowEOF(in));
		if (isLarge()) {
			width = height = 32;
		} else if (isSmall()) {
			width = height = 16;
		} else {
			setWidth(readAndThrowEOF(in) * 8);
			setHeight(width);
		}
		bitmap = new byte[width * height / 8];
		IOUtils.copyStreamToByteArray(in, bitmap, 0, bitmap.length);
	}

	@Override
	public void save(OutputStream out) throws IOException {
		out.write(getPosition());
		if (isVariable()) {
			out.write(width / 8);
			out.write(height);
		}
		out.write(bitmap);
	}

	public void setBitmap(byte[] bs) {
		bitmap = bs;
	}

	public void setHeight(int i) {
		if (i < 0) {
			throw new IllegalArgumentException();
		}
		height = i;
	}

	public void setWidth(int i) {
		if (i < 0 || (i % 8) != 0) {
			throw new IllegalArgumentException();
		}
		width = i;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("width", width).append("height", height).append("len", bitmap.length)
				.toString();
	}

	private boolean isLarge() {
		return width == height && width == 32;
	}

	private boolean isSmall() {
		return width == height && width == 16;
	}

	private boolean isVariable() {
		return !isSmall() && !isLarge();
	}

	private int width;
	private int height;
	private byte[] bitmap;
}