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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>PeekableIterator</code> wraps another <code>Iterator</code>
 * and allows a <code>peek()</code> at the next element to be 
 * returned by <code>next()</code>.
 * <p>
 * One, possibly undesirable, side-effect of using this iterator is that
 * the nested iterator's <code>next()</code> method will be invoked
 * <em>before</em> the peekable iterator's <code>next()</code>
 * method is invoked. This is because the "next" element must be
 * pre-loaded for the <code>peek()</code> method.
 * This also prevents a peekable iterator from supporting the optional
 * <code>remove()</code> method.
 */

public class PeekableIterator implements Iterator {
	private Iterator nestedIterator;
	private Object next;

	private static final Object END = new Object();

	/**
	 * Construct a peekable iterator that wraps the specified nested
	 * iterator.
	 */
	public PeekableIterator(Iterator nestedIterator) {
		super();
		this.nestedIterator = nestedIterator;
		this.loadNext();
	}
	
	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.next != END;
	}
	
	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.next == END) {
			throw new NoSuchElementException();
		}
		Object result = this.next;
		this.loadNext();
		return result;
	}
	
	/**
	 * Return the element that will be returned by the next call to the
	 * <code>next()</code> method, without advancing past it.
	 */
	public Object peek() {
		if (this.next == END) {
			throw new NoSuchElementException();
		}
		return this.next;
	}
	
	/**
	 * Because we need to pre-load the next element
	 * to be returned, we cannot support the <code>remove()</code>
	 * method.
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Load next with the next entry from the nested
	 * iterator. If there are none, next is set to <code>END</code>.
	 */
	private void loadNext() {
		if (this.nestedIterator.hasNext()) {
			this.next = this.nestedIterator.next();
		} else {
			this.next = END;
		}
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.nestedIterator + ')';
	}
	
}
