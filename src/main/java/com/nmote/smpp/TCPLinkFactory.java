/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;

/**
 * TCPLinkFactory creates links over TCP connections (sockets).
 *
 * @author Vjekoslav Nesek
 */
public class TCPLinkFactory implements LinkFactory {

	/**
	 * Constructor for TCPLink.
	 */
	public TCPLinkFactory() {
	}

	/**
	 * Constructor for TCPLink.
	 */
	public TCPLinkFactory(String host) {
		setHost(host);
		setTimeout(0);
	}

	/**
	 * Constructor for TCPLink.
	 */
	public TCPLinkFactory(String host, int port) {
		setHost(host);
		setPort(port);
		setTimeout(0);
	}

	@Override
	public Link createLink() throws LinkCreationException {
		try {
			if (log != null) {
				log.debug("Opening socket " + this);
			}
			Socket socket = createSocket();
			if (log != null) {
				log.debug("Socket opened");
			}
			return createLink(socket);
		} catch (SocketTimeoutException e) {
			throw new ConnectTimedOutException(e);
		} catch (IOException ioe) {
			if (log != null) {
				log.error("Failed to open socket: " + ioe);
			}
			throw new LinkCreationException(ioe);
		}
	}

	/**
	 * Returns the host.
	 *
	 * @return String
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return Returns the log.
	 */
	public Logger getLog() {
		return log;
	}

	/**
	 * Returns the port.
	 *
	 * @return int
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the timeout.
	 *
	 * @return int
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the host.
	 *
	 * @param host
	 *            The host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param log
	 *            The log to set.
	 */
	public void setLog(Logger log) {
		this.log = log;
	}

	/**
	 * Sets the port.
	 *
	 * @param port
	 *            The port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Sets the timeout.
	 *
	 * @param timeout
	 *            The timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * String representation.
	 *
	 * @return a String
	 */
	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		b.append("host", getHost());
		b.append("port", getPort());
		if (timeout != 0) {
			b.append("timeout", timeout);
		}
		return b.toString();
	}

	protected Link createLink(Socket socket) throws IOException {
		return new TCPLink(socket, log);
	}

	protected Socket createSocket() throws IOException {
		if (host == null) {
			throw new IOException("Remote host not specified");
		}
		Socket result = new Socket(host, port);
		if (timeout != 0) {
			result.setSoTimeout(timeout);
		}
		return result;
	}

	private String host;
	private int port = 2775;
	private int timeout = 0;
	private Logger log;
}