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

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Straightforward implementation of the ExceptionBroadcaster interface.
 */
public class SimpleExceptionBroadcaster
	implements ExceptionBroadcaster, Serializable
{
	/** The exception listeners. */
	private ExceptionListener[] listeners;

	/**
	 * The stream where exceptions are dumped when there are no
	 * listeners or when a listener triggers an exception while
	 * responding to the original exception
	 */
	private final PrintStream defaultStream;

	private static final ExceptionListener[] EMPTY_LISTENERS = new ExceptionListener[0];
	private static final long serialVersionUID = 1L;


	// ********** constructor **********

	/**
	 * Dump exceptions to the specified stream if there are no listeners.
	 */
	public SimpleExceptionBroadcaster(PrintStream defaultStream) {
		super();
		this.defaultStream = defaultStream;
	}

	/**
	 * Default constructor. Dump exceptions to the standard error stream
	 * if there are no listeners.
	 */
	public SimpleExceptionBroadcaster() {
		this(System.err);
	}


	// ********** ExceptionBroadcaster implementation **********

	/**
	 * Broadcast the specified exception to the broadcaster's listeners.
	 */
	public void broadcast(Thread thread, Throwable exception) {
		ExceptionListener[] currentListeners = null;
		synchronized (this) {
			if (this.listeners != null) {
				int len = this.listeners.length;
				currentListeners = new ExceptionListener[len];
				System.arraycopy(this.listeners, 0, currentListeners, 0, len);
			}
		}
		if (currentListeners == null) {
			this.broadcastOnDefaultStream(thread, exception);
		} else {
			for (int i = currentListeners.length; i-- > 0; ) {
				try {
					currentListeners[i].exceptionThrown(thread, exception);
				} catch (Throwable ex2) {
					// if we have a problem during the notification, dump to the default stream
					this.broadcastOnDefaultStream(Thread.currentThread(), ex2);
					this.broadcastOnDefaultStream(thread, exception);
				}
			}
		}
	}

	/**
	 * @see ExceptionBroadcaster#addExceptionListener(ExceptionListener)
	 */
	public synchronized void addExceptionListener(ExceptionListener listener) {
		if (listener == null) {
			throw new NullPointerException();		// better sooner than later
		}
		int len = 0;
		ExceptionListener[] newListeners;
		if (this.listeners == null) {
			newListeners = new ExceptionListener[1];
		} else {
			len = this.listeners.length;
			newListeners = new ExceptionListener[len + 1];
			System.arraycopy(this.listeners, 0, newListeners, 0, len);
		}
		newListeners[len] = listener;
		this.listeners = newListeners;
	}

	/**
	 * @see ExceptionBroadcaster#removeExceptionListener(ExceptionListener)
	 */
	public synchronized void removeExceptionListener(ExceptionListener listener) {
		if (this.listeners == null) {
			throw new IllegalArgumentException("listener not registered");
		}
		int len = this.listeners.length;
		int index = len;
		while (index-- > 0) {
			if (this.listeners[index] == listener) {	// use identity
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("listener not registered");
		}
		if (len == 1) {
			this.listeners = null;
		} else {
			ExceptionListener[] newListeners = new ExceptionListener[len - 1];
			if (index != 0) {
				System.arraycopy(this.listeners, 0, newListeners, 0, index);
			}
			int next = index + 1;
			if (next != len) {
				System.arraycopy(this.listeners, next, newListeners, index, len - next);
			}
			this.listeners = newListeners;
		}
	}


	// ********** public API **********

	/**
	 * Return the broadcaster's exception listeners.
	 */
	public synchronized ExceptionListener[] getExceptionListeners() {
		if (this.listeners == null) {
			return EMPTY_LISTENERS;
		}
		int len = this.listeners.length;
		ExceptionListener[] result = new ExceptionListener[len];
		System.arraycopy(this.listeners, 0, result, 0, len);
		return result;
	}

	/**
	 * Return whether the broadcaster has any exception listeners.
	 */
	public synchronized boolean hasExceptionListeners() {
		return this.listeners != null;
	}

	/**
	 * Return whether the broadcaster has no exception listeners.
	 */
	public synchronized boolean hasNoExceptionListeners() {
		return this.listeners == null;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}


	// ********** protected API **********

	/**
	 * Broadcast the exception to the "default" stream.
	 * "If a tree falls in the forest...?"
	 */
	protected void broadcastOnDefaultStream(Thread thread, Throwable exception) {
		this.broadcastOn(thread, exception, this.defaultStream);
	}

	/**
	 * Broadcast the exception to the specified stream.
	 */
	protected void broadcastOn(Thread thread, Throwable exception, PrintStream stream) {
		synchronized (stream) {
			stream.println(thread);
			exception.printStackTrace(stream);
		}
	}

}
