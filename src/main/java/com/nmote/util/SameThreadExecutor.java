/*
 * Copyright (c) Nmote d.o.o. 2003-2015. All rights reserved.
 * See LICENSE.txt for licensing information.
 */

package com.nmote.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SameThreadExecutor executes task in the same thread as a method that made a
 * call to run(Runnable). It is suitable for situations where tasks finish very
 * quickly.
 *
 * @author Vjekoslav Nesek
 */
public class SameThreadExecutor implements Executor {

	private static Logger log = LoggerFactory.getLogger(SameThreadExecutor.class);

	/**
	 * @see com.nmote.util.Executor#dispose()
	 */
	@Override
	public void dispose() {
	}

	/**
	 * @see Executor#exec(Runnable, int)
	 */
	@Override
	public void exec(Runnable task, int priority) {
		Thread t = Thread.currentThread();

		String oldName;
		int oldPriority;
		boolean securityOk;

		try {
			oldName = t.getName();
			oldPriority = t.getPriority();
			t.setName(task.toString());
			t.setPriority(priority);
			securityOk = true;
		} catch (SecurityException e) {
			oldName = null;
			oldPriority = 0;
			securityOk = false;
		}

		try {
			task.run();
		} catch (Throwable error) {
			log.error("Failed to execute {}", task, error);
		} finally {
			if (securityOk) {
				t.setName(oldName);
				t.setPriority(oldPriority);
			}
		}
	}
}