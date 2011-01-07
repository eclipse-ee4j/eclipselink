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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;


/**
 * Implementation of ListValueModel that simply holds on to a
 * list and uses it as the value.
 */
public class SimpleListValueModel
	extends AbstractModel
	implements ListValueModel
{
	/** The value. */
	protected List value;


	/**
	 * Construct a ListValueModel for the specified value.
	 */
	public SimpleListValueModel(List value) {
		super();
		this.setValue(value);
	}

	/**
	 * Construct a ListValueModel with an initial value
	 * of an empty list
	 */
	public SimpleListValueModel() {
		this(new ArrayList());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		// try to prevent backdoor modification of the list
		return new ReadOnlyListIterator(this.value);
	}

	// ********** ListValueModel implementation **********

	/**
	 * @see ListValueModel#addItem(int, Object)
	 */
	public void addItem(int index, Object item) {
		this.addItemToList(index, item, this.value, VALUE);
	}

	/**
	 * @see ListValueModel#addItems(int, java.util.List)
	 */
	public void addItems(int index, List items) {
		this.addItemsToList(index, items, this.value, VALUE);
	}

	/**
	 * @see ListValueModel#removeItem(int)
	 */
	public Object removeItem(int index) {
		return this.removeItemFromList(index, this.value, VALUE);
	}

	/**
	 * @see ListValueModel#removeItems(int, int)
	 */
	public List removeItems(int index, int length) {
		return this.removeItemsFromList(index, length, this.value, VALUE);
	}

	/**
	 * @see ListValueModel#replaceItem(int, Object)
	 */
	public Object replaceItem(int index, Object item) {
		return this.setItemInList(index, item, this.value, VALUE);
	}

	/**
	 * @see ListValueModel#replaceItems(int, java.util.List)
	 */
	public List replaceItems(int index, List items) {
		return this.setItemsInList(index, items, this.value, VALUE);
	}

	/**
	 * @see ListValueModel#getItem(int)
	 */
	public Object getItem(int index) {
		return this.value.get(index);
	}

	/**
	 * @see ListValueModel#size()
	 */
	public int size() {
		return this.value.size();
	}


	// ********** behavior **********

	/**
	 * Allow the value to be replaced.
	 */
	public void setValue(List value) {
		this.value = ((value == null) ? new ArrayList() : value);
		this.fireListChanged(VALUE);
	}

	/**
	 * Add the specified item to the end of the list.
	 */
	public void addItem(Object item) {
		this.addItem(this.size(), item);
	}

	/**
	 * Return the index of the first occurrence of the specified item.
	 */
	public int indexOfItem(Object item) {
		return this.value.indexOf(item);
	}

	/**
	 * Remove the first occurrence of the specified item.
	 */
	public void removeItem(Object item) {
		this.removeItem(this.indexOfItem(item));
	}

	/**
	 * Allow the value to be cleared.
	 */
	public void clear() {
		if (this.value.isEmpty()) {
			return;
		}
		List items = new ArrayList(this.value);
		this.value.clear();
		this.fireItemsRemoved(VALUE, 0, items);
	}

	public void toString(StringBuffer sb) {
		sb.append(this.value);
	}

}
