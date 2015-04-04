/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * Prints usage copyright, version and dependencies information to a standard
 * output.
 *
 * @author Vjekoslav Nesek
 */
public class About {

	public static final int MAJOR = 1;
	public static final int MINOR = 7;
	public static final int REVISION = 0;

	static final long SERIAL_VERSION_UID = MAJOR * 100 + MINOR * 10 + REVISION;

	public static final String VERSION = MAJOR + "." + MINOR + "." + REVISION;
	public static final String COPYRIGHT = "Nmote-SMPP " + VERSION
			+ ". Copyright (c) Nmote ltd. 2003-2015. All rights reserved.";

	public static void main(String[] args) {
		System.out.println(COPYRIGHT);
		System.out.println();
		System.out.println("Implements:           SMPP protocol (see http://www.smpp.org)");
		System.out.println("Runtime dependencies: slf4j, commons-lang3");
		System.out.println("Library version:      " + VERSION);
	}
}
