/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Saveable objects can be saved to OutputStream.
 */
public interface Saveable {

	/**
	 * Saves instance data to OutputStream.
	 *
	 * @param out
	 *            output stream
	 * @throws IOException
	 *             if writing fails
	 */
	void save(OutputStream out) throws IOException;

}
