/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

/**
 * Processor processes SMPP commands. Command is processed if processor calls a
 * respond() method wih a valid response PDU. All Commands are processed in a
 * scope of an established Session.
 *
 * @author Vjekoslav Nesek
 */
public interface Processor {

	/**
	 * Processed <code>command</code> in a scope of <code>session</code>. Should
	 * call <code>Command.respond()</code> if command was processed.
	 *
	 * @param command
	 *            Command to process
	 * @param session
	 *            Session in which command was received
	 */
	public void process(Command command, Session session);
}
