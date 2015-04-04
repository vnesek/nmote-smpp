/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commands tie together PDU requests and responses. Commands are executed
 * against Sessions.
 *
 * @see com.nmote.smpp.Session
 * @author Vjekoslav Nesek
 */
public class Command {
	/**
	 * Command is in a <code>NEW</code> state before it is associated with a
	 * Session
	 */
	public static final int NEW = 0;

	/** Command is <code>QUEUED</code> when command enters output queue */
	public static final int QUEUED = 1;

	/** Command is <code>SENT</code> after sending it to a remote entity */
	public static final int SENT = 2;

	/** Command is <code>TIMEDOUT</code> if command times out */
	public static final int TIMEDOUT = 3;

	/**
	 * Command is <code>CANCELLED</code> if cancel is called before command is
	 * sent
	 */
	public static final int CANCELLED = 4;

	/**
	 * Command is <code>EXECUTED</code> when response from remote entity is
	 * received, or when oneway command is SENT to remote entity
	 */
	public static final int EXECUTED = 5;

	/** Command is <code>RESPONDED</code> when processor calls respond on it */
	public static final int RESPONDED = 6;

	private static Logger log = LoggerFactory.getLogger(Command.class);

	private static final boolean TRACE_STATE_CHANGE = false;

	public Command(AbstractPDU request) {
		this.request = request;
	}

	public synchronized void cancel() {
		if (state < SENT) {
			if (TRACE_STATE_CHANGE) {
				log.debug("Cancelled");
			}
			setState(CANCELLED);
			this.notifyAll();
		}
	}

	/**
	 * Returns CommandStateListener registered for this command.
	 *
	 * @return CommandStateListener registered for this command.
	 */
	public CommandStateListener getCommandStateListener() {
		return commandStateListener;
	}

	public AbstractPDU getRequest() {
		return request;
	}

	public AbstractPDU getResponse() {
		return response;
	}

	/**
	 * Returns the command state.
	 *
	 * @return int the command state
	 */
	public int getState() {
		return state;
	}

	@Override
	public int hashCode() {
		return request.getSequence();
	}

	public synchronized void respond() {
		if (TRACE_STATE_CHANGE) {
			log.debug("Responded");
		}
		setState(RESPONDED);
		this.notifyAll();
	}

	public synchronized void respond(AbstractPDU response) {
		if (response.isRequestPDU()) {
			throw new IllegalArgumentException("Expected response PDU");
		}
		this.response = response;
		respond();
	}

	/**
	 * Sets CommandStateListener registered for this command. State change
	 * events will be reported on this listener.
	 *
	 * @param listener
	 *            command state listener
	 */
	public void setCommandStateListener(CommandStateListener listener) {
		commandStateListener = listener;
	}

	/**
	 * String representation.
	 *
	 * @return a String
	 */
	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		b.append("request", request);
		b.append("response", response);
		b.append("state", state);
		return b.toString();
	}

	public synchronized boolean waitForResponse(long timeout) {
		if (state != EXECUTED) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
		}
		return state == EXECUTED;
	}

	synchronized void oneWayCompleted() {
		if (TRACE_STATE_CHANGE) {
			log.debug("Executed oneway");
		}
		setState(EXECUTED);
		this.notifyAll();
	}

	synchronized void queued() {
		if (state < QUEUED) {
			if (TRACE_STATE_CHANGE) {
				log.debug("Queued");
			}
			setState(QUEUED);
		}
	}

	synchronized void requestSent() {
		if (state < SENT) {
			if (TRACE_STATE_CHANGE) {
				log.debug("Sent");
			}
			setState(SENT);
		}
	}

	synchronized void responseReceived(AbstractPDU response) {
		if (TRACE_STATE_CHANGE) {
			log.debug("Executed");
		}
		this.response = response;
		setState(EXECUTED);
		this.notifyAll();
	}

	synchronized void timedOut() {
		if (state != EXECUTED) {
			if (TRACE_STATE_CHANGE) {
				log.debug("Timedout");
			}
			setState(TIMEDOUT);
			this.notifyAll();
		}
	}

	private void fireStateChanged(int currentState, int nextState) {
		if (commandStateListener != null) {
			commandStateListener.stateChanged(new CommandStateEvent(this, currentState, nextState));
		}
	}

	private void setState(int newState) {
		if (newState != this.state) {
			fireStateChanged(this.state, newState);
			this.state = newState;
		}
	}

	private final AbstractPDU request;
	private AbstractPDU response;
	private int state = NEW;
	private CommandStateListener commandStateListener;
}