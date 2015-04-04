/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.util.EventObject;

/**
 * CommandStateEvent is generated when Command's state changes.
 * CommandStateListener implementations can be notifed about command events.
 *
 * @see com.nmote.smpp.CommandStateListener
 * @author Vjekoslav Nesek
 */
public class CommandStateEvent extends EventObject {
	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	/**
	 * Creates new CommandStateEvent instance.
	 *
	 * @param source
	 *            Source command
	 * @param currentState
	 *            Command's current state
	 * @param nextState
	 *            Command's next state
	 */
	public CommandStateEvent(Command source, int currentState, int nextState) {
		super(source);
		if (source == null) {
			throw new NullPointerException("Source == null");
		}
		this.currentState = currentState;
		this.nextState = nextState;
	}

	public Command getCommand() {
		return (Command) getSource();
	}

	/**
	 * Returns command's current state.
	 *
	 * @return current state of command
	 */
	public int getCurrentState() {
		return currentState;
	}

	/**
	 * Returns command's next state.
	 *
	 * @return next state of command
	 */
	public int getNextState() {
		return nextState;
	}

	private final int currentState;
	private final int nextState;
}