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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


/**
 * A <code>FilteringIterator</code> wraps another <code>Iterator</code>
 * and uses a <code>Filter</code> to determine which elements in the
 * nested iterator are to be returned by calls to <code>next()</code>.
 * <p>
 * As an alternative to building a <code>Filter</code>, a subclass
 * of <code>FilteringIterator</code> can override the
 * <code>accept(Object)</code> method.
 * <p>
 * One, possibly undesirable, side-effect of using this iterator is that
 * the nested iterator's <code>next()</code> method will be invoked
 * <em>before</em> the filtered iterator's <code>next()</code>
 * method is invoked. This is because the "next" element must be
 * checked for whether it is to be accepted before the filtered iterator
 * can determine whether it has a "next" element (i.e. that the
 * <code>hasNext()</code> method should return <code>true</code>).
 * This also prevents a filtered iterator from supporting the optional
 * <code>remove()</code> method.
 */
public class FilteringIterator implements Iterator {
	private Iterator nestedIterator;
	private Filter filter;
	private Object next;

	private static final Object START = new Object();
	private static final Object END = new Object();

	/**
	 * Construct an iterator with the specified nested
	 * iterator and a filter that simply accepts every object.
	 * Use this constructor if you want to override the
	 * <code>accept(Object)</code> method instead of building
	 * a <code>Filter</code>.
	 */
	public FilteringIterator(Iterator nestedIterator) {
		this(nestedIterator, Filter.NULL_INSTANCE);
	}
	
	/**
	 * Construct an iterator with the specified nested
	 * iterator and filter.
	 */
	public FilteringIterator(Iterator nestedIterator, Filter filter) {
		super();
		this.nestedIterator = nestedIterator;
		this.filter = filter;
		// postpone the pre-load until after the constructor has finished executing
		this.next = START;
	}
	
	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (this.next == START) {
			this.loadNext();
		}
		return this.next != END;
	}
	
	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.next == START) {
			this.loadNext();
		}
		if (this.next == END) {
			throw new NoSuchElementException();
		}
		Object result = this.next;
		this.loadNext();
		return result;
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
	 * Load next with the next valid entry from the nested
	 * iterator. If there are none, next is set to <code>END</code>.
	 */
	private void loadNext() {
		this.next = END;
		while (this.nestedIterator.hasNext() && (this.next == END)) {
			this.next = this.nestedIterator.next();
			if ( ! this.accept(this.next)) {
				this.next = END;
			}
		}
	}
	
	/**
	 * Return whether the <code>FilteringIterator</code>
	 * should return the specified next element from a call to the
	 * <code>next()</code> method.
	 * <p>
	 * This method can be overridden by a subclass as an
	 * alternative to building a <code>Filter</code>.
	 */
	protected boolean accept(Object o) {
		return this.filter.accept(o);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.nestedIterator + ')';
	}

}
