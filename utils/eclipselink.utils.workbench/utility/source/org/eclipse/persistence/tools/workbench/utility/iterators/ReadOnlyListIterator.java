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
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>ReadOnlyListIterator</code> wraps another
 * <code>ListIterator</code> and removes support for:
 * 	#remove()
 * 	#set(Object)
 * 	#add(Object)
 */
public class ReadOnlyListIterator implements ListIterator {
	private ListIterator nestedListIterator;

	/**
	 * Construct an iterator on the specified list that
	 * disallows removes, sets, and adds.
	 */
	public ReadOnlyListIterator(List list) {
		this(list.listIterator());
	}

	/**
	 * Construct an iterator on the specified list iterator that
	 * disallows removes, sets, and adds.
	 */
	public ReadOnlyListIterator(ListIterator nestedListIterator) {
		super();
		this.nestedListIterator = nestedListIterator;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		// delegate to the nested iterator
		return this.nestedListIterator.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		// delegate to the nested iterator
		return this.nestedListIterator.next();
	}

	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		// delegate to the nested iterator
		return this.nestedListIterator.hasPrevious();
	}

	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
		// delegate to the nested iterator
		return this.nestedListIterator.previous();
	}

	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		// delegate to the nested iterator
		return this.nestedListIterator.nextIndex();
	}

	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		// delegate to the nested iterator
		return this.nestedListIterator.previousIndex();
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
	 * @see java.util.ListIterator#add(Object)
	 */
	public void add(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.nestedListIterator + ')';
	}
	
}
