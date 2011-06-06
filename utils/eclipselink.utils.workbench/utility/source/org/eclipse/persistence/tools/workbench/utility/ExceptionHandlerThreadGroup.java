/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

/**
 * This ThreadGroup overrides the #uncaughtException(Thread, Throwable) method
 * and notifies the broadcaster that an exception occurred.
 */
public class ExceptionHandlerThreadGroup
	extends ThreadGroup
{
	/**
	 * Broadcasting delegate.
	 */
	private final ExceptionBroadcaster broadcaster;


	// ********** constructors/initialization **********

	/**
	 * @see ThreadGroup(String)
	 */
	public ExceptionHandlerThreadGroup(String name, ExceptionBroadcaster broadcaster) {
		super(name);
		this.broadcaster = broadcaster;
	}

	/**
	 * @see ThreadGroup(ThreadGroup, String)
	 */
	public ExceptionHandlerThreadGroup(ThreadGroup parent, String name, ExceptionBroadcaster broadcaster) {
		super(parent, name);
		this.broadcaster = broadcaster;
	}


	// ********** ThreadGroup overrides **********

	/**
	 * Any uncaught exceptions are forwarded to the broadcaster.
	 * @see ThreadGroup#uncaughtException(Thread, Throwable)
	 */
	public synchronized void uncaughtException(Thread t, Throwable e) {
		this.broadcaster.broadcast(t, e);
	}


	// ********** public API **********

	/**
	 * Return the broadcaster the thread group uses to broadcast any
	 * uncaught exceptions.
	 */
	public ExceptionBroadcaster getExceptionBroadcaster() {
		return this.broadcaster;
	}

}
