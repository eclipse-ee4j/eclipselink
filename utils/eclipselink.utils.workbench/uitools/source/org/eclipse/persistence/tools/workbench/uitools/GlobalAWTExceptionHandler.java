/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.Cursor;
import java.awt.Frame;

import org.eclipse.persistence.tools.workbench.uitools.AWTExceptionHandler.GlobalHandler;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.ExceptionBroadcaster;


/**
 * This is the "global" handler that is stored in a static field so exceptions
 * that occurred during the processing of AWT events can be forwarded
 * to it by the AWTExceptionHandler.
 * @see AWTExceptionHandler
 */
public final class GlobalAWTExceptionHandler
	implements GlobalHandler
{
	/**
	 * Broadcasting delegate.
	 */
	private final ExceptionBroadcaster broadcaster;


	/**
	 * Set up a "global" AWT exception handler and the "local" AWT
	 * exception handler to broadcast any unhandled exceptions via
	 * the specified broadcaster.
	 */
	public static void register(ExceptionBroadcaster broadcaster) {
		AWTExceptionHandler.setGlobalHandler(new GlobalAWTExceptionHandler(broadcaster));
		AWTExceptionHandler.register();
	}

	/**
	 * Construct a "global" AWT exception handler that will broadcast
	 * via the specified broadcaster.
	 */
	private GlobalAWTExceptionHandler(ExceptionBroadcaster broadcaster) {
		super();
		this.broadcaster = broadcaster;
	}


	// ********** GlobalHandler implementation **********

	/**
	 * First, restore the cursor; then broadcast the exception.
	 * @see AWTExceptionHandler.GlobalHandler#handle(Throwable)
	 */
	public synchronized void handle(Throwable t) {
		this.restoreDefaultCursor();
		this.broadcaster.broadcast(Thread.currentThread(), t);
	}


	// ********** internal methods **********

	/**
	 * Brute force approach for now: set the cursor for ALL the current
	 * windows to the default cursor.
	 */
	// TODO maybe something more elegant?
	private void restoreDefaultCursor() {
		Cursor defaultCursor = Cursor.getDefaultCursor();
		Frame[] frames = Frame.getFrames();
		for (int i = frames.length; i-- > 0; ) {
			frames[i].setCursor(defaultCursor);
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
