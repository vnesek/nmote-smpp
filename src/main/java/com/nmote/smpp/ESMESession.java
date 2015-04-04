/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.IOException;
import java.util.TimerTask;

/**
 * ESMESession is a session running at ESME and communicating with a SMSCSession
 * executing on an SMSC.
 *
 * @author Vjekoslav Nesek
 */
public class ESMESession extends Session {

	/**
	 * Instantiates a new ESMESession over a link
	 *
	 * @param link
	 */
	public ESMESession(Link link) {
		super(link);
	}

	/**
	 * Instantiates a new ESMESession. LinkFactory is used to create Link
	 * instance when open is called.
	 *
	 * @param linkFactory
	 */
	public ESMESession(LinkFactory linkFactory) {
		super(linkFactory);
	}

	/**
	 * Synchronously binds to SMSC session running on other side of link.
	 *
	 * @throws SessionException
	 */
	public void bind() throws SessionException {
		Binding binding = getBinding();

		if (binding == null) {
			throw new BindingFailedException("Binding data not set");
		}

		boolean receiver = binding.isReceiver();
		boolean transmitter = binding.isTransmitter();

		if (receiver && transmitter) {
			bindTransceiver();
		} else if (receiver) {
			bindReceiver();
		} else if (transmitter) {
			bindTransmitter();
		} else {
			log.warn("Called bind(), however neither receiver nor transmitter are true");
		}
	}

	/**
	 * @see com.nmote.smpp.Session#close()
	 */
	@Override
	public synchronized void close() {
		// TODO Workaround for some threading bug/issue
		boolean autoConnect = isAutoConnect();
		setAutoConnect(false);

		if (connectTask != null) {
			connectTask.cancel();
			connectTask = null;
		}

		super.close();

		setAutoConnect(autoConnect);
	}

	/**
	 * @return
	 */
	public long getConnectRetry() {
		return connectRetry;
	}

	/**
	 * Returns the autoConnect.
	 *
	 * @return boolean
	 */
	public boolean isAutoConnect() {
		return autoConnect;
	}

	/**
	 * @see com.nmote.smpp.Session#open()
	 */
	@Override
	public synchronized void open() throws LinkCreationException {
		try {
			super.open();
		} finally {
			if (autoConnect) {
				scheduleReconnect();
			}
		}
	}

	/**
	 * Sets the autoConnect.
	 *
	 * @param autoConnect
	 *            The autoConnect to set
	 */
	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}

	/**
	 * @param l
	 */
	public void setConnectRetry(long retry) {
		if (retry < 500) {
			throw new IllegalArgumentException("Retry interval must be > 500 ms: " + retry);
		}
		connectRetry = retry;
	}

	protected void bindReceiver() throws SessionException {
		BindReceiverPDU bind = new BindReceiverPDU();

		Binding binding = getBinding();
		bind.setSystemId(binding.getSystemId());
		bind.setSystemType(binding.getSystemType());
		bind.setPassword(binding.getPassword());
		bind.setAddressRange(binding.getAddressRange());
		bind.setInterfaceVersion(getLocalVersion());
		executeBind(bind);
		setState(BOUND_RX);
	}

	protected void bindTransceiver() throws SessionException {
		BindTransceiverPDU bind = new BindTransceiverPDU();

		Binding binding = getBinding();
		bind.setSystemId(binding.getSystemId());
		bind.setSystemType(binding.getSystemType());
		bind.setPassword(binding.getPassword());
		bind.setAddressRange(binding.getAddressRange());
		bind.setInterfaceVersion(getLocalVersion());
		executeBind(bind);
		setState(BOUND_TRX);
	}

	protected void bindTransmitter() throws SessionException {
		BindTransmitterPDU bind = new BindTransmitterPDU();

		Binding binding = getBinding();
		bind.setSystemId(binding.getSystemId());
		bind.setSystemType(binding.getSystemType());
		bind.setPassword(binding.getPassword());
		bind.setAddressRange(binding.getAddressRange());
		bind.setInterfaceVersion(getLocalVersion());
		executeBind(bind);
		setState(BOUND_TX);
	}

	protected AbstractPDU executeBind(AbstractPDU bind) throws SessionException {
		Command cmd = new Command(bind);
		execute(cmd);
		int status = cmd.getResponse().getStatus();
		if (status != ESMEStatus.OK) {
			throw new BindingFailedException(status);
		}
		AbstractPDU response = cmd.getResponse();

		// Determine SMPP version of remote side
		Parameter scif = response.getParameter(Tag.SC_INTERFACE_VERSION);
		if (scif != null) {
			setRemoteVersion(scif.getInt1());
		} else {
			setRemoteVersion(0x33);
		}

		return response;
	}

	/**
	 * @see com.nmote.smpp.Session#linkException(IOException)
	 */
	@Override
	protected void linkException(IOException ioe) {
		super.linkException(ioe);
		if (getState() == CLOSED) {
			scheduleReconnect();
		}
	}

	private synchronized void scheduleReconnect() {
		if (connectTask != null) {
			connectTask.cancel();
			connectTask = null;
		}

		connectTask = new TimerTask() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - this.scheduledExecutionTime() >= connectRetry / 2) {
					return;
				}

				if (isAutoConnect()) {
					int state = getState();
					if (state == CLOSED) {
						// if (state != BOUND_RX && state != BOUND_TRX && state
						// != BOUND_TX) {
						try {
							ESMESession.this.open();
							ESMESession.this.bind();
						} catch (SessionException se) {
							log.error("Connection failure while auto reconnecting: " + se);
						} catch (LinkCreationException lce) {
							log.error("Failed to create Link while auto reconnecting: " + lce);
						}
					}
				} else {
					cancel();
				}
			}
		};
		smppTimer.schedule(connectTask, 5000, connectRetry);
	}

	private long connectRetry = 5000;
	private boolean autoConnect;
	private TimerTask connectTask;
}