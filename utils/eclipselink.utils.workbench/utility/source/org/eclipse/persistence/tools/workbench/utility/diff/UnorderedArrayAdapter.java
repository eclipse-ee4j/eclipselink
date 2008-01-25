/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.diff;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;


/**
 * This adapter assumes that the two containers are object arrays.
 */
public class UnorderedArrayAdapter
	implements ContainerDifferentiator.Adapter
{

	// singleton
	private static UnorderedArrayAdapter INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized UnorderedArrayAdapter instance() {
		if (INSTANCE == null) {
			INSTANCE = new UnorderedArrayAdapter();
		}
		return INSTANCE;
	}

	/**
	 * Allow subclasses.
	 */
	protected UnorderedArrayAdapter() {
		super();
	}

	/**
	 * @see ContainerDifferentiator.Adapter#diffIsFatal(Object, Object)
	 */
	public boolean diffIsFatal(Object object1, Object object2) {
		if ((object1 == null) || ! (object1.getClass().isArray())) {
			return true;
		}
		if ((object2 == null) || ! (object2.getClass().isArray())) {
			return true;
		}
		return false;
	}

	/**
	 * @see ContainerDifferentiator.Adapter#containerClass()
	 */
	public Class containerClass() {
		return Array.class;
	}

	/**
	 * @see ContainerDifferentiator.Adapter#size(Object)
	 */
	public int size(Object container) {
		return ((Object[]) container).length;
	}

	/**
	 * @see ContainerDifferentiator.Adapter#iterator(Object)
	 */
	public Iterator iterator(Object container) {
		return new ArrayIterator((Object[]) container);
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "UnorderedArrayAdapter";
	}

}
