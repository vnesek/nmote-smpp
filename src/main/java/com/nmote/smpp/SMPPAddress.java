/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SMPPAddress represents GSM network address
 *
 * @author Vjekoslav Nesek
 */
public class SMPPAddress implements Serializable, Cloneable {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	/**
	 * TON - Unknown. "Unknown" is used when the user or network has no a priori
	 * information about the numbering plan. In this case, the Address-Value
	 * field is organized according to the network dialling plan, e.g. prefix or
	 * escape digits might be present.
	 */
	public static final int TON_UNKNOWN = 0x00;

	/**
	 * TON - International. Most common TOM. The international format shall be
	 * accepted also when the message is destined to a recipient in the same
	 * country as the MSC or as the SGSN.
	 */
	public static final int TON_INTERNATIONAL = 0x01;

	/**
	 * TON - National. Prefix or escape digits shall not be included.
	 */
	public static final int TON_NATIONAL = 0x02;

	/**
	 * TON - Network specific. "Network specific number" is used to indicate
	 * administration/service number specific to the serving network, e.g. used
	 * to access an operator.
	 */
	public static final int TON_NETWORK_SPECIFIC = 0x03;

	/**
	 * TON - Subscriber. "Subscriber number" is used when a specific short
	 * number representation is stored in one or more SCs as part of a higher
	 * layer application. (Note that "Subscriber number" shall only be used in
	 * connection with the proper PID referring to this application).
	 */
	public static final int TON_SUBSCRIBER = 0x04;

	/**
	 * TON - Alphanumeric. Number must be coded according to 3GPP TS 23.038 GSM
	 * 7-bit default alphabet.
	 * <p>
	 * NPI Must be set to UNKNOWN.
	 */
	public static final int TON_ALPHANUMERIC = 0x05;

	/**
	 * TON - Abbreviated.
	 */
	public static final int TON_ABBREVIATED = 0x06;

	/**
	 * NPI - Unknown
	 */
	public static final int NPI_UNKNOWN = 0x00;

	/**
	 * NPI - ISDN/Telephone. (E.164 /E.163)
	 */
	public static final int NPI_ISDN = 0x01;

	/**
	 * NPI - Data Numbering Plan (X.121)
	 */
	public static final int NPI_DATA = 0x03;

	/**
	 * NPI - Telex
	 */
	public static final int NPI_TELEX = 0x04;

	/**
	 * NPI - Land Mobile (E.212)
	 */
	public static final int NPI_LAND_MOBILE = 0x06;

	/**
	 * NPI - National
	 */
	public static final int NPI_NATIONAL = 0x08;

	/**
	 * NPI - Private
	 */
	public static final int NPI_PRIVATE = 0x09;

	/**
	 * NPI - Ermes
	 */
	public static final int NPI_ERMES = 0x0A;

	/**
	 * NPI - Internet IP
	 */
	public static final int NPI_INTERNET = 0x0E;

	/**
	 * NPI - WAP Client Id
	 */
	public static final int NPI_WAP_CLIENT_ID = 0x12;

	private static final char[] PHONE_DIGITS = "+0123456789*#ab".toCharArray();

	private static int ABBR_LEN = getPropertyInt("abbr_len", 5);

	private static int ABBR_TON = getPropertyInt("abbr_ton", TON_ABBREVIATED);

	private static int ABBR_NPI = getPropertyInt("abbr_npi", NPI_UNKNOWN);

	private static int ALPHA_TON = getPropertyInt("alpha_ton", TON_ALPHANUMERIC);

	private static int ALPHA_NPI = getPropertyInt("alpha_npi", NPI_UNKNOWN);

	private static int INT_TON = getPropertyInt("int_ton", TON_INTERNATIONAL);

	private static int INT_NPI = getPropertyInt("int_npi", NPI_ISDN);

	private static int NAT_TON = getPropertyInt("nat_ton", TON_NATIONAL);

	private static int NAT_NPI = getPropertyInt("nat_npi", NPI_ISDN);

	private static String INT_PREFIX = getPropertyString("int_prefix", "+");

	private static String INT_PREFIX_ZERO = getPropertyString("int_prefix", "00");

	private static String NAT_PREFIX_ZERO = getPropertyString("nat_prefix", "0");

	private static Logger log = LoggerFactory.getLogger(SMPPAddress.class);

	private static int getPropertyInt(String key, int def) {
		key = "com.nmote.smpp." + key;
		String s = System.getProperty(key);
		int result = def;
		if (s != null) {
			try {
				result = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				log.warn("Expected integer in system property " + key + ": " + s);
			}
		}
		// System.out.println("XX " + key +"=" + result + "/" + def + " s=" +
		// s);
		return result;
	}

	private static String getPropertyString(String key, String def) {
		key = "com.nmote.smpp." + key;
		String result = System.getProperty(key, def);
		return result;
	}

	@Deprecated
	public SMPPAddress(String phoneNumber) {
		setPhoneNumber(phoneNumber);
	}

	/**
	 * Constructor for SMPPAddress. Sets address, ton and npi.
	 *
	 * @param address
	 *            phone number or shortcode
	 * @param ton
	 *            Type Of Network
	 * @param npi
	 *            Network Plan Identificator
	 *
	 */
	public SMPPAddress(String address, int ton, int npi) {
		setAddress(address);
		setTon(ton);
		setNpi(npi);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException();
		}
	}

	/**
	 * Checks if o and this object are equal.
	 *
	 * @param o
	 *            object to test
	 * @return true if objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		boolean result;
		if (o instanceof SMPPAddress) {
			SMPPAddress a = (SMPPAddress) o;
			EqualsBuilder b = new EqualsBuilder();
			b.append(a.getTon(), getTon());
			b.append(a.getNpi(), getNpi());
			b.append(a.getAddress(), getAddress());
			result = b.isEquals();
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * Returns the address.
	 *
	 * @return String
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Returns the npi.
	 *
	 * @return int
	 */
	public int getNpi() {
		return npi;
	}

	public String getPhoneNumber() {
		String result;
		if (ton == TON_INTERNATIONAL) {
			result = INT_PREFIX + address;
		} else if (ton == TON_NATIONAL) {
			result = NAT_PREFIX_ZERO + address;
		} else {
			result = address;
		}

		return result;
	}

	/**
	 * Returns the ton.
	 *
	 * @return int
	 */
	public int getTon() {
		return ton;
	}

	/**
	 * Calculates hash code of this object.
	 *
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder b = new HashCodeBuilder();
		b.append(getTon());
		b.append(getNpi());
		b.append(getAddress());
		return b.toHashCode();
	}

	/**
	 * Sets the address.
	 *
	 * @param address
	 *            The address to set
	 */
	public void setAddress(String address) {
		if (address == null) {
			throw new IllegalArgumentException("Address is not allowed to be null");
		}
		this.address = address;
	}

	/**
	 * Sets the npi.
	 *
	 * @param npi
	 *            The npi to set
	 */
	public void setNpi(int npi) {
		this.npi = npi;
	}

	public void setPhoneNumber(String number) {
		setTon(TON_UNKNOWN);
		setNpi(NPI_UNKNOWN);
		setAddress(number);
		autoDetectTonAndNpi();
	}

	/**
	 * Sets the ton.
	 *
	 * @param ton
	 *            The ton to set
	 */
	public void setTon(int ton) {
		this.ton = ton;
	}

	/**
	 * String representation.
	 *
	 * @return a String
	 */
	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		b.append("ton", getTon());
		b.append("npi", getNpi());
		b.append("address", getAddress());
		return b.toString();
	}

	private void autoDetectTonAndNpi() {
		int len = address.length();
		if (len > 0) {
			boolean allPhoneDigits = StringUtils.containsOnly(address, PHONE_DIGITS);

			if (allPhoneDigits) {
				if (len <= ABBR_LEN) {
					ton = ABBR_TON;
					npi = ABBR_NPI;
				} else if (address.startsWith(INT_PREFIX)) {
					ton = INT_TON;
					npi = INT_NPI;
					address = address.substring(INT_PREFIX.length());
				} else if (address.startsWith(INT_PREFIX_ZERO)) {
					ton = INT_TON;
					npi = INT_NPI;
					address = address.substring(INT_PREFIX_ZERO.length());
				} else if (address.startsWith(NAT_PREFIX_ZERO)) {
					ton = NAT_TON;
					npi = NAT_NPI;
					address = address.substring(NAT_PREFIX_ZERO.length());
				}
			} else {
				ton = ALPHA_TON;
				npi = ALPHA_NPI;
			}
		}
	}

	private String address;
	private int ton;

	private int npi;
}
