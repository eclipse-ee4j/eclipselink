/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility;

/**
 * An ExceptionBroadcaster will notify its listeners when an
 * [uncaught] exception has occurred.
 */
public interface ExceptionBroadcaster {

	/**
	 * Broadcast the specified exception to the broadcaster's
	 * listeners. The specified thread was executing when the
	 * specified exception was thrown.
	 */
	void broadcast(Thread thread, Throwable exception);

	/**
	 * Add the specified listener to the broadcaster's
	 * registered listeners. The listener will be notified
	 * of any unhandled exceptions.
	 */
	void addExceptionListener(ExceptionListener listener);

	/**
	 * Remove the specified listener from the broadcaster's
	 * registered listeners.
	 */
	void removeExceptionListener(ExceptionListener listener);

}
