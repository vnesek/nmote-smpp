/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.nmote.io.IOUtils;
import com.nmote.io.Loadable;
import com.nmote.io.Saveable;

/**
 * UserData
 */
public class UserData implements Loadable, Saveable, Serializable {

	private static final long serialVersionUID = 1L;

	public void addHeader(IE ie) {
		createHeaders();
		headers.add(ie);
	}

	public int getHeaderLength() {
		int udhl = 0;
		if (headers != null) {
			for (Iterator<IE> i = headers.iterator(); i.hasNext();) {
				IE ie = i.next();
				udhl += ie.getLength() + 2;
			}
		}
		return udhl;
	}

	public List<IE> getHeaders() {
		createHeaders();
		return headers;
	}

	public byte[] getUserData() {
		return userData;
	}

	@Override
	public void load(InputStream in) throws IOException {
		int udhl = IE.readAndThrowEOF(in);
		if (udhl > 0) {
			createHeaders();
			IEFactory factory = IEFactory.getInstance();
			for (int i = 0; i < udhl;) {
				int identifier = IE.readAndThrowEOF(in);
				IE ie = factory.create(identifier);
				int ieLen = IE.readAndThrowEOF(in);
				ie.load(in);
				if (ie.getLength() != ieLen) {
					throw new IOException();
				}
				i += 2 + ieLen;
				addHeader(ie);
			}
		}
		userData = IOUtils.toByteArray(in, 140 - udhl, 1024);
	}

	@Override
	public void save(OutputStream out) throws IOException {
		int udhl = getHeaderLength();
		out.write(udhl);
		if (headers != null) {
			for (Iterator<IE> i = headers.iterator(); i.hasNext();) {
				IE ie = i.next();
				out.write(ie.getIdentifier());
				out.write(ie.getLength());
				ie.save(out);
			}
		}
		if (userData != null) {
			out.write(userData);
		}
	}

	public void setHeaders(List<IE> list) {
		headers = list;
	}

	public void setUserData(byte[] bs) {
		userData = bs;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("udh", headers).append("data", new String(userData)).toString();
	}

	private void createHeaders() {
		if (headers == null) {
			headers = new ArrayList<IE>();
		}
	}

	private List<IE> headers;
	private byte[] userData;
}
