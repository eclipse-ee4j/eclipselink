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
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>NullListIterator</code> is just that.
 */
public final class NullListIterator implements ListIterator {

	// singleton
	private static NullListIterator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized ListIterator instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullListIterator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullListIterator() {
		super();
	}
	
	/**
	 * @see java.util.ListIterator#add(Object)
	 */
	public void add(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return false;
	}

	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return false;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		throw new NoSuchElementException();
	}

	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return 0;
	}

	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
		throw new NoSuchElementException();
	}

	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		return -1;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.ListIterator#set(Object)
	 */
	public void set(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}
	
}
