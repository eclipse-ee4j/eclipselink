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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.List;

/**
 * Extend ValueModel to allow the adding and
 * removing of items in a list value.
 * Typically the value returned from #getValue()
 * will be a ListIterator.
 */
public interface ListValueModel extends ValueModel {

	/**
	 * Add the item at the specified index in the list value.
	 */
	void addItem(int index, Object item);
	
	/**
	 * Add the items at the specified index in the list value.
	 */
	void addItems(int index, List items);
	
	/**
	 * Remove the item at the specified index from the list value
	 * and return it.
	 */
	Object removeItem(int index);
	
	/**
	 * Remove the items from the list value, starting at the specified index
	 * for the specified length. Return a list containing the removed items.
	 */
	List removeItems(int index, int length);
	
	/**
	 * Replace the item at the specified index of the list value
	 * and return the item that was there previously.
	 */
	Object replaceItem(int index, Object item);
	
	/**
	 * Replace the items at the specified index of the list value
	 * and return the items that were there previously.
	 */
	List replaceItems(int index, List items);
	
	/**
	 * Return the item at the specified index of the list value.
	 */
	Object getItem(int index);
	
	/**
	 * Return the size of the list value.
	 */
	int size();

}
