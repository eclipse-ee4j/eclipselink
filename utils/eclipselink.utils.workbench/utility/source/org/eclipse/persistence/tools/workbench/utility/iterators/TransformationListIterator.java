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

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;


/**
 * A <code>TransformationListIterator</code> wraps another <code>ListIterator</code>
 * and transforms it results for client consumption. To use, supply a 
 * <code>Transformer</code> or subclass <code>TransformationIterator</code>
 * and override the <code>transform(Object)</code> method.
 * 
 * The methods <code>set(Object)</code> and <code>add(Object)</code>
 * are left unsupported in this class.
 */
public class TransformationListIterator implements ListIterator {
	private ListIterator nestedIterator;
	private Transformer transformer;
	
	/**
	 * Construct an iterator with the specified nested iterator
	 * and a transformer that simply returns the object, unchanged.
	 * Use this constructor if you want to override the
	 * <code>transform(Object)</code> method instead of building
	 * a <code>Transformer</code>.
	 */
	public TransformationListIterator(ListIterator nestedIterator) {
		this(nestedIterator, Transformer.NULL_INSTANCE);
	}
	
	/**
	 * Construct an iterator with the specified nested iterator
	 * and transformer.
	 */
	public TransformationListIterator(ListIterator nestedIterator, Transformer transformer) {
		super();
		this.nestedIterator = nestedIterator;
		this.transformer = transformer;
	}
	
	/**
	 * @see java.util.ListIterator#hasNext()
	 */
	public boolean hasNext() {
		// delegate to the nested iterator
		return this.nestedIterator.hasNext();
	}
	
	/**
	 * @see java.util.ListIterator#next()
	 */
	public Object next() {
		// transform the object returned by the nested iterator before returning it
		return this.transform(this.nestedIterator.next());
	}
	
	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		// delegate to the nested iterator
		return this.nestedIterator.nextIndex();
	}
	
	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		// delegate to the nested iterator
		return this.nestedIterator.hasPrevious();
	}
	
	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
		// transform the object returned by the nested iterator before returning it
		return this.transform(this.nestedIterator.previous());
	}
	
	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		// delegate to the nested iterator
		return this.nestedIterator.previousIndex();
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
		// delegate to the nested iterator
		this.nestedIterator.remove();
	}
	
	/**
	 * Transform the specified object and return the result.
	 */
	protected Object transform(Object next) {
		return this.transformer.transform(next);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.nestedIterator + ')';
	}
}
