/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmote.util.Executor;
import com.nmote.util.NewThreadExecutor;

/**
 * Session controls SMPP communication dialog. Session maintains an outgoing
 * command queue and a chain of processors that execute incoming commands. Two
 * threads are started by Session, one receives PDUs from link, other sends
 * PDUs. Chain of a processors is executed in a separate task that is executed
 * in a processorsThreadPool. You can supply another implementation of
 * ThreadPool interface.
 */
public class Session {

	private class AutoEnquireLinkTask extends TimerTask implements CommandStateListener {

		@Override
		public void run() {
			// log.info("Testing link");
			if (System.currentTimeMillis() - getLastActivity() > autoEnquireLink) {
				Command command = new Command(new EnquireLinkPDU());
				command.setCommandStateListener(this);
				try {
					executeAsync(command);
				} catch (SessionException e) {
					log.error("Enquire link failed", e);
				}
			}
		}

		@Override
		public void stateChanged(CommandStateEvent event) {
			if (event.getNextState() == Command.EXECUTED) {
				// log.info("Link ok");
			} else if (event.getNextState() == Command.TIMEDOUT) {
				// log.info("Link timedout");
			}
		}

	}

	private class Receiver implements Runnable {
		@Override
		public void run() {
			receiver = Thread.currentThread();

			final boolean debug = log.isDebugEnabled();
			if (debug) {
				log.debug("Receiver started");
			}
			while (getState() != CLOSED && link != null) {
				try {
					// Receive pdu
					AbstractPDU pdu = link.receive();

					// Set a version on a PDU
					pdu.version = getRemoteVersion();

					touch();

					if (pdu.isRequestPDU()) {
						if (debug) {
							log.debug("Request received " + pdu);
						}
						requestReceived(pdu);
					} else {
						if (debug) {
							log.debug("Response received  " + pdu);
						}
						// Lookup command in a set of pending commands
						Integer key = new Integer(pdu.getSequence());
						Command cmd = pending.remove(key);
						if (cmd != null) {
							cmd.responseReceived(pdu);
						} else {
							log.warn("Received response without associated request " + pdu);
						}
					}
				} catch (IOException e) {
					linkException(e);
				} catch (Throwable t) {
					log.error("Fatal error", t);
				}
			}
			if (debug) {
				log.debug("Receiver finished");
			}

			receiver = null;
		}

		@Override
		public String toString() {
			return "SMPP Receiver " + link;
		}
	}

	private class Sender implements Runnable {
		@Override
		public void run() {
			final boolean debug = log.isDebugEnabled();
			if (debug) {
				log.debug("Sender started");
			}
			while (getState() != CLOSED) {
				try {
					// Retrieve pdu to send
					Command cmd;
					synchronized (outgoing) {
						if (outgoing.size() > 0) {
							cmd = (Command) outgoing.remove();
						} else {
							try {
								outgoing.wait();
							} catch (InterruptedException e) {
								assert false : "Sender interrupted";
							}

							if (getState() == CLOSED) {
								break;
							}

							if (outgoing.isEmpty()) {
								continue;
							}

							cmd = (Command) outgoing.remove();
						}
					}

					if (cmd.getState() == Command.QUEUED) {
						AbstractPDU pdu = cmd.getRequest();

						// Set a sequence number for PDU
						pdu.sequence = nextSequence();

						// Set a protocol version on a pdu
						pdu.version = getRemoteVersion();

						// Determine if PDU is one way
						final boolean oneWay = pdu.isOneWay();

						Integer key = new Integer(pdu.getSequence());
						try {
							if (debug) {
								log.debug("Sending request " + pdu);
							}
							if (!oneWay) {
								pending.put(key, cmd);
							}
							link.send(pdu);
							cmd.requestSent();
							if (oneWay) {
								cmd.oneWayCompleted();
							}
							touch();
						} catch (IOException ioe) {
							if (!oneWay) {
								pending.remove(key);
							}
							linkException(ioe);
						}
					} else {
						if (log.isDebugEnabled()) {
							log.debug("Skipping command " + cmd);
						}
					}
				} catch (Throwable t) {
					log.error("Fatal error", t);
				}
			}
			if (debug) {
				log.debug("Sender finished");
			}
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "SMPP Sender " + link;
		}
	}

	public static final int BOUND_RX = 2;

	public static final int BOUND_TRX = 4;
	public static final int BOUND_TX = 3;
	public static final int CLOSED = 0;

	public static final int OPEN = 1;

	public static final int OUTBOUND = 6;

	public static final int UNBOUND = 5;

	static Logger log = LoggerFactory.getLogger(Session.class);

	static Timer smppTimer = new Timer(true);

	/**
	 * Constructor for Session.
	 */
	public Session() {
		touch();
		DispatchProcessor ss = new DispatchProcessor();
		ss.setDefaultProcessor(GenericNackProcessor.INSTANCE);
		ss.setProcessor(PDU.ENQUIRE_LINK, EnquireLinkProcessor.INSTANCE);
		ss.setProcessor(PDU.UNBIND, UnbindProcessor.INSTANCE);
		setIncomingProcessor(ss);
	}

	/**
	 * Instantiates a new Session with a given link.
	 *
	 * @param link
	 *            communication link
	 */
	public Session(Link link) {
		this();
		this.link = link;
	}

	/**
	 * Constructor for Session.
	 *
	 * @param linkFactory
	 *            factory for link creation
	 */
	public Session(LinkFactory linkFactory) {
		this();
		setLinkFactory(linkFactory);
	}

	public void addSessionStateListener(SessionStateListener l) {
		if (sessionStateListeners == null) {
			sessionStateListeners = new ArrayList<SessionStateListener>();
		}
		sessionStateListeners.add(l);
	}

	@SuppressWarnings("unchecked")
	public synchronized void close() {
		if (getState() != CLOSED) {
			setState(CLOSED);

			// Stop auto enquire link
			if (autoEnquireLinkTask != null) {
				autoEnquireLinkTask.cancel();
				autoEnquireLinkTask = null;
			}

			synchronized (outgoing) {
				outgoing.notifyAll();
			}

			// Close all
			if (link != null) {
				try {
					link.close();
				} catch (IOException e) {
					log.error("Closing link failed", e);
				} finally {
					link = null;
				}
			}

			synchronized (outgoing) {
				cancelCommands(outgoing);
				outgoing.clear();
			}

			synchronized (pending) {
				cancelCommands(pending.values());
				pending.clear();
			}

			log.debug("SMPP link closed");
		} else {
			log.debug("Session already closed");
		}
	}

	/**
	 * Enquires Link between entities.
	 *
	 * @throws SessionClosedException
	 *             if link is closed
	 */
	public void enquireLink() throws SessionException {
		enquireLink(timeout);
	}

	/**
	 * Enquires Link between entities.
	 *
	 * @param timeout
	 *            maximum time in ms to wait for response. If set to 0 wait
	 *            forever.
	 * @throws SessionClosedException
	 *             if link is closed
	 */
	public void enquireLink(long timeout) throws SessionException {
		Command cmd = new Command(new EnquireLinkPDU());
		execute(cmd, timeout);
		AbstractPDU resp = cmd.getResponse();
		if (resp.getStatus() != ESMEStatus.OK) {
			throw new SessionException("");
		}
	}

	/**
	 * Executes command synchronously. Waits for a response up to timeout
	 * miliseconds (specified by session timeout property) and if response
	 * wasn't received throws CommandTimedOutException.
	 *
	 * @param command
	 *            Command to execute.
	 * @throws CommandTimedOutException
	 *             if command has timed out
	 * @throws CommandCancelledException
	 *             if command was cancelled
	 * @throws GenericNackException
	 *             if GenericNackPDU is received insead of correct PDU
	 */
	public void execute(Command command) throws SessionException {
		execute(command, timeout);
	}

	/**
	 * Executes command synchronously. Waits for a response up to timeout
	 * miliseconds and if response wasn't received throws
	 * CommandTimedOutException.
	 *
	 * @param command
	 *            Command to execute.
	 * @param timeout
	 *            Maximum time in ms to wait for response. If set to 0 wait
	 *            forever.
	 * @throws CommandTimedOutException
	 *             if command has timed out
	 * @throws CommandCancelledException
	 *             if command was cancelled
	 * @throws GenericNackException
	 *             if GenericNackPDU is received insead of correct PDU
	 */
	public void execute(Command command, long timeout) throws SessionException {
		if (Thread.currentThread() == receiver) {
			throw new IllegalStateException("Can't execute commands from PDU receiver thread");
		}

		executeAsync(command);
		if (command.waitForResponse(timeout) == false) {
			assert command.getState() != Command.EXECUTED;
			if (command.getState() == Command.CANCELLED) {
				throw new CommandCancelledException();
			} else {
				command.timedOut();
				throw new CommandTimedOutException("timeout=" + timeout + " ms");
			}
		}
		AbstractPDU response = command.getResponse();
		AbstractPDU request = command.getRequest();

		assert response != null : "Response is null";

		if (response instanceof GenericNackPDU) {
			throw new GenericNackException(response.getStatus());
		}

		// Check if command id's match
		assert (response.getCommandId() & 0x8FFFFFFF) == request.getCommandId() : "Request and response command id's don't match";

		assert response.getSequence() != request.getSequence() : "Request sequence != response sequence";
	}

	/**
	 * Executes command asynchronously. Use Command.waitForResponse() method to
	 * wait for a response PDU. This method returns immediately.
	 *
	 * @param command
	 *            Command to execute.
	 * @throws SessionException
	 *             if error occures while queuing command
	 * @throws IllegalStateException
	 *             if command is already executing
	 */
	@SuppressWarnings("unchecked")
	public void executeAsync(Command command) throws SessionException {
		if (getState() == CLOSED) {
			throw new SessionClosedException();
		}

		if (remoteVersion > 0 && remoteVersion < 0x34) {
			List<Parameter> optional = command.getRequest().getParameters();
			if (optional != null && optional.size() > 0) {
				throw new SessionException("Sending optional parameters to remote entity with version "
						+ Integer.toHexString(remoteVersion) + " is prohibited");
			}
		}

		if (command.getState() != Command.NEW) {
			throw new IllegalStateException("Command isn't in a NEW state");
		}

		// Add command to outgoing queue
		synchronized (outgoing) {
			command.queued();
			outgoing.add(command);
			outgoing.notify();
		}
	}

	/**
	 * Returns the autoEnquireLink.
	 *
	 * @return long
	 */
	public long getAutoEnquireLink() {
		return autoEnquireLink;
	}

	/**
	 * Returns the binding.
	 *
	 * @return Binding
	 */
	public Binding getBinding() {
		return binding;
	}

	/**
	 * Returns the executor.
	 *
	 * @return Executor
	 */
	public Executor getExecutor() {
		return executor;
	}

	/**
	 * Returns processor for incomming messages.
	 *
	 * @return Processor for incomming messages
	 */
	public Processor getIncomingProcessor() {
		return incomingProcessor;
	}

	/**
	 * Returns Exector used to executed IO related threads.
	 *
	 * @return Executor for IO threads
	 */
	public Executor getIoThreadsExecutor() {
		return ioThreadsExecutor;
	}

	/**
	 * Returns the lastActivity.
	 *
	 * @return long
	 */
	public long getLastActivity() {
		return lastActivity;
	}

	/**
	 * Returns the linkFactory.
	 *
	 * @return LinkFactory
	 */
	public LinkFactory getLinkFactory() {
		return linkFactory;
	}

	/**
	 * Returns the localVersion.
	 *
	 * @return int
	 */
	public int getLocalVersion() {
		return localVersion;
	}

	/**
	 * Returns the maxConcurrentCommands.
	 *
	 * @return int
	 */
	public int getMaxConcurrentCommands() {
		return maxConcurrentCommands;
	}

	/**
	 * Returns the remoteVersion.
	 *
	 * @return int
	 */
	public int getRemoteVersion() {
		return remoteVersion;
	}

	/**
	 * Returns the state.
	 *
	 * @return int
	 */
	public int getState() {
		return state;
	}

	/**
	 * Returns the timeout.
	 *
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	public boolean isCloseWhenUnbound() {
		return closeWhenUnbound;
	}

	public synchronized void open() throws LinkCreationException {
		if (getState() == CLOSED) {
			// Create a link if not already created
			if (link == null) {
				if (linkFactory == null) {
					throw new LinkCreationException("LinkFactory not set");
				}
				link = linkFactory.createLink();
			}
			setState(OPEN);

			// Start sender thread
			ioThreadsExecutor.exec(new Sender(), Thread.NORM_PRIORITY);

			// Start receiver thread
			ioThreadsExecutor.exec(new Receiver(), Thread.NORM_PRIORITY);

			log.debug("SMPP link opened");

			// Start auto enquire link
			if (this.autoEnquireLink > 0) {
				long freq = autoEnquireLink;
				autoEnquireLinkTask = new AutoEnquireLinkTask();
				smppTimer.schedule(autoEnquireLinkTask, freq, freq);
			}
		} else {
			log.debug("Session already open");
		}
	}

	public void removeSessionStateListener(SessionStateListener l) {
		if (sessionStateListeners != null) {
			sessionStateListeners.remove(l);
		}
	}

	/**
	 * Sets the autoEnquireLink.
	 *
	 * @param autoEnquireLink
	 *            The autoEnquireLink to set
	 */
	public void setAutoEnquireLink(long autoEnquireLink) {
		if (autoEnquireLink < 0) {
			throw new IllegalArgumentException();
		}
		synchronized (this) {
			if (autoEnquireLinkTask != null && autoEnquireLink == 0) {
				autoEnquireLinkTask.cancel();
				autoEnquireLinkTask = null;
			}
		}
		;
		this.autoEnquireLink = autoEnquireLink;
	}

	/**
	 * Sets the binding.
	 *
	 * @param binding
	 *            The binding to set
	 */
	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public void setCloseWhenUnbound(boolean b) {
		closeWhenUnbound = b;
	}

	/**
	 * Sets the executor.
	 *
	 * @param executor
	 *            The executor to set
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public void setIncomingProcessor(Processor processor) {
		if (processor == null) {
			throw new NullPointerException("processor == null");
		}
		incomingProcessor = processor;
	}

	public void setIoThreadsExecutor(Executor executor) {
		if (executor == null) {
			throw new NullPointerException("IOThreadsExecutor cannot be null");
		}
		ioThreadsExecutor = executor;
	}

	/**
	 * Sets the linkFactory.
	 *
	 * @param linkFactory
	 *            The linkFactory to set
	 */
	public void setLinkFactory(LinkFactory linkFactory) {
		if (linkFactory == null) {
			throw new NullPointerException("LinkFactory == null");
		}
		this.linkFactory = linkFactory;
	}

	/**
	 * Sets the local SMPP protocol version.
	 *
	 * @param localVersion
	 *            The localVersion to set
	 */
	public void setLocalVersion(int localVersion) {
		this.localVersion = localVersion;
	}

	/**
	 * Sets the maxConcurrentCommands.
	 *
	 * @param maxConcurrentCommands
	 *            The maxConcurrentCommands to set
	 */
	public void setMaxConcurrentCommands(int maxConcurrentCommands) {
		if (maxConcurrentCommands < 1) {
			throw new IllegalArgumentException("Max < 1:" + maxConcurrentCommands);
		}
		this.maxConcurrentCommands = maxConcurrentCommands;
	}

	/**
	 * Sets the remoteVersion.
	 *
	 * @param remoteVersion
	 *            The remoteVersion to set
	 */
	public void setRemoteVersion(int remoteVersion) {
		this.remoteVersion = remoteVersion;
	}

	/**
	 * Sets the timeout.
	 *
	 * @param timeout
	 *            The timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * String representation.
	 *
	 * @return a String
	 */
	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		b.append("state", getState());
		b.append("version", getLocalVersion());
		b.append("remoteVersion", getRemoteVersion());
		b.append("linkFactory", getLinkFactory());
		if (timeout != 0) {
			b.append("timeout", getTimeout());
		}
		b.append("seq", sequence);
		b.append("pending", pending.size());
		b.append("outgoing", outgoing.size());
		return b.toString();
	}

	/**
	 * Unbinds from remote entity.
	 *
	 * @throws SessionException
	 *             if unbinding fails
	 */
	public void unbind() throws SessionException {
		unbind(timeout);
	}

	/**
	 * Unbinds from remote entity.
	 *
	 * @param timeout
	 *            maximum time in ms to wait for response. If set to 0 wait
	 *            forever.
	 * @throws SessionException
	 *             if unbinding fails
	 */
	public void unbind(long timeout) throws SessionException {
		Command cmd = new Command(new UnbindPDU());
		execute(cmd, timeout);
		AbstractPDU resp = cmd.getResponse();
		int status = resp.getStatus();
		if (status != ESMEStatus.OK) {
			throw new SessionException("Unbind failed", status);
		}
		setState(UNBOUND);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			int s = getState();
			if (s == BOUND_RX || s == BOUND_TX || s == BOUND_TRX) {
				unbind();
			}
		} finally {
			close();
		}
	}

	protected void fireSessionChanged(int currentState, int nextState) {
		List<SessionStateListener> listeners = sessionStateListeners;
		if (listeners != null) {
			SessionStateEvent event = new SessionStateEvent(this, currentState, nextState);
			for (Iterator<SessionStateListener> i = listeners.iterator(); i.hasNext();) {
				SessionStateListener l = i.next();
				try {
					l.stateChanged(event);
				} catch (Throwable t) {
					log.error("Session state listener " + l + " throwed exception", t);
				}
			}
		}
	}

	/**
	 * Processes exception occurred on link. Closes session.
	 *
	 * @param ioe
	 *            exception
	 */
	protected synchronized void linkException(IOException ioe) {
		if (getState() == CLOSED) {
			log.debug("Link is closing");
		} else if (ioe instanceof EOFException) {
			log.debug("EOF on link", ioe);
			close();
		} else {
			log.error("Link error", ioe);
			close();
		}
	}

	protected synchronized int nextSequence() {
		return ++sequence;
	}

	/**
	 * Called every time PDU is received or sent.
	 */
	protected void touch() {
		lastActivity = System.currentTimeMillis();
	}

	void setState(int state) {
		if (state < CLOSED || state > OUTBOUND) {
			throw new IllegalStateException();
		}
		fireSessionChanged(this.state, state);
		this.state = state;
	}

	private void cancelCommands(Collection<Command> c) {
		for (Command command : c) {
			try {
				command.cancel();
			} catch (Exception e) {
				log.warn("Command.cancel() throwed exception", e);
			}
		}
	}

	private void processIncomingCommand(final Command command) {
		assert incomingProcessor != null : "IncomingProcessor = null";
		incomingProcessor.process(command, Session.this);

		// Respond with generic_nack if there is no other response
		if (command.getState() != Command.RESPONDED) {
			GenericNackPDU nack = new GenericNackPDU();
			nack.sequence = command.getRequest().getSequence();
			nack.setStatus(ESMEStatus.SYSERR);
			command.respond(nack);
		}

		AbstractPDU response = command.getResponse();
		if (response != null) {
			if (log.isDebugEnabled()) {
				log.debug("Sending response " + response);
			}
			Link l = link;
			if (l != null) {
				try {
					link.send(response);
				} catch (IOException ioe) {
					linkException(ioe);
				}
			}
		}

		// Close session if we are unbound
		if (isCloseWhenUnbound() && getState() == UNBOUND) {
			close();
		}
	}

	private void requestReceived(AbstractPDU pdu) {
		final Command command = new Command(pdu);

		// Execute Command
		if (executor != null) {
			executor.exec(new Runnable() {
				@Override
				public void run() {
					try {
						processIncomingCommand(command);
					} catch (Throwable t) {
						log.error("Processing incoming command failed", t);
					}
				}
			}, Thread.NORM_PRIORITY);
		} else {
			processIncomingCommand(command);
		}
	}

	private long autoEnquireLink = 0;
	private TimerTask autoEnquireLinkTask;
	private boolean closeWhenUnbound = true;
	private Binding binding;
	private Executor executor;
	private Executor ioThreadsExecutor = NewThreadExecutor.INSTANCE;
	private Processor incomingProcessor;
	private long lastActivity;
	private Link link;
	private LinkFactory linkFactory;
	private int localVersion = 0x50;
	private int maxConcurrentCommands = 35;
	private Buffer outgoing = new UnboundedFifoBuffer();
	private Map<Integer, Command> pending = new ConcurrentHashMap<>(43);
	private Thread receiver;
	private int remoteVersion;
	private volatile int sequence;
	private List<SessionStateListener> sessionStateListeners;

	private volatile int state = CLOSED;
	private long timeout = 20 * 1000;
}