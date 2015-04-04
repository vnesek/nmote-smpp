/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.IOException;

/**
 * Link is a bi-directional communication channel between two SMPP entities
 * (most commonly ESME and MC). It is used by Session to send and receive PDUs.
 * Links are created by LinkFactories.
 *
 * @see com.nmote.smpp.LinkFactory
 * @author Vjekoslav Nesek
 */
public interface Link {

	/**
	 * Closes link and undelying input/output streams.
	 *
	 * @throws IOException
	 *             if Link can't be closed
	 */
	public void close() throws IOException;

	/**
	 * Receives AbstractPDU from link.
	 *
	 * @return AbstractPDU
	 * @throws IOException
	 *             on IO error
	 */
	public AbstractPDU receive() throws IOException;

	/**
	 * Sends pdu over the link.
	 *
	 * @param pdu
	 *            to send
	 * @throws IOException
	 *             on IOError
	 */
	public void send(AbstractPDU pdu) throws IOException;
}
