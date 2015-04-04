/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.util.EventListener;

/**
 * CommandStateListener listens for state changes on a command. By registering
 * command state listener on a command you can be notified when asynchronously
 * executed command completes execution, timeouts, etc.
 *
 * @author Vjekoslav Nesek
 */
public interface CommandStateListener extends EventListener {

	/**
	 * Called when command state changes
	 *
	 * @param event
	 */
	void stateChanged(CommandStateEvent event);
}
