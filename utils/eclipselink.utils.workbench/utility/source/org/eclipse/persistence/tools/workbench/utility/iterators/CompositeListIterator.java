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

import java.util.*;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>CompositeListIterator</code> wraps a list
 * of <code>ListIterator</code>s and makes them appear to be a single
 * <code>ListIterator</code>.
 */
public class CompositeListIterator implements ListIterator {
	private ListIterator iterators;
	private ListIterator nextIterator;
	private int nextIndex;
 	/**
 	 * true if "next" was last returned; false if "previous" was last returned;
 	 * this determines the effect of remove(Object) on nextIndex
 	 */
	private boolean nextReturned;
	private ListIterator lastIteratorToReturnElement;


	/**
	 * Construct a list iterator with the specified list of list iterators.
	 */
	public CompositeListIterator(List iterators) {
		this(iterators.listIterator());
	}
	
	/**
	 * Construct a list iterator with the specified list of list iterators.
	 */
	public CompositeListIterator(ListIterator iterators) {
		super();
		this.iterators = iterators;
		this.nextIndex = 0;
		this.nextReturned = false;
	}
	
	/**
	 * Construct a list iterator with the specified object prepended
	 * to the specified iterator.
	 */
	public CompositeListIterator(Object object, ListIterator iterator) {
		this(new SingleElementListIterator(object), iterator);
	}
	
	/**
	 * Construct a list iterator with the specified object appended
	 * to the specified iterator.
	 */
	public CompositeListIterator(ListIterator iterator, Object object) {
		this(iterator, new SingleElementListIterator(object));
	}
	
	/**
	 * Construct a list iterator with the specified list iterators.
	 */
	public CompositeListIterator(ListIterator iterator1, ListIterator iterator2) {
		this(buildIterators(iterator1, iterator2));
	}
	
	/**
	 * Construct a list iterator with the specified list iterators.
	 */
	public CompositeListIterator(ListIterator iterator1, ListIterator iterator2, ListIterator iterator3) {
		this(buildIterators(iterator1, iterator2, iterator3));
	}
	
	/**
	 * Construct a list iterator with the specified array of list iterators.
	 */
	public CompositeListIterator(ListIterator[] iterators) {
		this(new ArrayListIterator(iterators));
	}
	
	private static ListIterator buildIterators(ListIterator iterator1, ListIterator iterator2) {
		List list = new ArrayList(2);
		list.add(iterator1);
		list.add(iterator2);
		return list.listIterator();
	}
	
	private static ListIterator buildIterators(ListIterator iterator1, ListIterator iterator2, ListIterator iterator3) {
		List list = new ArrayList(3);
		list.add(iterator1);
		list.add(iterator2);
		list.add(iterator3);
		return list.listIterator();
	}
	
	/**
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	public void add(Object o) {
		this.checkNextIterator();
		this.nextIterator.add(o);
		this.nextIndex++;
	}
	
	/**
	 * @see java.util.ListIterator#hasNext()
	 */
	public boolean hasNext() {
		try {
			this.loadNextIterator();
		} catch (NoSuchElementException ex) {
			// this occurs if there are no iterators at all
			return false;
		}
		return this.nextIterator.hasNext();
	}
	
	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		try {
			this.loadPreviousIterator();
		} catch (NoSuchElementException ex) {
			// this occurs if there are no iterators at all
			return false;
		}
		return this.nextIterator.hasPrevious();
	}
	
	/**
	 * @see java.util.ListIterator#next()
	 */
	public Object next() {
		this.loadNextIterator();
		Object result = this.nextIterator.next();
	
		// the statement above will throw a NoSuchElementException
		// if the current iterator is at the end of the line;
		// so if we get here, we can set the lastIteratorToReturnElement
		this.lastIteratorToReturnElement = this.nextIterator;
		this.nextIndex++;
		this.nextReturned = true;
	
		return result;
	}
	
	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return this.nextIndex;
	}
	
	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
		this.loadPreviousIterator();
		Object result = this.nextIterator.previous();
	
		// the statement above will throw a NoSuchElementException
		// if the current iterator is at the end of the line;
		// so if we get here, we can set the lastIteratorToReturnElement
		this.lastIteratorToReturnElement = this.nextIterator;
		this.nextIndex--;
		this.nextReturned = false;
	
		return result;
	}
	
	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		return this.nextIndex  - 1;
	}
	
	/**
	 * @see java.util.ListIterator#remove()
	 */
	public void remove() {
		if (this.lastIteratorToReturnElement == null) {
			throw new IllegalStateException();
		}
		this.lastIteratorToReturnElement.remove();
		if (this.nextReturned) {
			// decrement the index because the "next" element has moved forward in the list
			this.nextIndex--;
		}
	}
	
	/**
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	public void set(Object o) {
		if (this.lastIteratorToReturnElement == null) {
			throw new IllegalStateException();
		}
		this.lastIteratorToReturnElement.set(o);
	}
	
	/**
	 * Load nextIterator with the first iterator that <code>hasNext()</code>
	 * or the final iterator if all the elements have already been retrieved.
	 */
	private void loadNextIterator() {
		this.checkNextIterator();
		while (( ! this.nextIterator.hasNext()) && this.iterators.hasNext()) {
			this.nextIterator = (ListIterator) this.iterators.next();
		}
	}
	
	/**
	 * Load nextIterator with the first iterator that <code>hasPrevious()</code>
	 * or the first iterator if all the elements have already been retrieved.
	 */
	private void loadPreviousIterator() {
		this.checkNextIterator();
		while (( ! this.nextIterator.hasPrevious()) && this.iterators.hasPrevious()) {
			this.nextIterator = (ListIterator) this.iterators.previous();
		}
	}
	
	/**
	 * If nextIterator is null, load it with the first iterator.
	 */
	private void checkNextIterator() {
		if (this.nextIterator == null) {
			this.nextIterator = (ListIterator) this.iterators.next();
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.iterators + ')';
	}
	
}
