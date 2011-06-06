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
package org.eclipse.persistence.tools.workbench.utility.diff;

import java.util.List;

/**
 * This adapter assumes that the two containers are Lists.
 */
public class ListAdapter
	implements OrderedContainerDifferentiator.Adapter
{

	// singleton
	private static ListAdapter INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized ListAdapter instance() {
		if (INSTANCE == null) {
			INSTANCE = new ListAdapter();
		}
		return INSTANCE;
	}

	/**
	 * Allow subclasses.
	 */
	protected ListAdapter() {
		super();
	}

	/**
	 * @see OrderedContainerDifferentiator.Adapter#diffIsFatal(Object, Object)
	 */
	public boolean diffIsFatal(Object object1, Object object2) {
		if ( ! (object1 instanceof List)) {
			return true;
		}
		if ( ! (object2 instanceof List)) {
			return true;
		}
		return false;
	}

	/**
	 * @see OrderedContainerDifferentiator.Adapter#containerClass()
	 */
	public Class containerClass() {
		return List.class;
	}

	/**
	 * @see OrderedContainerDifferentiator.Adapter#size(Object)
	 */
	public int size(Object container) {
		return ((List) container).size();
	}

	/**
	 * @see OrderedContainerDifferentiator.Adapter#get(Object, int)
	 */
	public Object get(Object container, int index) {
		return ((List) container).get(index);
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "ListAdapter";
	}

}
