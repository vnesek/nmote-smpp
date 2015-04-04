/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * BindingAuthorizer controls binding of an SMSC session.
 *
 * @author Vjekoslav Nesek
 */
public interface BindingAuthorizer {

	/**
	 * Check if binding can be established.
	 *
	 * @param binding
	 *            Binding object to check.
	 * @throws BindingFailedException
	 *             if binding cannot be established,
	 */
	public void check(Binding binding) throws BindingFailedException;
}
