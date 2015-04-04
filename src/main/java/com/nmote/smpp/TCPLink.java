/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCPLink implements Link interface for communication over sockets.
 *
 * @author Vjekoslav Nesek
 */
class TCPLink extends StreamLink {

	private static Logger log = LoggerFactory.getLogger(TCPLink.class);

	/**
	 * Constructor for TCPLink.
	 */
	public TCPLink(Socket socket) throws IOException {
		this(socket, null);
	}

	public TCPLink(Socket socket, Logger log) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream(), log);
		this.socket = socket;
	}

	/**
	 * @see com.nmote.smpp.Link#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		if (socket != null) {
			// IOException ioe;
			// try {
			// super.close();
			// ioe = null;
			// } catch (IOException e) {
			// ioe = e;
			// }

			try {
				socket.close();
			} finally {
				socket = null;
			}

			// if (ioe != null) {
			// log.debug("Closing StreamLink failed", ioe);
			// throw new IOException("Socket streams closing falied: " + ioe);
			// }

		} else {
			log.warn("Closing already closed link (" + this + ")");
		}
	}

	/**
	 * String representation.
	 *
	 * @return a String
	 */
	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		b.append("host", socket.getInetAddress());
		b.append("port", socket.getPort());
		// b.append("connected", socket.isConnected());
		return b.toString();
	}

	private Socket socket;
}