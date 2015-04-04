/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;

/**
 * ServerTCPLinkFactory listens on a server port and creates Link instances for
 * accepted connections.
 *
 * @author Vjekoslav Nesek
 */
public class ServerTCPLinkFactory implements LinkFactory {

	/**
	 * Constructor for ServerTCPLinkFactory.
	 */
	public ServerTCPLinkFactory() {
	}

	/**
	 * Constructor for ServerTCPLinkFactory.
	 *
	 * @param port
	 *            Port to listen for connections
	 */
	public ServerTCPLinkFactory(int port) {
		setPort(port);
	}

	@Override
	public Link createLink() throws LinkCreationException {
		for (;;) {
			try {
				if (timeout != 0) {
					serverSocket.setSoTimeout(timeout);
				}
				if (serverSocket == null) {
					throw new LinkCreationException("not accepting");
				}
				Socket s = serverSocket.accept();
				if (checkSocket(s)) {
					customizeSocket(s);
					return new TCPLink(s, log);
				}
			} catch (SocketTimeoutException e) {
				throw new ConnectTimedOutException(e);
			} catch (IOException ioe) {
				throw new LinkCreationException(ioe);
			}
		}
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
	 * Returns the accept.
	 *
	 * @return boolean
	 */
	public boolean isAccept() {
		return accept;
	}

	/**
	 * Sets the accept.
	 *
	 * @param accept
	 *            The accept to set
	 */
	public void setAccept(boolean accept) throws IOException {
		if (accept && serverSocket == null) {
			serverSocket = createServerSocket();
		} else if (!accept && serverSocket != null) {
			try {
				serverSocket.close();
			} finally {
				serverSocket = null;
			}
		}

		this.accept = accept;
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
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout must be >= 0");
		}
		this.timeout = timeout;
	}

	/**
	 * Checks socket (IP address/hostname) or some other checks. If this method
	 * returns false, connection is automatically rejected and socket is closed.
	 *
	 * @param s
	 *            Socket to check
	 * @return true if socket is ok.
	 */
	protected boolean checkSocket(Socket s) throws IOException {
		if (log != null && log.isDebugEnabled()) {
			log.debug("Accepted connection from " + s.getInetAddress());
		}
		return true;
	}

	/**
	 * Creates a ServerSocket instance for this LinkFactory. Called when
	 * <code>accept</code> property is changed to <code>true</code>.
	 *
	 * @return ServerSocket
	 * @throws IOException
	 */
	protected ServerSocket createServerSocket() throws IOException {
		return new ServerSocket(getPort());
	}

	/**
	 * Customizes socket parameters. Called after <code>checkSocket()</code>
	 * methos.
	 *
	 * @param s
	 *            Socket to customize
	 */
	protected void customizeSocket(Socket s) {
	}

	private int port = 2775;
	private boolean accept;
	private ServerSocket serverSocket;
	private int timeout = 0;
	private Logger log;
}