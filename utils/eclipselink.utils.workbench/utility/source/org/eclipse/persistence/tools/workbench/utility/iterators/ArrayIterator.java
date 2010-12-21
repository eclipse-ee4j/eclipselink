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
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * An <code>ArrayIterator</code> provides an <code>Iterator</code>
 * for an array of objects.
 */
public class ArrayIterator
	implements Iterator
{
	Object[] array;	// private-protected
	int nextIndex;		// private-protected
	private int maxIndex;

	/**
	 * Construct an iterator for the specified array.
	 */
	public ArrayIterator(Object[] array) {
		this(array, 0, array.length);
	}
	
	/**
	 * Construct an iterator for the specified array,
	 * starting at the specified start index and continuing for
	 * the specified length.
	 */
	public ArrayIterator(Object[] array, int start, int length) {
		if ((start < 0) || (start > array.length)) {
			throw new IllegalArgumentException("start: " + start);
		}
		if ((length < 0) || (length > array.length - start)) {
			throw new IllegalArgumentException("length: " + length);
		}
		this.array = array;
		this.nextIndex = start;
		this.maxIndex = start + length;
	}
	
	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.nextIndex < this.maxIndex;
	}
	
	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.hasNext()) {
			return this.array[this.nextIndex++];
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + CollectionTools.list(this.array) + ')';
	}
	
}
