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

import java.io.Serializable;
import java.util.EmptyStackException;

/**
 * Thread-safe implementation of the Stack interface.
 * This also provides protocol for suspending a thread until the
 * stack is empty or not empty, with optional time-outs.
 */
public class SynchronizedStack
	implements Stack, Serializable
{
	/** Backing stack. */
	private Stack stack;

	/** Object to synchronize on. */
	private final Object mutex;

	private static final long serialVersionUID = 1L;


	// ********** constructors **********

	/**
	 * Construct a synchronized stack that wraps the
	 * specified stack and locks on the specified mutex.
	 */
	public SynchronizedStack(Stack stack, Object mutex) {
		super();
		this.stack = stack;
		this.mutex = mutex;
	}

	/**
	 * Construct a synchronized stack that wraps the
	 * specified stack and locks on itself.
	 */
	public SynchronizedStack(Stack stack) {
		super();
		this.stack = stack;
		this.mutex = this;
	}

	/**
	 * Construct a synchronized stack that locks on the specified mutex.
	 */
	public SynchronizedStack(Object mutex) {
		this(new SimpleStack(), mutex);
	}

	/**
	 * Construct a synchronized stack that locks on itself.
	 */
	public SynchronizedStack() {
		this(new SimpleStack());
	}


	// ********** Stack implementation **********

	/**
	 * @see Stack#push(Object)
	 */
	public void push(Object o) {
		synchronized (this.mutex) {
			this.stack.push(o);
			this.mutex.notifyAll();
		}
	}

	/**
	 * @see Stack#pop()
	 */
	public Object pop() {
		synchronized (this.mutex) {
			Object o = this.stack.pop();
			this.mutex.notifyAll();
			return o;
		}
	}

	/**
	 * @see Stack#peek()
	 */
	public Object peek() {
		synchronized (this.mutex) {
			return this.stack.peek();
		}
	}

	/**
	 * @see Stack#isEmpty()
	 */
	public boolean isEmpty() {
		synchronized (this.mutex) {
			return this.stack.isEmpty();
		}
	}


	// ********** indefinite waits **********

	/**
	 * Suspend the current thread until the stack's empty status changes
	 * to the specified value.
	 */
	public void waitUntilEmptyIs(boolean empty) throws InterruptedException {
		synchronized (this.mutex) {
			while (this.isEmpty() != empty) {
				this.mutex.wait();
			}
		}
	}

	/**
	 * Suspend the current thread until the stack is empty.
	 */
	public void waitUntilEmpty() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilEmptyIs(true);
		}
	}

	/**
	 * Suspend the current thread until the stack has something on it.
	 */
	public void waitUntilNotEmpty() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilEmptyIs(false);
		}
	}

	/**
	 * Suspend the current thread until the stack is empty,
	 * then "push" the specified item on to the top of the stack
	 * and continue executing.
	 */
	public void waitToPush(Object o) throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilEmpty();
			this.push(o);
		}
	}

	/**
	 * Suspend the current thread until the stack has something on it,
	 * then "pop" an item from the top of the stack and return it.
	 */
	public Object waitToPop() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilNotEmpty();
			return this.pop();
		}
	}


	// ********** timed waits **********

	/**
	 * Suspend the current thread until the stack's empty status changes
	 * to the specified value or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was achieved; return false if a time-out occurred.
	 */
	public boolean waitUntilEmptyIs(boolean empty, long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			if (timeout == 0L) {
				this.waitUntilEmptyIs(empty);	// wait indefinitely until notified
				return true;	// if it ever comes back, the condition was met
			}

			long stop = System.currentTimeMillis() + timeout;
			long remaining = timeout;
			while ((this.isEmpty() != empty) && (remaining > 0L)) {
				this.mutex.wait(remaining);
				remaining = stop - System.currentTimeMillis();
			}
			return (this.isEmpty() == empty);
		}
	}

	/**
	 * Suspend the current thread until the stack is empty
	 * or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if
	 * the stack is empty; return false if a time-out occurred.
	 */
	public boolean waitUntilEmpty(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			return this.waitUntilEmptyIs(true, timeout);
		}
	}

	/**
	 * Suspend the current thread until the stack has something on it.
	 * or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if
	 * the stack has something on it; return false if a time-out occurred.
	 */
	public boolean waitUntilNotEmpty(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			return this.waitUntilEmptyIs(false, timeout);
		}
	}

	/**
	 * Suspend the current thread until the stack is empty,
	 * then "push" the specified item on to the top of the stack
	 * and continue executing. If the stack is not emptied out
	 * before the time-out, simply continue executing without
	 * "pushing" the item.
	 * The time-out is specified in milliseconds. Return true if the
	 * item was pushed; return false if a time-out occurred.
	 */
	public boolean waitToPush(Object o, long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			boolean success = this.waitUntilEmpty(timeout);
			if (success) {
				this.push(o);
			}
			return success;
		}
	}

	/**
	 * Suspend the current thread until the stack has something on it,
	 * then "pop" an item from the top of the stack and return it.
	 * If the stack is empty and nothing is "pushed" on to it before the
	 * time-out, throw an empty stack exception.
	 * The time-out is specified in milliseconds.
	 */
	public Object waitToPop(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			boolean success = this.waitUntilNotEmpty(timeout);
			if (success) {
				return this.pop();
			}
			throw new EmptyStackException();
		}
	}


	// ********** synchronized behavior **********

	/**
	 * If the current thread is not interrupted, execute the specified command 
	 * with the mutex locked. This is useful for initializing the stack in another
	 * thread.
	 */
	public void execute(Command command) throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		synchronized (this.mutex) {
			command.execute();
		}
	}


	// ********** additional public protocol **********

	/**
	 * Return the object this object locks on while performing
	 * its operations.
	 */
	public Object getMutex() {
		return this.mutex;
	}


	// ********** Object overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		synchronized (this.mutex) {
			return this.stack.toString();
		}
	}

}
