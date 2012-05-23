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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;


/**
 * Implementation of CollectionValueModel that simply holds on to a
 * collection and uses it as the value.
 */
public class SimpleCollectionValueModel
	extends AbstractModel
	implements CollectionValueModel
{
	/** The value. */
	protected Collection value;


	/**
	 * Construct a CollectionValueModel for the specified value.
	 */
	public SimpleCollectionValueModel(Collection value) {
		super();
		this.setValue(value);
	}

	/**
	 * Construct a CollectionValueModel with an initial
	 * value of an empty collection.
	 */
	public SimpleCollectionValueModel() {
		this(new HashBag());
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
		// try to prevent backdoor modification of the collection
		return new ReadOnlyIterator(this.value);
	}


	// ********** CollectionValueModel implementation **********

	/**
	 * @see CollectionValueModel#addItem(Object)
	 */
	public void addItem(Object item) {
		this.addItemToCollection(item, this.value, VALUE);
	}

	/**
	 * @see CollectionValueModel#addItems(Collection)
	 */
	public void addItems(Collection items) {
		this.addItemsToCollection(items, this.value, VALUE);
	}

	/**
	 * @see CollectionValueModel#removeItem(Object)
	 */
	public void removeItem(Object item) {
		this.removeItemFromCollection(item, this.value, VALUE);
	}

	/**
	 * @see CollectionValueModel#removeItems(Collection)
	 */
	public void removeItems(Collection items) {
		this.removeItemsFromCollection(items, this.value, VALUE);
	}

	/**
	 * @see CollectionValueModel#size()
	 */
	public int size() {
		return this.value.size();
	}


	// ********** behavior **********

	/**
	 * Allow the value to be replaced.
	 */
	public void setValue(Collection value) {
		this.value = ((value == null) ? new HashBag() : value);
		this.fireCollectionChanged(VALUE);
	}

	/**
	 * Allow the value to be cleared.
	 */
	public void clear() {
		if (this.value.isEmpty()) {
			return;
		}
		Collection items = new ArrayList(this.value);
		this.value.clear();
		this.fireItemsRemoved(VALUE, items);
	}

	public void toString(StringBuffer sb) {
		sb.append(this.value);
	}

}
