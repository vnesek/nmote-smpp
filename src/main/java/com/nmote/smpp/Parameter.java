/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.UnsupportedEncodingException;

import com.nmote.util.Hex;

/**
 * Parameter is a holder for PDU TLV parameters. Data is stored as a byte array.
 * There is a number of helper getter and setter methods for more convinient
 * data access.
 *
 * @author Vjekoslav Nesek
 */
public class Parameter {

	/**
	 * Constructor for TLV.
	 *
	 * @param tag
	 *            SMPP tag code
	 */
	public Parameter(int tag) {
		setTag(tag);
	}

	/**
	 * Constructor for TLV.
	 *
	 * @param tag
	 *            SMPP tag code
	 * @param data
	 *            parameter value
	 */
	public Parameter(int tag, byte[] data) {
		setTag(tag);
		setData(data);
	}

	/**
	 * Returns the data.
	 *
	 * @return byte[]
	 */
	public byte[] getData() {
		return data;
	}

	public int getInt1() {
		int len = getLength();
		if (len < 1) {
			throw new IllegalStateException("Data length too short");
		}
		return data[0];
	}

	public int getInt2() {
		int len = getLength();
		if (len < 2) {
			throw new IllegalStateException("Data length too short");
		}
		return (data[0] << 8) | (data[1]);
	}

	public int getInt4() {
		int len = getLength();
		if (len < 4) {
			throw new IllegalStateException("Data length too short");
		}
		return (data[0] << 24) | (data[1] << 16) | (data[0] << 8) | (data[1]);
	}

	public int getLength() {
		return data != null ? data.length : 0;
	}

	public String getString() {
		String result;
		if (data != null) {
			if (data.length == 0) {
				return "";
			}
			try {
				// Special case C-style zero terminated strings
				if (data[data.length - 1] == 0) {
					result = new String(data, 0, data.length - 1, "ISO-8859-1");
				} else {
					result = new String(data, "ISO-8859-1");
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Impossible");
			}
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * Returns the tag.
	 *
	 * @return tag code
	 */
	public int getTag() {
		return tag;
	}

	/**
	 * Sets the data.
	 *
	 * @param data
	 *            The data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	public void setInt1(int value) {
		data = new byte[1];
		data[0] = (byte) (value & 0xff);
	}

	public void setInt2(int value) {
		data = new byte[2];
		data[0] = (byte) ((value >> 8) & 0xff);
		data[1] = (byte) (value & 0xff);
	}

	public void setInt4(int value) {
		data = new byte[4];
		data[0] = (byte) ((value >> 24) & 0xff);
		data[1] = (byte) ((value >> 16) & 0xff);
		data[2] = (byte) ((value >> 8) & 0xff);
		data[3] = (byte) (value & 0xff);
	}

	public void setString(String value) {
		if (value != null) {
			try {
				data = value.getBytes("ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Impossible");
			}
		} else {
			data = null;
		}
	}

	/**
	 * Sets the tag.
	 *
	 * @param tag
	 *            The tag to set
	 */
	public void setTag(int tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer(32);
		b.append(Tag.toString(tag));
		b.append('=');
		b.append(Hex.encode(data));
		return b.toString();
	}

	private int tag;
	private byte[] data;
}