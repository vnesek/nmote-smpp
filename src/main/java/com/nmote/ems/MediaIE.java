/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.ems;

/**
 * MediaIE represents superclass for Animations, Sounds and Pictures inserted
 * into an EMS message
 */
public abstract class MediaIE extends IE {

	private static final long serialVersionUID = 1L;

	public MediaIE(int identifier) {
		super(identifier);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int i) {
		position = i;
	}

	int position;
}