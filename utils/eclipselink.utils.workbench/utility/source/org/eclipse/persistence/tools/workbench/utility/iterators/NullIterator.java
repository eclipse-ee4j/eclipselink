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
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>NullIterator</code> is just that.
 */
public final class NullIterator implements Iterator {

	// singleton
	private static NullIterator INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Iterator instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullIterator();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullIterator() {
		super();
	}
	
	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return false;
	}
	
	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		throw new NoSuchElementException();
	}
	
	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}
	
}
