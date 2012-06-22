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
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;


/**
 * An adapter that allows us to make a CollectionValueModel behave like
 * a read-only ListValueModel, sorta.
 * 
 * To maintain a reasonably consistent appearance to client code, we
 * keep an internal list somewhat in synch with the wrapped collection.
 * 
 * NB: Since we only listen to the wrapped collection when we have
 * listeners ourselves and we can only stay in synch with the wrapped
 * collection while we are listening to it, results to various methods
 * (e.g. #size(), getItem(int)) will be unpredictable whenever
 * we do not have any listeners. This should not be too painful since,
 * most likely, client objects will also be listeners.
 */
public class CollectionListValueModelAdapter
	extends AbstractModel
	implements ListValueModel
{
	/** The wrapped collection value model. */
	protected CollectionValueModel collectionHolder;

	/** A listener that forwards any events fired by the collection holder. */
	protected CollectionChangeListener collectionChangeListener;

	/**
	 * Our internal list, which holds the same elements as
	 * the wrapped collection, but keeps them in order.
	 */
	// we declare this an ArrayList so we can use #clone() and #ensureCapacity(int)
	protected ArrayList list;


	// ********** constructors **********

	/**
	 * Wrap the specified CollectionValueModel.
	 */
	public CollectionListValueModelAdapter(CollectionValueModel collectionHolder) {
		super();
		if (collectionHolder == null) {
			throw new NullPointerException();
		}
		this.collectionHolder = collectionHolder;
		// postpone building the list and listening to the underlying collection
		// until we have listeners ourselves...
	}


	// ********** initialization **********

	protected void initialize() { // private-protected
		super.initialize();
		this.collectionChangeListener = this.buildCollectionChangeListener();
		this.list = new ArrayList();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}

	/**
	 * The wrapped collection has changed, forward an equivalent
	 * list change event to our listeners.
	 */
	protected CollectionChangeListener buildCollectionChangeListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				CollectionListValueModelAdapter.this.itemsAdded(e);
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				CollectionListValueModelAdapter.this.itemsRemoved(e);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				CollectionListValueModelAdapter.this.collectionChanged(e);
			}
			public String toString() {
				return "collection change listener";
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		// try to prevent backdoor modification of the list
		return new ReadOnlyListIterator(this.list);
	}


	// ********** ListValueModel implementation **********

	/**
	 * @see ListValueModel#addItem(int, Object)
	 */
	public void addItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#addItems(int, java.util.List)
	 */
	public void addItems(int index, List items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#removeItem(int)
	 */
	public Object removeItem(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#removeItems(int, int)
	 */
	public List removeItems(int index, int length) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#replaceItem(int, Object)
	 */
	public Object replaceItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#replaceItems(int, java.util.List)
	 */
	public List replaceItems(int index, List items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#getItem(int)
	 */
	public Object getItem(int index) {
		return this.list.get(index);
	}

	/**
	 * @see ListValueModel#size()
	 */
	public int size() {
		return this.list.size();
	}


	// ********** extend change support **********

	/**
	 * Override to start listening to the collection holder if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addListChangeListener(ListChangeListener)
	 */
	public void addListChangeListener(ListChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModel();
		}
		super.addListChangeListener(listener);
	}

	/**
	 * Override to start listening to the collection holder if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addListChangeListener(String, ListChangeListener)
	 */
	public void addListChangeListener(String listName, ListChangeListener listener) {
		if (listName == VALUE && this.hasNoListeners()) {
			this.engageModel();
		}
		super.addListChangeListener(listName, listener);
	}

	/**
	 * Override to stop listening to the collection holder if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeListChangeListener(ListChangeListener)
	 */
	public void removeListChangeListener(ListChangeListener listener) {
		super.removeListChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModel();
		}
	}

	/**
	 * Override to stop listening to the collection holder if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removeListChangeListener(String, ListChangeListener)
	 */
	public void removeListChangeListener(String listName, ListChangeListener listener) {
		super.removeListChangeListener(listName, listener);
		if (listName == VALUE && this.hasNoListeners()) {
			this.disengageModel();
		}
	}


	// ********** queries **********

	protected boolean hasListeners() {
		return this.hasAnyListChangeListeners(VALUE);
	}

	protected boolean hasNoListeners() {
		return ! this.hasListeners();
	}

	/**
	 * Return the index of the specified item, using object
	 * identity instead of equality.
	 */
	protected int lastIdentityIndexOf(Object o) {
		return this.lastIdentityIndexOf(o, this.list.size());
	}
	
	/**
	 * Return the last index of the specified item, starting just before the
	 * the specified endpoint, and using object identity instead of equality.
	 */
	protected int lastIdentityIndexOf(Object o, int end) {
		for (int i = end; i-- > 0; ) {
			if (this.list.get(i) == o) {
				return i;
			}
		}
		return -1;
	}
	

	// ********** behavior **********

	protected void buildList() {
		Iterator stream = (Iterator) this.collectionHolder.getValue();
		// if the new collection is empty, do nothing
		if (stream.hasNext()) {
			this.list.ensureCapacity(this.collectionHolder.size());
			while (stream.hasNext()) {
				this.list.add(stream.next());
			}
			this.postBuildList();
		}
	}

	/**
	 * Allow subclasses to manipulate the internal list before
	 * sending out change notification.
	 */
	protected void postBuildList() {
		// the default is to do nothing...
	}

	protected void engageModel() {
		this.collectionHolder.addCollectionChangeListener(VALUE, this.collectionChangeListener);
		// synch our list *after* we start listening to the collection holder,
		// since its value might change when a listener is added
		this.buildList();
	}

	protected void disengageModel() {
		this.collectionHolder.removeCollectionChangeListener(VALUE, this.collectionChangeListener);
		// clear out the list when we are not listening to the collection holder
		this.list.clear();
	}

	protected void itemsAdded(CollectionChangeEvent e) {
		List addedItems = CollectionTools.list(e.items());
		int index = indexToAddItems();
		this.list.addAll(index, addedItems);
		this.fireItemsAdded(VALUE, index, addedItems);
	}
	
    protected int indexToAddItems() {
        return this.list.size();
    }
    
	protected void itemsRemoved(CollectionChangeEvent e) {
		// we have to remove the items individually,
		// since they are probably not in sequence
		for (Iterator stream = e.items(); stream.hasNext(); ) {
			Object removedItem = stream.next();
			int index = this.lastIdentityIndexOf(removedItem);
			this.list.remove(index);
			this.fireItemRemoved(VALUE, index, removedItem);
		}
	}

	/**
	 * synchronize our internal list with the wrapped collection
	 * and fire the appropriate events
	 */
	protected void collectionChanged(CollectionChangeEvent e) {
		// put in empty check so we don't fire events unnecessarily
		if ( ! this.list.isEmpty()) {
			// either we clone the list here...
			List removedItems = this.list;
			// ...or we create a new one here (which is what we do)
			this.list = new ArrayList();
			this.fireItemsRemoved(VALUE, 0, removedItems);
		}

		this.buildList();
		// put in empty check so we don't fire events unnecessarily
		if ( ! this.list.isEmpty()) {
			this.fireItemsAdded(VALUE, 0, this.list);
		}
	}

	public void toString(StringBuffer sb) {
		sb.append(this.collectionHolder);
	}

}
