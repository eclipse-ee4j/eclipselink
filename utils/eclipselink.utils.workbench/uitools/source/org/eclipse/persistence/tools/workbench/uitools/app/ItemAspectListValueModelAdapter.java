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
import java.util.EventObject;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.Counter;
import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;


/**
 * Abstract list value model that provides behavior for wrapping a list value
 * model (or collection value model) and listening for changes to aspects of the
 * *items* held by the list (or collection). Changes to the actual list
 * (or collection) are also monitored.
 * 
 * This is useful if you have a collection of items that can be modified by adding
 * or removing items or the items themselves might change in a fashion that
 * might change the collection's external appearance.
 * 
 * Subclasses need to override two methods:
 * 
 * listenToItem(Model)
 *     begin listening to the appropriate aspect of the specified item and call
 *     #itemAspectChanged(Object) whenever the aspect changes
 * 
 * stopListeningToItem(Model)
 *     stop listening to the appropriate aspect of the specified item
 */
public abstract class ItemAspectListValueModelAdapter extends ListValueModelWrapper {

	/**
	 * Maintain a counter for each of the items in the
	 * wrapped list holder we are listening to.
	 */
	protected IdentityHashMap counters;


	// ********** constructors **********

	/**
	 * Constructor - the list holder is required.
	 */
	protected ItemAspectListValueModelAdapter(ListValueModel listHolder) {
		super(listHolder);
	}

	/**
	 * Constructor - the collection holder is required.
	 */
	protected ItemAspectListValueModelAdapter(CollectionValueModel collectionHolder) {
		this(new CollectionListValueModelAdapter(collectionHolder));
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.counters = new IdentityHashMap();
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		return this.listHolder.getValue();
	}


	// ********** ListValueModel implementation **********

	/**
	 * @see ListValueModel#addItem(int, Object)
	 */
	public void addItem(int index, Object item) {
		this.listHolder.addItem(index, item);
	}

	/**
	 * @see ListValueModel#addItems(int, java.util.List)
	 */
	public void addItems(int index, List items) {
		this.listHolder.addItems(index, items);
	}

	/**
	 * @see ListValueModel#removeItem(int)
	 */
	public Object removeItem(int index) {
		return this.listHolder.removeItem(index);
	}

	/**
	 * @see ListValueModel#removeItems(int, int)
	 */
	public List removeItems(int index, int length) {
		return this.listHolder.removeItems(index, length);
	}

	/**
	 * @see ListValueModel#replaceItem(int, Object)
	 */
	public Object replaceItem(int index, Object item) {
		return this.listHolder.replaceItem(index, item);
	}

	/**
	 * @see ListValueModel#replaceItems(int, java.util.List)
	 */
	public List replaceItems(int index, List items) {
		return this.listHolder.replaceItems(index, items);
	}

	/**
	 * @see ListValueModel#getItem(int)
	 */
	public Object getItem(int index) {
		return this.listHolder.getItem(index);
	}

	/**
	 * @see ListValueModel#size()
	 */
	public int size() {
		return this.listHolder.size();
	}


	// ********** behavior **********

	/**
	 * Start listening to the list holder and the items in the list.
	 */
	protected void engageModel() {
		super.engageModel();
		this.engageAllItems();
	}

	protected void engageAllItems() {
		this.engageItems((ListIterator) this.listHolder.getValue());
	}

	protected void engageItems(Iterator stream) {
		while (stream.hasNext()) {
			this.engageItem(stream.next());
		}
	}

	protected void engageItem(Object item) {
		// listen to an item only once
		Counter counter = (Counter) this.counters.get(item);
		if (counter == null) {
			counter = new Counter();
			this.counters.put(item, counter);
			this.startListeningToItem((Model) item);
		}
		counter.increment();
	}

	/**
	 * Start listening to the specified item.
	 */
	protected abstract void startListeningToItem(Model item);

	/**
	 * Stop listening to the list holder and the items in the list.
	 */
	protected void disengageModel() {
		this.disengageAllItems();
		super.disengageModel();
	}

	protected void disengageAllItems() {
		this.disengageItems((ListIterator) this.listHolder.getValue());
	}

	protected void disengageItems(Iterator stream) {
		while (stream.hasNext()) {
			this.disengageItem(stream.next());
		}
	}

	protected void disengageItem(Object item) {
		// stop listening to an item only once
		Counter counter = (Counter) this.counters.get(item);
		if (counter == null) {
			// something is wrong if this happens...  ~bjv
			throw new IllegalStateException("missing counter: " + item);
		}
		if (counter.decrement() == 0) {
			this.counters.remove(item);
			this.stopListeningToItem((Model) item);
		}
	}

	/**
	 * Stop listening to the specified item.
	 */
	protected abstract void stopListeningToItem(Model item);


	// ********** list change support **********

	/**
	 * Items were added to the wrapped list holder.
	 * Forward the event and begin listening to the added items.
	 */
	protected void itemsAdded(ListChangeEvent e) {
		// re-fire event with the wrapper as the source
		this.fireItemsAdded(e.cloneWithSource(this, VALUE));
		this.engageItems(e.items());
	}

	/**
	 * Items were removed from the wrapped list holder.
	 * Stop listening to the removed items and forward the event.
	 */
	protected void itemsRemoved(ListChangeEvent e) {
		this.disengageItems(e.items());
		// re-fire event with the wrapper as the source
		this.fireItemsRemoved(e.cloneWithSource(this, VALUE));
	}

	/**
	 * Items were replaced in the wrapped list holder.
	 * Stop listening to the removed items, forward the event,
	 * and begin listening to the added items.
	 */
	protected void itemsReplaced(ListChangeEvent e) {
		this.disengageItems(e.replacedItems());
		// re-fire event with the wrapper as the source
		this.fireItemsReplaced(e.cloneWithSource(this, VALUE));
		this.engageItems(e.items());
	}

	/**
	 * The wrapped list holder has changed in some dramatic fashion.
	 * Reconfigure our listeners and forward the event.
	 */
	protected void listChanged(ListChangeEvent e) {
		// we should only need to disengage each item once...
		// make a copy to prevent a ConcurrentModificationException
		Collection keys = new ArrayList(this.counters.keySet());
		this.disengageItems(keys.iterator());
		this.counters.clear();
		// re-fire event with the wrapper as the source
		this.fireListChanged(VALUE);
		this.engageAllItems();
	}


	// ********** item change support **********

	/**
	 * The specified item has a bound property that has changed.
	 * Notify listeners of the change.
	 */
	protected void itemAspectChanged(EventObject e) {
		Object item = e.getSource();
		int index = this.lastIdentityIndexOf(item);
		while (index != -1) {
			this.itemAspectChanged(index, item);
			index = this.lastIdentityIndexOf(item, index);
		}
	}

	/**
	 * The specified item has a bound property that has changed.
	 * Notify listeners of the change.
	 */
	protected void itemAspectChanged(int index, Object item) {
		this.fireItemReplaced(VALUE, index, item, item);		// hmmm...
	}

	/**
	 * Return the last index of the specified item, using object
	 * identity instead of equality.
	 */
	protected int lastIdentityIndexOf(Object o) {
		return this.lastIdentityIndexOf(o, this.listHolder.size());
	}

	/**
	 * Return the last index of the specified item, starting just before the
	 * the specified endpoint, and using object identity instead of equality.
	 */
	protected int lastIdentityIndexOf(Object o, int end) {
		for (int i = end; i-- > 0; ) {
			if (this.listHolder.getItem(i) == o) {
				return i;
			}
		}
		return -1;
	}

}
