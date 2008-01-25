/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.Collection;

/**
 * Extend ValueModel to allow the adding and
 * removing of items in a collection value.
 * Typically the value returned from #getValue()
 * will be an Iterator.
 */
public interface CollectionValueModel extends ValueModel {

	/**
	 * Add the specified item to the collection value.
	 */
	void addItem(Object item);

	/**
	 * Add the specified items to the collection value.
	 */
	void addItems(Collection items);

	/**
	 * Remove the specified item from the collection value.
	 */
	void removeItem(Object item);

	/**
	 * Remove the specified items from the collection value.
	 */
	void removeItems(Collection items);

	/**
	 * Return the size of the collection value.
	 */
	int size();

}
