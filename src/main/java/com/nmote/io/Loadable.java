/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loadable objects can be loaded from InputStream
 */
public interface Loadable {

	/**
	 * Loads instance data from InputStream
	 *
	 * @param in
	 * @throws IOException
	 */
	void load(InputStream in) throws IOException;

}
