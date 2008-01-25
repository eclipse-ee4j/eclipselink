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

import java.util.Collection;
import java.util.Iterator;

/**
 * This adapter assumes that the two containers are Collections.
 */
public class CollectionAdapter
	implements ContainerDifferentiator.Adapter
{

	// singleton
	private static CollectionAdapter INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized CollectionAdapter instance() {
		if (INSTANCE == null) {
			INSTANCE = new CollectionAdapter();
		}
		return INSTANCE;
	}

	/**
	 * Allow subclasses.
	 */
	protected CollectionAdapter() {
		super();
	}

	/**
	 * @see ContainerDifferentiator.Adapter#diffIsFatal(Object, Object)
	 */
	public boolean diffIsFatal(Object object1, Object object2) {
		if ( ! (object1 instanceof Collection)) {
			return true;
		}
		if ( ! (object2 instanceof Collection)) {
			return true;
		}
		return false;
	}

	/**
	 * @see ContainerDifferentiator.Adapter#containerClass()
	 */
	public Class containerClass() {
		return Collection.class;
	}

	/**
	 * @see ContainerDifferentiator.Adapter#size(Object)
	 */
	public int size(Object container) {
		return ((Collection) container).size();
	}

	/**
	 * @see ContainerDifferentiator.Adapter#iterator(Object)
	 */
	public Iterator iterator(Object container) {
		return ((Collection) container).iterator();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "CollectionAdapter";
	}

}
