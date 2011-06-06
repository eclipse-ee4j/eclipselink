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
 * A <code>SingleElementIterator</code> holds a single element
 * and returns it with the first call to <code>next()</code>, at
 * which point it will return <code>false</code> to any subsequent
 * call to <code>hasNext()</code>.
 * <p>
 * A <code>SingleElementIterator</code> is equivalent to the
 * <code>Iterator</code> returned by:
 * 	<code>java.util.Collections.singleton(element).iterator()</code>
 */
public class SingleElementIterator implements Iterator {
	private Object element;
	private static final Object END = new Object();


	/**
	 * Construct an iterator that returns only the specified element.
	 */
	public SingleElementIterator(Object element) {
		super();
		this.element = element;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.element != END;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.element == END) {
			throw new NoSuchElementException();
		}
		Object result = this.element;
		this.element = END;
		return result;
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
		return ClassTools.shortClassNameForObject(this) + '(' + this.element + ')';
	}

}
