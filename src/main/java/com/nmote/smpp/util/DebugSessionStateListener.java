/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmote.smpp.SessionStateEvent;
import com.nmote.smpp.SessionStateListener;

/**
 * DebugSessionStateListener.java
 *
 * @author Vjekoslav Nesek
 */
public class DebugSessionStateListener implements SessionStateListener {

	private static Logger log = LoggerFactory.getLogger(DebugSessionStateListener.class);

	/**
	 * @see com.nmote.smpp.SessionStateListener#stateChanged(SessionStateEvent)
	 */
	@Override
	public void stateChanged(SessionStateEvent event) {
		log.debug("State change {}, {} -> {}", event.getSession(), event.getCurrentState(), event.getNextState());
	}
}
