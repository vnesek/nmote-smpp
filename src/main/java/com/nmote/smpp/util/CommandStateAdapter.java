/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp.util;

import com.nmote.smpp.Command;
import com.nmote.smpp.CommandStateEvent;
import com.nmote.smpp.CommandStateListener;

/**
 * <p>
 * CommandStateAdapter is an adapter class that implements CommandStateListener
 * and dispatches state change events to a set of methods. Default method bodies
 * are empty, subclasses (perhaps anonymous) should overide methods they are
 * interested in.
 * </p>
 *
 * <p>
 * Followin example shows how to use this class when submitting message
 * asynchronously:
 * </p>
 *
 * <pre>
 * Command submit = new Command(req);
 * 
 * // Set a CommandStateListener that will be called when
 * // command completes execution
 * submit.setCommandStateListener(new CommandStateAdapter() {
 * 	public void executed(Command cmd) {
 * 		SubmitSmRespPDU resp = (SubmitSmRespPDU) cmd.getResponse();
 * 		System.out.println(&quot;Status &quot; + resp.getStatus());
 * 		System.out.println(&quot;Message ID &quot; + resp.getMessageId());
 * 	}
 * });
 * 
 * // Execute command asynchronously
 * s.executeAsync(submit);
 * </pre>
 *
 * @see com.nmote.smpp.Command
 * @author Vjekoslav Nesek
 */
public abstract class CommandStateAdapter implements CommandStateListener {

	/**
	 * Called when command is cancelled by a client.
	 *
	 * @param cmd
	 */
	public void cancelled(Command cmd) {
	}

	/**
	 * Called when command is executed i.e. response is received from remote
	 * entity.
	 *
	 * @param cmd
	 */
	public void executed(Command cmd) {
	}

	/**
	 * Called when command is queued into outgoing command queue.
	 *
	 * @param cmd
	 */
	public void queued(Command cmd) {
	}

	/**
	 * Called when command is responded i.e. client sends response to requestor.
	 *
	 * @param cmd
	 */
	public void responded(Command cmd) {
	}

	/**
	 * Called when command is sent to remote entity.
	 *
	 * @param cmd
	 */
	public void sent(Command cmd) {
	}

	/**
	 * @see com.nmote.smpp.CommandStateListener#stateChanged(com.nmote.smpp.CommandStateEvent)
	 */
	@Override
	public void stateChanged(CommandStateEvent event) {
		Command cmd = event.getCommand();
		switch (event.getNextState()) {
		case Command.CANCELLED:
			cancelled(cmd);
			break;
		case Command.EXECUTED:
			executed(cmd);
			break;
		case Command.QUEUED:
			queued(cmd);
			break;
		case Command.SENT:
			sent(cmd);
			break;
		case Command.TIMEDOUT:
			timedOut(cmd);
			break;
		case Command.RESPONDED:
			responded(cmd);
			break;
		}
	}

	/**
	 * Called when command times out.
	 *
	 * @param cmd
	 */
	public void timedOut(Command cmd) {
	}
}