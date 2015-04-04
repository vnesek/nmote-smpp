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
 * PortAddressingIE
 */
public class PortAddressingIE extends IE {

	private static final long serialVersionUID = 1L;

	public PortAddressingIE(int identifier) {
		super(identifier);
		if (identifier == 4) {
			eightBit = true;
		} else if (identifier == 5) {
			eightBit = false;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	@Override
	public byte getIdentifier() {
		return (byte) (eightBit ? 4 : 5);
	}

	@Override
	public int getLength() {
		return eightBit ? 2 : 4;
	}

	public int getOriginatorPort() {
		return originatorPort;
	}

	@Override
	public void load(InputStream in) throws IOException {
		if (eightBit) {
			setDestinationPort(readAndThrowEOF(in));
			setOriginatorPort(readAndThrowEOF(in));
		} else {
			setDestinationPort(read16AndThrowEOF(in));
			setOriginatorPort(read16AndThrowEOF(in));
		}
	}

	@Override
	public void save(OutputStream out) throws IOException {
		if (eightBit) {
			out.write(destinationPort);
			out.write(originatorPort);
		} else {
			write16(out, destinationPort);
			write16(out, originatorPort);
		}
	}

	public void setDestinationPort(int i) {
		if (i < 0 || i > 65535) {
			throw new IllegalArgumentException();
		}
		destinationPort = i;
		eightBit = destinationPort <= 255 && originatorPort <= 255;
	}

	public void setOriginatorPort(int i) {
		if (i < 0 || i > 65535) {
			throw new IllegalArgumentException();
		}
		originatorPort = i;
		eightBit = destinationPort <= 255 && originatorPort <= 255;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("dst", destinationPort).append("org", originatorPort).toString();
	}

	private int originatorPort;
	private int destinationPort;
	private boolean eightBit;
}