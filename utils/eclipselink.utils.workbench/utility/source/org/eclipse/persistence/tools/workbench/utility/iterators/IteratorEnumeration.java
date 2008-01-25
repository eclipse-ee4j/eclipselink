/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * An <code>IteratorEnumeration</code> wraps an
 * <code>Iterator</code> so that it can be treated like an
 * <code>Enumeration</code>.
 */
public class IteratorEnumeration implements Enumeration {
	private Iterator iterator;

	/**
	 * Construct an enumeration that wraps the specified iterator.
	 */
	public IteratorEnumeration(Iterator iterator) {
		super();
		this.iterator = iterator;
	}

	/**
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return this.iterator.hasNext();
	}

	/**
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement() {
		return this.iterator.next();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.iterator + ')';
	}
	
}
