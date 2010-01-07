/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This runnable will sleep for a specified amount of time
 * and then kill the JVM with a call to System.exit() with
 * a specified exit status. If the thread is interrupted,
 * we will stop the thread without killing the JVM.
 */
public final class RunnableDeath implements Runnable {
	/** The time to wait before killing the JVM, in milliseconds. */
	private final int wait;
	/** The exit status code that will passed to the O/S on exit. */
	private final int exitStatus;

	/**
	 * Construct a killer that will wait for the specified time and
	 * exit with the specified exit status.
	 */
	public RunnableDeath(int wait, int exitStatus) {
		super();
		this.wait = wait;
		this.exitStatus = exitStatus;
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {
		long stop = System.currentTimeMillis() + this.wait;
		long remaining = this.wait;
		while (remaining > 0L) {
			try {
				Thread.sleep(remaining);
			} catch (InterruptedException ex) {
				return;		// commuted death sentence
			}
			remaining = stop - System.currentTimeMillis();
		}
		System.exit(this.exitStatus);
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, "wait=" + this.wait + "; exit status=" + this.exitStatus);
	}

}
