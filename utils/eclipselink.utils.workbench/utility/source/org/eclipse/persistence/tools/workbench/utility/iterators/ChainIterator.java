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


/**
 * A <code>ChainIterator</code> provides a pluggable <code>Iterator</code>
 * that loops over a chain of arbitrarily linked objects. The chain
 * should be null-terminated (i.e. a call to the <code>nextLink(Object)</code>
 * method should return <code>null</code> when it is passed the last
 * link of the chain).
 * To use, supply a starting link and supply a <code>Linker</code> or 
 * subclass <code>ChainIterator</code> and override the
 * <code>nextLink(Object)</code> method.
 * The starting link will be the first object returned by the iterator.
 * If the starting link is <code>null</code>, the iterator will be empty.
 * Note that the iterator does not support <code>null</code> elements.
 */
public class ChainIterator
	implements Iterator
{
	private Object nextLink;
	private Linker linker;


	/**
	 * Construct an iterator with the specified starting link
	 * and a linker that simply returns null, indicating the end of the chain.
	 * Use this constructor if you want to override the
	 * <code>nextLink(Object)</code> method instead of building
	 * a <code>Linker</code>.
	 */
	public ChainIterator(Object startLink) {
		this(startLink, Linker.NULL_INSTANCE);
	}
	
	/**
	 * Construct an iterator with the specified starting link
	 * and linker.
	 */
	public ChainIterator(Object startLink, Linker linker) {
		super();
		this.nextLink = startLink;
		this.linker = linker;
	}
	
	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.nextLink != null;
	}
	
	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.nextLink == null) {
			throw new NoSuchElementException();
		}
		Object result = this.nextLink;
		this.nextLink = this.nextLink(this.nextLink);
		return result;
	}
	
	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return the next link in the chain.
	 */
	protected Object nextLink(Object currentLink) {
		return this.linker.nextLink(currentLink);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.nextLink + ')';
	}
	

//********** inner classes **********

/**
 * Used by <code>ChainIterator</code> to link
 * the elements in the chain.
 */
public interface Linker {

	/**
	 * Return the next link in the chain.
	 */
	Object nextLink(Object currentLink);


	Linker NULL_INSTANCE =
		new Linker() {
			// simply return null, indicating the chain is ended
			public Object nextLink(Object currentLink) {
				return null;
			}
		};

}

}
