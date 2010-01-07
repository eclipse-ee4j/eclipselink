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
package org.eclipse.persistence.tools.workbench.utility.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.RunnableDeath;


/**
 * This log record handler will shutdown the JVM if it receives
 * a sufficiently severe log record.
 */
public class DeathHandler extends Handler {
	Runnable runnableDeath;

	/**
	 * Construct a log handler that will shut down the JVM if it
	 * receives a sufficiently severe log record. It will wait the
	 * specified amount of time and exit with the specified status.
	 */
	public DeathHandler(int wait, int exitStatus) {
		super();
		this.runnableDeath = new RunnableDeath(wait, exitStatus);
	}

	/**
	 * @see java.util.logging.Handler#close()
	 */
	public void close() throws SecurityException {
		// do nothing
	}

	/**
	 * @see java.util.logging.Handler#flush()
	 */
	public void flush() {
		// do nothing
	}

	/**
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord record) {
		if (this.isLoggable(record)) {
			new Thread(this.runnableDeath, "Death").start();
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
