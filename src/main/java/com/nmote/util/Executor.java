/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.util;

/**
 * <p>
 * Executor is used to run Commands received from remote entity. Multiple
 * implementations are possible, blocking, unlimited, etc. If you already use a
 * thread pool in your application, you could write an adapter and use it as a
 * executor instead of default SameThreadExecutor.
 * </p>
 *
 * <p>
 * Executor is also used to execute Runnables that read and write PDUs from a
 * Session
 * </p>
 *
 * @see com.nmote.smpp.Command
 * @see com.nmote.smpp.Session
 *
 * @author Vjekoslav Nesek
 */
public interface Executor {

	/**
	 * Disposes prealocated resources (for example ThreadPool) maintained by
	 * this executor.
	 */
	public void dispose();

	/**
	 * Executes <code>task</code>, using priority as a hint.
	 *
	 * @param task
	 *            Runnable to execute.
	 * @param priority
	 *            Thread priority
	 */
	public void exec(Runnable task, int priority);
}
