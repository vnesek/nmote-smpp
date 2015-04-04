/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * SMPPAddressConverter is a utility class used to convert SMPPAddresses to and
 * from String format.
 */
public interface SMPPAddressConverter {

	public SMPPAddress toSMPPAddress(String s);

	public String toString(SMPPAddress a);
}
