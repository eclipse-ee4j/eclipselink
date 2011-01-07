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

import java.io.Serializable;

/**
 * This class provides synchronized access to an object.
 * It also provides protocol for suspending a thread until the
 * value is set to null or a non-null value, with optional time-outs.
 */
public class SynchronizedObject
	implements Cloneable, Serializable
{
	/** Backing value. */
	private Object value;

	/** Object to synchronize on. */
	private final Object mutex;

	private static final long serialVersionUID = 1L;


	// ********** constructors **********

	/**
	 * Create a synchronized object with the specified initial value
	 * and mutex.
	 */
	public SynchronizedObject(Object value, Object mutex) {
		super();
		this.value = value;
		this.mutex = mutex;
	}

	/**
	 * Create a synchronized object with the specified initial value.
	 */
	public SynchronizedObject(Object value) {
		super();
		this.value = value;
		this.mutex = this;
	}

	/**
	 * Create a synchronized object with an initial value of null.
	 */
	public SynchronizedObject() {
		this(null);
	}


	// ********** accessors **********

	/**
	 * Return the current value.
	 */
	public Object getValue() {
		synchronized (this.mutex) {
			return this.value;
		}
	}

	/**
	 * Return whether the current value is null.
	 */
	public boolean isNull() {
		synchronized (this.mutex) {
			return this.value == null;
		}
	}

	/**
	 * Return whether the current value is not null.
	 */
	public boolean isNotNull() {
		synchronized (this.mutex) {
			return this.value != null;
		}
	}

	/**
	 * Set the value. If the value changes, all waiting
	 * threads are notified.
	 */
	public void setValue(Object value) {
		synchronized (this.mutex) {
			if (this.value != value) {
				this.value = value;
				this.mutex.notifyAll();
			}
		}
	}

	/**
	 * Set the value to null. If the value changes, all waiting
	 * threads are notified.
	 */
	public void setNull() {
		synchronized (this.mutex) {
			this.setValue(null);
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
	 * Suspend the current thread until the value changes
	 * to the specified value.
	 */
	public void waitUntilValueIs(Object x) throws InterruptedException {
		synchronized (this.mutex) {
			while (this.value != x) {
				this.mutex.wait();
			}
		}
	}

	/**
	 * Suspend the current thread until the value changes
	 * to something other than the specified value.
	 */
	public void waitUntilValueIsNot(Object x) throws InterruptedException {
		synchronized (this.mutex) {
			while (this.value == x) {
				this.mutex.wait();
			}
		}
	}

	/**
	 * Suspend the current thread until the value changes to null.
	 */
	public void waitUntilNull() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilValueIs(null);
		}
	}

	/**
	 * Suspend the current thread until the value changes
	 * to something other than null.
	 */
	public void waitUntilNotNull() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilValueIsNot(null);
		}
	}

	/**
	 * Suspend the current thread until the value changes to
	 * something other than the specified value, then change
	 * it back to the specified value and continue executing.
	 */
	public void waitToSetValue(Object x) throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilValueIsNot(x);
			this.setValue(x);
		}
	}

	/**
	 * Suspend the current thread until the value changes to
	 * null, then change it back to null and continue executing.
	 */
	public void waitToSetNull() throws InterruptedException {
		synchronized (this.mutex) {
			this.waitUntilNotNull();
			this.setValue(null);
		}
	}


	// ********** timed waits **********

	/**
	 * Suspend the current thread until the value changes
	 * to the specified value or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was achieved; return false if a time-out occurred.
	 */
	public boolean waitUntilValueIs(Object x, long timeout) throws InterruptedException {
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
	 * Suspend the current thread until the value changes to something
	 * other than the specified value or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was removed; return false if a time-out occurred.
	 */
	public boolean waitUntilValueIsNot(Object x, long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			if (timeout == 0L) {
				this.waitUntilValueIsNot(x);	// wait indefinitely until notified
				return true;	// if it ever comes back, the condition was met
			}

			long stop = System.currentTimeMillis() + timeout;
			long remaining = timeout;
			while ((this.value == x) && (remaining > 0L)) {
				this.mutex.wait(remaining);
				remaining = stop - System.currentTimeMillis();
			}
			return (this.value != x);
		}
	}

	/**
	 * Suspend the current thread until the value changes
	 * to null or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was achieved; return false if a time-out occurred.
	 */
	public boolean waitUntilNull(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			return this.waitUntilValueIs(null, timeout);
		}
	}

	/**
	 * Suspend the current thread until the value changes
	 * to something other than null or the specified time-out occurs.
	 * The time-out is specified in milliseconds. Return true if the specified
	 * value was achieved; return false if a time-out occurred.
	 */
	public boolean waitUntilNotNull(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			return this.waitUntilValueIsNot(null, timeout);
		}
	}

	/**
	 * Suspend the current thread until the value changes to
	 * something other than the specified value, then change
	 * it back to the specified value and continue executing.
	 * If the value does not change to something other than the
	 * specified before the time-out, simply continue executing
	 * without changing the value.
	 * The time-out is specified in milliseconds. Return true if the value was
	 * set to true; return false if a time-out occurred.
	 */
	public boolean waitToSetValue(Object x, long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			boolean success = this.waitUntilValueIsNot(x, timeout);
			if (success) {
				this.setValue(x);
			}
			return success;
		}
	}

	/**
	 * Suspend the current thread until the value changes to something
	 * other than null, then change it back to null and continue executing.
	 * If the value does not change to something other than null before
	 * the time-out, simply continue executing without changing the value.
	 * The time-out is specified in milliseconds. Return true if the value was
	 * set to false; return false if a time-out occurred.
	 */
	public boolean waitToSetNull(long timeout) throws InterruptedException {
		synchronized (this.mutex) {
			boolean success = this.waitUntilNotNull(timeout);
			if (success) {
				this.setValue(null);
			}
			return success;
		}
	}


	// ********** synchronized behavior **********

	/**
	 * If current thread is not interrupted, execute the specified command 
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
	public boolean equals(Object obj) {
		if ( ! (obj instanceof SynchronizedObject)) {
			return false;
		}
		Object thisValue = this.getValue();
		Object otherValue = ((SynchronizedObject) obj).getValue();
		return (thisValue == null) ?
			(otherValue == null) : thisValue.equals(otherValue);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		Object temp = this.getValue();
		return (temp == null) ? 0 : temp.hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return String.valueOf(this.getValue());
	}

}
