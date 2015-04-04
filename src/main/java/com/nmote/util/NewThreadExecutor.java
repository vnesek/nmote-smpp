/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.util;

/**
 * NewThreadExecutor executes each task in a new execution thread.
 *
 * @author Vjekoslav Nesek
 */
public class NewThreadExecutor implements Executor {

	public static final Executor INSTANCE = new NewThreadExecutor();

	/**
	 * @see com.nmote.util.Executor#dispose()
	 */
	@Override
	public void dispose() {
	}

	/**
	 * @see com.nmote.smpp.util.Executor#exec(java.lang.Runnable, int)
	 */
	@Override
	public void exec(Runnable task, int priority) {
		Thread t = new Thread(task);
		t.setPriority(priority);
		t.setDaemon(isDaemon());
		t.setName(task.toString());
		t.start();
	}

	/**
	 * Returns true if created threads are daemon threads.
	 *
	 * @return true if created threads are daemon threads
	 */
	public boolean isDaemon() {
		return daemon;
	}

	/**
	 * Sets daemonization of threads. If daemon is true new threads will be
	 * daemon threads.
	 *
	 * @see Thread#setDaemon(boolean)
	 * @param daemon
	 */
	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	private boolean daemon;
}
