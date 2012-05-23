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
 * This class can be used wherever a mutable integer object is needed.
 * It is a cross between an int and an Integer. It can be stored in a standard
 * container (e.g. Collection) but can be modified.
 */
public final class Counter
	implements Cloneable, Serializable
{
	private int count = 0;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct a counter with the specified initial value.
	 */
	public Counter(int count) {
		super();
		this.count = count;
	}

	/**
	 * Construct a counter with an initial value of zero.
	 */
	public Counter() {
		this(0);
	}

	/**
	 * Return the current count of the counter.
	 */
	public synchronized int count() {
		return this.count;
	}

	/**
	 * Increment and return the current count of the counter.
	 */
	public synchronized int increment() {
		return ++this.count;
	}

	/**
	 * Increment and return the current count of the counter.
	 */
	public synchronized int increment(int increment) {
		return this.count += increment;
	}

	/**
	 * Derement and return the current count of the counter.
	 */
	public synchronized int decrement() {
		return --this.count;
	}

	/**
	 * Derement and return the current count of the counter.
	 */
	public synchronized int decrement(int decrement) {
		return this.count -= decrement;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public synchronized boolean equals(Object o) {
		if ( ! (o instanceof Counter)) {
			return false;
		}
		return this.count == ((Counter) o).count;
	}

	/**
	 * @see Object#hashCode()
	 */
	public synchronized int hashCode() {
		return this.count;
	}

	/**
	 * @see Object#clone()
	 */
	public synchronized Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	/**
	 * @see Object#toString()
	 */
	public synchronized String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(ClassTools.shortClassNameForObject(this));
		sb.append('(');
		sb.append(this.count);
		sb.append(')');
		return sb.toString();
	}

}
