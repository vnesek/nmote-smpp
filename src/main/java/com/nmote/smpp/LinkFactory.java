/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * LinkFactory creates Link instances. LinkFactory is used by Session to
 * establish link with other entity's Session.
 *
 * @see com.nmote.smpp.Link
 * @see com.nmote.smpp.Session
 * @author Vjekoslav Nesek
 */
public interface LinkFactory {

	/**
	 * Creates a new Link instance.
	 *
	 * @return a Link instance
	 * @throws LinkCreationException
	 *             if Link can't be created.
	 */
	public Link createLink() throws LinkCreationException;
}
