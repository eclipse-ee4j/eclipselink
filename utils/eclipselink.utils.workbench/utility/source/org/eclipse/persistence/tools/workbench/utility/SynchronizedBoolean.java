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
package org.eclipse.persistence.tools.workbench.utility;

import java.io.Serializable;

/**
 * This class provides synchronized access to a boolean value.
 * It also provides protocol for suspending a thread until the
 * boolean value is set to true or false, with optional time-outs.
 */
public class SynchronizedBoolean
	implements Cloneable, Serializable
{
	/** Backing boolean. */
	private boolean value;

	/** Object to synchronize on. */
	private final Object mutex;

	private static final long serialVersionUID = 1L;


	// ********** constructors **********

	/**
	 * Create a synchronized boolean with the specified initial value
	 * and mutex.
	 */
	public SynchronizedBoolean(boolean value, Object mutex) {
		super();
		this.value = value;
		this.mutex = mutex;
	}

	/**
	 * Create a synchronized boolean with the specified initial value.
	 */
	public SynchronizedBoolean(boolean value) {
		super();
		this.value = value;
		this.mutex = this;
	}

	/**
	 * Create a synchronized boolean with an initial value of false
	 * and specified mutex.
	 */
	public SynchronizedBoolean(Object mutex) {
		this(false, mutex);
	}

	/**
	 * Create a synchronized boolean with an initial value of false.
	 */
	public SynchronizedBoolean() {
		this(false);
	}


	// ********** accessors **********

	/**
	 * Return the current boolean value.
	 */
	public boolean getValue() {
		synchronized (this.mutex) {
			return this.value;
		}
	}

	/**
	 * Return whether the current boolean value is true.
	 */
	public boolean isTrue() {
		synchronized (this.mutex) {
			return this.value;
		}
	}

	/**
	 * Return whether the current boolean value is false.
	 */
	public boolean isFalse() {
		synchronized (this.mutex) {
			return ! this.value;
		}
	}

	/**
	 * Set the boolean value. If the value changes, all waiting
	 * threads are notified.
	 */
	public void setValue(boolean value) {
		synchronized (this.mutex) {
			if (this.value != value) {
				this.value = value;
				this.mutex.notifyAll();
			}
		}
	}

	/**
	 * Set the boolean value to true. If the value changes, all waiting
	 * threads are notified.
	 */
	public void setTrue() {
		synchronized (this.mutex) {
			this.setValue(true);
		}
	}

	/**
	 * Set the boolean value to false. If the value changes, all waiting
	 * threads are notified.
	 */
	public void setFalse() {
		synchronized (this.mutex) {
			this.setValue(false);
		}
	}

	/**
	 * Return the object this object locks on while performing
	 * its operations.
	 */
	public Object getMutex() {
		return this.mutex;
	}


	// ********** indefinite waits **********

	/**
	 * Suspend the current thread until the boolean value changes
	 * to the specified value.
	 */
	public void waitUntilValueIs(boolean x) throws InterruptedException {
		synchronized (this.mutex) {
			while (this.value != x) {
				this.mutex.wait();
			}
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes to true.
	 */
	public void waitUntilTrue() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilValueIs(true);
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes to false.
	 */
	public void waitUntilFalse() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilValueIs(false);
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes to false,
	 * then change it back to true and continue executing.
	 */
	public void waitToSetTrue() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilFalse();
			this.setValue(true);
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes to true,
	 * then change it back to false and continue executing.
	 */
	public void waitToSetFalse() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilTrue();
			this.setValue(false);
		}
	}


	// ********** timed waits **********

	/**
	 * Suspend the current thread until the boolean value changes
	 * to the specified value or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was achieved; return false if a time-out occurred.
	 */
	public boolean waitUntilValueIs(boolean x, long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			if (timeout == 0L) {
				this.waitUntilValueIs(x);	// wait indefinitely until notified
				return true;	// if it ever comes back, the condition was met
			}
	
			long stop = System.currentTimeMillis() + timeout;
			long remaining = timeout;
			while ((this.value != x) && (remaining > 0L)) {
				this.mutex.wait(remaining);
				remaining = stop - System.currentTimeMillis();
			}
			return (this.value == x);
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes
	 * to true or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was achieved; return false if a time-out occurred.
	 */
	public boolean waitUntilTrue(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			return this.waitUntilValueIs(true, timeout);
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes
	 * to false or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was achieved; return false if a time-out occurred.
	 */
	public boolean waitUntilFalse(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			return this.waitUntilValueIs(false, timeout);
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes to false,
	 * then change it back to true and continue executing. If the boolean
	 * value does not change to false before the time-out, simply continue
	 * executing without changing the value.
	 * The time-out is specified in milliseconds. Return true if the value was
	 * set to true; return false if a time-out occurred.
	 */
	public boolean waitToSetTrue(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			boolean success = this.waitUntilFalse(timeout);
			if (success) {
				this.setValue(true);
			}
			return success;
		}
	}

	/**
	 * Suspend the current thread until the boolean value changes to true,
	 * then change it back to false and continue executing. If the boolean
	 * value does not change to true before the time-out, simply continue
	 * executing without changing the value.
	 * The time-out is specified in milliseconds. Return true if the value was
	 * set to false; return false if a time-out occurred.
	 */
	public boolean waitToSetFalse(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			boolean success = this.waitUntilTrue(timeout);
			if (success) {
				this.setValue(false);
			}
			return success;
		}
	}


	// ********** synchronized behavior **********

	/**
	 * If the current thread is not interrupted, execute the specified command 
	 * with the mutex locked. This is useful for initializing the value in another
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


	// ********** standard methods **********

	/**
	 * @see Object#clone()
	 */
	public Object clone() {
		try {
			synchronized (this.mutex) {
				return super.clone();
			}
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof SynchronizedBoolean) {
			return this.getValue() == ((SynchronizedBoolean) o).getValue();
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.getValue() ? 1 : 0;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return String.valueOf(this.getValue());
	}

}
