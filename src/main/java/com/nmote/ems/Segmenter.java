/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.nmote.io.IOUtils;

/**
 * Segmenter splits payload into multiple SMS segments and optionally adds port
 * addressing information.
 */
public class Segmenter {

	private static final IEFactory IE_FACTORY = IEFactory.getInstance();

	private static Random RANDOM = new Random();

	public Segmenter() {
	}

	public Segmenter(int originatorPort, int destinationPort) {
		portAddressing = (PortAddressingIE) IE_FACTORY.createPortAddressing(originatorPort, destinationPort);
	}

	public List<byte[]> toByteArrayList(byte[] payload) {
		List<byte[]> result = new ArrayList<byte[]>();
		for (UserData ud : toUserDataList(payload)) {
			try {
				result.add(IOUtils.save(ud));
			} catch (IOException e) {
				throw new RuntimeException("Impossible");
			}
		}
		return result;
	}

	public List<UserData> toUserDataList(byte[] payload) {
		List<UserData> result = new ArrayList<UserData>();
		List<IE> concatenations = new ArrayList<IE>();

		for (int i = 0; i < payload.length;) {
			UserData userData = new UserData();
			result.add(userData);

			if (portAddressing != null) {
				userData.addHeader(portAddressing);
			}

			// Check if everything fits in a single SMS
			if (i == 0 && (userData.getHeaderLength() + 1) + payload.length <= 140) {
				userData.setUserData(payload);
				break;
			}

			// Add concatenation ie
			IE con = IE_FACTORY.createConcatenation(255, 1, 0);
			concatenations.add(con);
			userData.addHeader(con);

			// Cut payload
			int len = 140 - (userData.getHeaderLength() + 1);
			if (len > payload.length - i) {
				len = payload.length - i;
			}
			byte[] data = new byte[len];
			System.arraycopy(payload, i, data, 0, len);
			userData.setUserData(data);
			i += len;
		}

		int m = result.size();
		if (m > 1) {
			int ref = RANDOM.nextInt(255);
			// Fixup concatenations
			int seq = 0;
			for (Iterator<IE> i = concatenations.iterator(); i.hasNext();) {
				ConcatenationIE con = (ConcatenationIE) i.next();
				con.setMaximum(m);
				con.setSequence(++seq);
				con.setReference(ref);
			}
		}

		return result;
	}

	private PortAddressingIE portAddressing;
}
