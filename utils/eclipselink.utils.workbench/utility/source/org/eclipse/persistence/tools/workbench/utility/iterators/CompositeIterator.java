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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>CompositeIterator</code> wraps a collection
 * of <code>Iterator</code>s and makes them appear to be a single
 * <code>Iterator</code>.
 */
public class CompositeIterator implements Iterator {
	private final Iterator iterators;
	private Iterator currentIterator;
	private Iterator lastIteratorToReturnNext;


	/**
	 * Construct an iterator with the specified collection of iterators.
	 */
	public CompositeIterator(Collection iterators) {
		this(iterators.iterator());
	}

	/**
	 * Construct an iterator with the specified collection of iterators.
	 */
	public CompositeIterator(Iterator iterators) {
		super();
		this.iterators = iterators;
	}

	/**
	 * Construct an iterator with the specified object prepended
	 * to the specified iterator.
	 */
	public CompositeIterator(Object object, Iterator iterator) {
		this(new SingleElementIterator(object), iterator);
	}

	/**
	 * Construct an iterator with the specified object appended
	 * to the specified iterator.
	 */
	public CompositeIterator(Iterator iterator, Object object) {
		this(iterator, new SingleElementIterator(object));
	}

	/**
	 * Construct an iterator with the specified iterators.
	 */
	public CompositeIterator(Iterator iterator1, Iterator iterator2) {
		this(new ArrayIterator(new Object[] {iterator1, iterator2}));
	}

	/**
	 * Construct an iterator with the specified iterators.
	 */
	public CompositeIterator(Iterator iterator1, Iterator iterator2, Iterator iterator3) {
		this(new ArrayIterator(new Object[] {iterator1, iterator2, iterator3}));
	}

	/**
	 * Construct an iterator with the specified array of iterators.
	 */
	public CompositeIterator(Iterator[] iterators) {
		this(new ArrayIterator(iterators));
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		try {
			this.loadCurrentIterator();
		} catch (NoSuchElementException ex) {
			// this occurs if there are no iterators at all
			return false;
		}
		return this.currentIterator.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		this.loadCurrentIterator();
		Object result = this.currentIterator.next();

		// the statement above will throw a NoSuchElementException
		// if the current iterator is at the end of the line;
		// so if we get here, we can set 'lastIteratorToReturnNext'
		this.lastIteratorToReturnNext = this.currentIterator;

		return result;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		if (this.lastIteratorToReturnNext == null) {
			// CompositeIterator#next() has never been called
			throw new IllegalStateException();
		}
		this.lastIteratorToReturnNext.remove();
	}

	/**
	 * Load currentIterator with the first iterator that <code>hasNext()</code>
	 * or the final iterator if all the elements have already been retrieved.
	 */
	private void loadCurrentIterator() {
		if (this.currentIterator == null) {
			this.currentIterator = (Iterator) this.iterators.next();
		}
		while (( ! this.currentIterator.hasNext()) && this.iterators.hasNext()) {
			this.currentIterator = (Iterator) this.iterators.next();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.iterators + ')';
	}

}
