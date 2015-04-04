/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * BindingAuthorizer implementation that checks systemType, systemId, password
 * and binding mode.
 *
 * @author Vjekoslav Nesek
 */
public class BasicBindingAuthorizer implements BindingAuthorizer {

	/**
	 * @see BindingAuthorizer#check(Binding)
	 */
	@Override
	public void check(Binding binding) throws BindingFailedException {
		if (systemType != null && !systemType.equals(binding.getSystemType())) {
			throw new BindingFailedException(ESMEStatus.INVSYSTYP);
		}

		if (systemId != null && !systemId.equals(binding.getSystemId())) {
			throw new BindingFailedException(ESMEStatus.INVSYSID);
		}

		if (password != null && !password.equals(binding.getPassword())) {
			throw new BindingFailedException(ESMEStatus.INVPASWD);
		}

		if (binding.isReceiver() && !receiver) {
			throw new BindingFailedException("Can't bind as a receiver", ESMEStatus.BINDFAIL);
		}

		if (binding.isTransmitter() && !transmitter) {
			throw new BindingFailedException("Can't bind as a transmitter", ESMEStatus.BINDFAIL);
		}
	}

	/**
	 * Returns the addressRange.
	 *
	 * @return SMPPAddress
	 */
	public SMPPAddress getAddressRange() {
		return addressRange;
	}

	/**
	 * Returns the password.
	 *
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the systemId.
	 *
	 * @return String
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * Returns the user.
	 *
	 * @return String
	 */
	public String getSystemType() {
		return systemType;
	}

	/**
	 * Returns the receiver.
	 *
	 * @return boolean
	 */
	public boolean isReceiver() {
		return receiver;
	}

	/**
	 * Returns the transmitter.
	 *
	 * @return boolean
	 */
	public boolean isTransmitter() {
		return transmitter;
	}

	/**
	 * Sets the addressRange.
	 *
	 * @param addressRange
	 *            The addressRange to set
	 */
	public void setAddressRange(SMPPAddress addressRange) {
		this.addressRange = addressRange;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *            The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the receiver.
	 *
	 * @param receiver
	 *            The receiver to set
	 */
	public void setReceiver(boolean receiver) {
		this.receiver = receiver;
	}

	/**
	 * Sets the systemId.
	 *
	 * @param systemId
	 *            The systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * Sets the user.
	 *
	 * @param user
	 *            The user to set
	 */
	public void setSystemType(String user) {
		this.systemType = user;
	}

	/**
	 * Sets the transmitter.
	 *
	 * @param transmitter
	 *            The transmitter to set
	 */
	public void setTransmitter(boolean transmitter) {
		this.transmitter = transmitter;
	}

	private String systemType;
	private String password;
	private String systemId;
	private SMPPAddress addressRange;
	private boolean receiver = true;
	private boolean transmitter = true;
}