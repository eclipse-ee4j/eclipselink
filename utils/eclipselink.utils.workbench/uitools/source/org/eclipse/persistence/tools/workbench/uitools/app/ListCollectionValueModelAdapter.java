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
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;


/**
 * An adapter that allows us to make a ListValueModel behave like
 * a read-only CollectionValueModel, sorta.
 * 
 * We keep an internal collection somewhat in synch with the wrapped list.
 * 
 * NB: Since we only listen to the wrapped list when we have
 * listeners ourselves and we can only stay in synch with the wrapped
 * list while we are listening to it, results to various methods
 * (e.g. #size(), getValue()) will be unpredictable whenever
 * we do not have any listeners. This should not be too painful since,
 * most likely, client objects will also be listeners.
 */
public class ListCollectionValueModelAdapter
	extends AbstractModel
	implements CollectionValueModel
{
	/** The wrapped list value model. */
	protected ListValueModel listHolder;

	/** A listener that forwards any events fired by the list holder. */
	protected ListChangeListener listChangeListener;

	/**
	 * Our internal collection, which holds the same elements as
	 * the wrapped list.
	 */
	// we declare this an ArrayList so we can use #clone() and #ensureCapacity(int)
	protected ArrayList collection;


	// ********** constructors/initialization **********

	/**
	 * Wrap the specified ListValueModel.
	 */
	public ListCollectionValueModelAdapter(ListValueModel listHolder) {
		super();
		if (listHolder == null) {
			throw new NullPointerException();
		}
		this.listHolder = listHolder;
		// postpone building the collection and listening to the underlying list
		// until we have listeners ourselves...
	}

	protected void initialize() {
		super.initialize();
		this.listChangeListener = this.buildListChangeListener();
		this.collection = new ArrayList();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}

	/**
	 * The wrapped list has changed, forward an equivalent
	 * collection change event to our listeners.
	 */
	protected ListChangeListener buildListChangeListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ListCollectionValueModelAdapter.this.itemsAdded(e);
			}
			public void itemsRemoved(ListChangeEvent e) {
				ListCollectionValueModelAdapter.this.itemsRemoved(e);
			}
			public void itemsReplaced(ListChangeEvent e) {
				ListCollectionValueModelAdapter.this.itemsReplaced(e);
			}
			public void listChanged(ListChangeEvent e) {
				ListCollectionValueModelAdapter.this.listChanged(e);
			}
			public String toString() {
				return "list change listener";
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		// try to prevent backdoor modification of the list
		return new ReadOnlyIterator(this.collection);
	}


	// ********** CollectionValueModel implementation **********

	/**
	 * @see CollectionValueModel#addItem(Object)
	 */
	public void addItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#addItems(Collection)
	 */
	public void addItems(Collection items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#removeItem(Object)
	 */
	public void removeItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#removeItems(Collection)
	 */
	public void removeItems(Collection items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#size()
	 */
	public int size() {
		return this.collection.size();
	}


	// ********** extend change support **********

	/**
	 * Override to start listening to the list holder if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addCollectionChangeListener(CollectionChangeListener)
	 */
	public void addCollectionChangeListener(CollectionChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModel();
		}
		super.addCollectionChangeListener(listener);
	}

	/**
	 * Override to start listening to the list holder if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addCollectionChangeListener(String, CollectionChangeListener)
	 */
	public void addCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		if (collectionName == VALUE && this.hasNoListeners()) {
			this.engageModel();
		}
		super.addCollectionChangeListener(collectionName, listener);
	}

	/**
	 * Override to stop listening to the list holder if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeCollectionChangeListener(CollectionChangeListener)
	 */
	public void removeCollectionChangeListener(CollectionChangeListener listener) {
		super.removeCollectionChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModel();
		}
	}

	/**
	 * Override to stop listening to the list holder if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeCollectionChangeListener(String, CollectionChangeListener)
	 */
	public void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		super.removeCollectionChangeListener(collectionName, listener);
		if (collectionName == VALUE && this.hasNoListeners()) {
			this.disengageModel();
		}
	}


	// ********** queries **********

	protected boolean hasListeners() {
		return this.hasAnyCollectionChangeListeners(VALUE);
	}

	protected boolean hasNoListeners() {
		return ! this.hasListeners();
	}

	/**
	 * Return the index of the specified item, using object
	 * identity instead of equality.
	 */
	protected int lastIdentityIndexOf(Object o) {
		return this.lastIdentityIndexOf(o, this.collection.size());
	}
	
	/**
	 * Return the last index of the specified item, starting just before the
	 * the specified endpoint, and using object identity instead of equality.
	 */
	protected int lastIdentityIndexOf(Object o, int end) {
		for (int i = end; i-- > 0; ) {
			if (this.collection.get(i) == o) {
				return i;
			}
		}
		return -1;
	}
	

	// ********** behavior **********

	protected void buildCollection() {
		Iterator stream = (Iterator) this.listHolder.getValue();
		// if the new list is empty, do nothing
		if (stream.hasNext()) {
			this.collection.ensureCapacity(this.listHolder.size());
			while (stream.hasNext()) {
				this.collection.add(stream.next());
			}
		}
	}

	protected void engageModel() {
		this.listHolder.addListChangeListener(VALUE, this.listChangeListener);
		// synch our collection *after* we start listening to the list holder,
		// since its value might change when a listener is added
		this.buildCollection();
	}

	protected void disengageModel() {
		this.listHolder.removeListChangeListener(VALUE, this.listChangeListener);
		// clear out the collection when we are not listening to the list holder
		this.collection.clear();
	}

	protected void addInternalItems(Iterator items) {
		Collection addedItems = CollectionTools.collection(items);
		this.collection.addAll(addedItems);
		this.fireItemsAdded(VALUE, addedItems);
	}

	protected void itemsAdded(ListChangeEvent e) {
		this.addInternalItems(e.items());
	}

	protected void removeInternalItems(Iterator items) {
		// we have to remove the items individually,
		// since they are probably not in sequence
		while (items.hasNext()) {
			Object removedItem = items.next();
			int index = this.lastIdentityIndexOf(removedItem);
			this.collection.remove(index);
			this.fireItemRemoved(VALUE, removedItem);
		}
	}

	protected void itemsRemoved(ListChangeEvent e) {
		this.removeInternalItems(e.items());
	}

	protected void itemsReplaced(ListChangeEvent e) {
		this.removeInternalItems(e.replacedItems());
		this.addInternalItems(e.items());
	}

	/**
	 * synchronize our internal collection with the wrapped list
	 * and fire the appropriate events
	 */
	protected void listChanged(ListChangeEvent e) {
		// put in empty check so we don't fire events unnecessarily
		if ( ! this.collection.isEmpty()) {
			// either we clone the list here...
			Collection removedItems = this.collection;
			// ...or we create a new one here (which is what we do)
			this.collection = new ArrayList();
			this.fireItemsRemoved(VALUE, removedItems);
		}

		this.buildCollection();
		// put in empty check so we don't fire events unnecessarily
		if ( ! this.collection.isEmpty()) {
			this.fireItemsAdded(VALUE, this.collection);
		}
	}

	public void toString(StringBuffer sb) {
		sb.append(this.listHolder);
	}

}
