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
import java.util.NoSuchElementException;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>NullEnumeration</code> is just that.
 */
public final class NullEnumeration implements Enumeration {

	// singleton
	private static NullEnumeration INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized Enumeration instance() {
		if (INSTANCE == null) {
			INSTANCE = new NullEnumeration();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullEnumeration() {
		super();
	}

	/**
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return false;
	}

	/**
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement() {
		throw new NoSuchElementException();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}
	
}
