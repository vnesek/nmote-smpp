/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.util.EventListener;

/**
 * SessionStateListener listens for state changes on a Session object.
 *
 * @author Vjekoslav Nesek
 */
public interface SessionStateListener extends EventListener {

	public void stateChanged(SessionStateEvent event);
}
