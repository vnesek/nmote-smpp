/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Binding contains binding information for establishing session. Binding
 * information include systemId (aka user name), system type, password, esme
 * address range. Binding use is two fold, ESMESession uses it for binding to
 * SMSC while SMSCSession uses Binding to authorize/authenticate ESME.
 *
 * @see com.nmote.smpp.ESMESession
 * @see com.nmote.smpp.SMSCSession
 * @author Vjekoslav Nesek
 */
public class Binding implements Serializable {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	/**
	 * Instantiates a new Binding instance.
	 */
	public Binding() {
	}

	/**
	 * Instantiates a new Binding instance with a given systemId, systemType and
	 * password.
	 *
	 * @param systemId
	 * @param systemType
	 * @param password
	 */
	public Binding(String systemId, String systemType, String password) {
		setSystemId(systemId);
		setSystemType(systemType);
		setPassword(password);
	}

	/**
	 * Instantiates a new Binding instance with a given systemId, systemType and
	 * password. If receiver is true Binding will be used for receiving messages
	 * and if transmitter is true Binding will be used for sending SMS messages.
	 * Both receiver and transmitter can be set simoultaenously.
	 *
	 * @param systemId
	 * @param systemType
	 * @param password
	 * @param receiver
	 * @param transmitter
	 */
	public Binding(String systemId, String systemType, String password, boolean receiver, boolean transmitter) {
		setSystemId(systemId);
		setSystemType(systemType);
		setPassword(password);
		setReceiver(receiver);
		setTransmitter(transmitter);
	}

	/**
	 * Checks if o and this object are equal.
	 *
	 * @return true if objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		boolean result;
		if (o != null && o instanceof Binding) {
			Binding binding = (Binding) o;
			EqualsBuilder b = new EqualsBuilder();
			b.append(systemId, binding.systemId);
			b.append(systemType, binding.systemType);
			b.append(receiver, binding.receiver);
			b.append(transmitter, binding.transmitter);
			b.append(password, binding.password);
			b.append(addressRange, binding.addressRange);

			result = b.isEquals();
		} else {
			result = false;
		}

		return result;
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
	 * Returns the systemType.
	 *
	 * @return String
	 */
	public String getSystemType() {
		return systemType;
	}

	/**
	 * Calculates hash code of this object.
	 *
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder b = new HashCodeBuilder();

		b.append(systemId);
		b.append(systemType);
		b.append(receiver);
		b.append(transmitter);
		b.append(password);
		b.append(addressRange);

		return b.toHashCode();
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
	 * Sets the systemType.
	 *
	 * @param systemType
	 *            The systemType to set
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
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

	/**
	 * Creates a string representation of this Binding instance.
	 *
	 * @return a String
	 */
	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);

		b.append("systemId", systemId);
		b.append("systemType", systemType);
		b.append("password", password);
		b.append("t", transmitter);
		b.append("r", receiver);
		if (addressRange != null) {
			b.append("addressRange", addressRange);
		}

		return b.toString();
	}

	private String systemId;
	private String systemType;
	private String password;
	private boolean receiver = true;
	private boolean transmitter = true;
	private SMPPAddress addressRange;
}