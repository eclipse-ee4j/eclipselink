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
 * A <code>SingleElementListIterator</code> holds a single element
 * and returns it with the first call to <code>next()</code>, at
 * which point it will return <code>false</code> to any subsequent
 * call to <code>hasNext()</code>. Likewise, it will return <code>false</code>
 * to a call to <code>hasPrevious()</code> until a call to <code>next()</code>,
 * at which point a call to <code>previous()</code> will return the
 * single element.
 * <p>
 * A <code>SingleElementIterator</code> is equivalent to the
 * <code>Iterator</code> returned by:
 * 	<code>java.util.Collections.singletonList(element).listIterator()</code>
 */
public class SingleElementListIterator implements ListIterator {
	private Object element;
	private Object next;

	private static final Object END = new Object();


	/**
	 * Construct a list iterator that returns only the specified element.
	 */
	public SingleElementListIterator(Object element) {
		super();
		this.element = element;
		this.next = element;
	}

	/**
	 * @see java.util.ListIterator#hasNext()
	 */
	public boolean hasNext() {
		return this.next == this.element;
	}

	/**
	 * @see java.util.ListIterator#next()
	 */
	public Object next() {
		if (this.next == END) {
			throw new NoSuchElementException();
		}
		this.next = END;
		return this.element;
	}

	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return (this.next == this.element) ? 0 : 1;
	}

	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return this.next == END;
	}

	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
		if (this.next == this.element) {
			throw new NoSuchElementException();
		}
		this.next = this.element;
		return this.element;
	}

	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int previousIndex() {
		return (this.next == END) ? 0 : -1;
	}

	/**
	 * @see java.util.ListIterator#add(Object)
	 */
	public void add(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.ListIterator#set(Object)
	 */
	public void set(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.ListIterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.element + ')';
	}

}
