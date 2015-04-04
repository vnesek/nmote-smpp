/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.util.EventObject;

/**
 * SessionStateEvent.java
 *
 * @author Vjekoslav Nesek
 */
public class SessionStateEvent extends EventObject {

	private static final long serialVersionUID = About.SERIAL_VERSION_UID;

	/**
	 * Constructor for SessionStateEvent.
	 *
	 * @param session
	 *            object
	 * @param currentState
	 *            of session
	 * @param nextState
	 *            of session
	 */
	public SessionStateEvent(Session session, int currentState, int nextState) {
		super(session);
		this.currentState = currentState;
		this.nextState = nextState;
	}

	/**
	 * Returns the currentState.
	 *
	 * @return int
	 */
	public int getCurrentState() {
		return currentState;
	}

	/**
	 * Returns the nextState.
	 *
	 * @return int
	 */
	public int getNextState() {
		return nextState;
	}

	public Session getSession() {
		return (Session) getSource();
	}

	private int currentState;
	private int nextState;
}
