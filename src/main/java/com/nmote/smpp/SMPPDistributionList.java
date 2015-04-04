/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.Serializable;

/**
 * TODO (Not tested) SMPPDistributionList
 *
 * @author Vjekoslav Nesek
 */
public class SMPPDistributionList implements Serializable {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	public SMPPDistributionList(String name) {
		setName(name);
	}

	/**
	 * Returns the name.
	 *
	 * @return distrubution list name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            The name to set
	 */
	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name can't be null");
		}
		this.name = name;
	}

	private String name;
}
