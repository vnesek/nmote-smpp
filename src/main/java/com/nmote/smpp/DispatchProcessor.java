/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.smpp;

import java.util.HashMap;
import java.util.Map;

/**
 * DispatchProcessor represents SMPP session state. DispatchProcessor maintans
 * registry of Processors used to process Commands and dispatches command to a
 * correct processor. DefaultProcessor is used to process commands without
 * registered Processor. This allows chaining of DispatchProcessors.
 *
 * @author Vjekoslav Nesek
 */
public class DispatchProcessor implements Processor {

	/**
	 * Constructor for DispatchProcessor
	 */
	public DispatchProcessor() {
	}

	/**
	 * Constructor for DispatchProcessor. <code>defaultProcessor</code> is used
	 * to process commands without registered processor.
	 *
	 * @param defaultProcessor
	 */
	public DispatchProcessor(Processor defaultProcessor) {
		setDefaultProcessor(defaultProcessor);
	}

	/**
	 * Returns default processor.
	 *
	 * @return Default processor
	 */
	public Processor getDefaultProcessor() {
		return defaultProcessor;
	}

	/**
	 * Returns processor for a given <code>cmd</code>
	 *
	 * @param cmd
	 * @return Processor for <code>cmd</code>.
	 */
	public Processor getProcessor(int cmd) {
		Processor result;
		if (processors != null) {
			result = processors.get(new Integer(cmd));
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * @see com.nmote.smpp.Processor#process(com.nmote.smpp.Command,
	 *      com.nmote.smpp.Session)
	 */
	@Override
	public void process(Command command, Session session) {
		// Dispatch command
		Processor delegate = getProcessor(command.getRequest().getCommandId());
		if (delegate != null) {
			delegate.process(command, session);
		}

		// Process using default processor if command hasn't completed
		if (command.getState() != Command.RESPONDED && defaultProcessor != null) {
			defaultProcessor.process(command, session);
		}
	}

	/**
	 * Sets the default processor. Default processor is used to process command
	 * if no other processor is specified.
	 *
	 * @param defaultProcessor
	 *            The defaultProcessor to set
	 */
	public void setDefaultProcessor(Processor defaultProcessor) {
		this.defaultProcessor = defaultProcessor;
	}

	public void setProcessor(int cmd, Processor p) {
		if (locked) {
			throw new IllegalStateException("Can't modify");
		}
		synchronized (this) {
			if (processors == null) {
				processors = new HashMap<Integer, Processor>();
			}

			processors.put(new Integer(cmd), p);
		}
	}

	private Processor defaultProcessor;
	private boolean locked;

	private Map<Integer, Processor> processors;
}