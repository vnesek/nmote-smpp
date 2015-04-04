/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.util;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple thread pool executor maintains a thread pool that is used to execute
 * runnables.
 */
public class SimpleThreadPoolExecutor extends SameThreadExecutor {

	private class ThreadPoolThread extends Thread {

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			synchronized (SimpleThreadPoolExecutor.this) {
				++activeThreads;
			}

			try {
				do {
					if (task == null) {
						synchronized (pooledThreads) {
							pooledThreads.push(this);
							pooledThreads.notify();
						}
					}

					while (task == null) {
						synchronized (this) {
							try {
								wait();
							} catch (InterruptedException e) {
							}
						}

						if (disposed) {
							return;
						}
					}

					// Execute task
					try {
						setPriority(priority);
						task.run();
					} catch (Throwable error) {
						log.error("Failed to execute " + task, error);
					} finally {
						task = null;
					}
				} while (activeThreads < maxThreads && !disposed);

			} finally {
				synchronized (SimpleThreadPoolExecutor.this) {
					--activeThreads;
				}
			}
		}

		Runnable task;
		int priority;
	}

	private static Logger log = LoggerFactory.getLogger(SameThreadExecutor.class);

	/**
	 * @see com.nmote.util.Executor#dispose()
	 */
	@Override
	public void dispose() {
		disposed = true;
		while (pooledThreads.size() > 0) {
			ThreadPoolThread t = pooledThreads.pop();
			execHelper(null, 0, t);
		}
	}

	/**
	 * @see com.nmote.util.Executor#exec(java.lang.Runnable, int)
	 */
	@Override
	public synchronized void exec(Runnable task, int priority) {
		final boolean debug = log.isDebugEnabled();
		if (pooledThreads.size() > 0) {
			ThreadPoolThread t = pooledThreads.pop();
			execHelper(task, priority, t);
		} else if (activeThreads < maxThreads) {
			ThreadPoolThread t = new ThreadPoolThread();
			execHelper(task, priority, t);
			t.start();
			if (debug) {
				log.debug("Started new ThreadPoolThread, active threads = " + activeThreads);
			}
		} else {
			waitHelper();
			if (pooledThreads.size() > 0) {
				ThreadPoolThread t = pooledThreads.pop();
				execHelper(task, priority, t);
			} else {
				log.warn("Thread pool exhausted, executing in the same thread");
				super.exec(task, priority);
			}
		}
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		dispose();
	}

	private void execHelper(Runnable task, int priority, ThreadPoolThread t) {
		synchronized (t) {
			t.task = task;
			t.priority = priority;
			t.notify();
		}
	}

	private synchronized void waitHelper() {
		if (maxBlock >= 0) {
			try {
				wait(maxBlock);
			} catch (InterruptedException e) {
			}
		}
	}

	private Stack<ThreadPoolThread> pooledThreads = new Stack<ThreadPoolThread>();
	private int activeThreads;
	private int maxThreads = 15;
	private long maxBlock = 500;

	private boolean disposed;
}
