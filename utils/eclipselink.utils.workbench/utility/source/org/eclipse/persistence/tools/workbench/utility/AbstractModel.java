/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Convenience implementation of Model.
 */
public abstract class AbstractModel implements Model {
	/**
	 * Delegate state/property/collection/list/tree change support to this
	 * helper object. The change support object is "lazy-initialized".
	 */
	private ChangeSupport changeSupport;


	// ********** constructors/initialization **********
	
	/**
	 * Default constructor.
	 * This will call #initialize() on the newly-created instance.
	 */
	protected AbstractModel() {
		super();
		this.initialize();
	}
	
	protected void initialize() {
		// do nothing by default
	}

	/**
	 * This accessor will build the change support when required.
	 */
	private ChangeSupport changeSupport() {
		if (this.changeSupport == null) {
			this.changeSupport = this.buildDefaultChangeSupport();
		}
		return this.changeSupport;
	}

	protected ChangeSupport buildDefaultChangeSupport() {
		return new ChangeSupport(this);
	}


	// ********** state change support **********
	
	/**
	 * @see Model#addStateChangeListener(StateChangeListener)
	 */
	public synchronized void addStateChangeListener(StateChangeListener listener) {
		this.changeSupport().addStateChangeListener(listener);
	}
	
	/**
	 * @see Model#removeStateChangeListener(StateChangeListener)
	 */
	public synchronized void removeStateChangeListener(StateChangeListener listener) {
		this.changeSupport().removeStateChangeListener(listener);
	}
	
	protected final void fireStateChanged() {
		this.changeSupport().fireStateChanged();
	}
	
	protected final void fireStateChanged(StateChangeEvent evt) {
		this.changeSupport().fireStateChanged(evt);
	}
	

	// ********** property change support **********
	
	/**
	 * @see Model#addPropertyChangeListener(PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		this.changeSupport().addPropertyChangeListener(listener);
	}
	
	/**
	 * @see Model#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.changeSupport().addPropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * @see Model#removePropertyChangeListener(PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		this.changeSupport().removePropertyChangeListener(listener);
	}
	
	/**
	 * @see Model#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.changeSupport().removePropertyChangeListener(propertyName, listener);
	}
	
	protected final void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		this.changeSupport().firePropertyChanged(propertyName, oldValue, newValue);
	}
	
	protected final void firePropertyChanged(String propertyName, int oldValue, int newValue) {
		this.changeSupport().firePropertyChanged(propertyName, oldValue, newValue);
	}
	
	protected final void firePropertyChanged(String propertyName, boolean oldValue, boolean newValue) {
		this.changeSupport().firePropertyChanged(propertyName, oldValue, newValue);
	}
	
	protected final void firePropertyChanged(String propertyName, Object newValue) {
		this.changeSupport().firePropertyChanged(propertyName, null, newValue);
	}
		
	protected final void firePropertyChanged(PropertyChangeEvent evt) {
		this.changeSupport().firePropertyChanged(evt);
	}
		

	// ********** collection change support **********
	
	/**
	 * @see Model#addCollectionChangeListener(CollectionChangeListener)
	 */
	public synchronized void addCollectionChangeListener(CollectionChangeListener listener) {
		this.changeSupport().addCollectionChangeListener(listener);
	}
	
	/**
	 * @see Model#addCollectionChangeListener(String, CollectionChangeListener)
	 */
	public synchronized void addCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		this.changeSupport().addCollectionChangeListener(collectionName, listener);
	}
	
	/**
	 * @see Model#removeCollectionChangeListener(CollectionChangeListener)
	 */
	public synchronized void removeCollectionChangeListener(CollectionChangeListener listener) {
		this.changeSupport().removeCollectionChangeListener(listener);
	}
	
	/**
	 * @see Model#removeCollectionChangeListener(String, CollectionChangeListener)
	 */
	public synchronized void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		this.changeSupport().removeCollectionChangeListener(collectionName, listener);
	}
	
	protected final void fireItemAdded(String collectionName, Object addedItem) {
		this.changeSupport().fireItemAdded(collectionName, addedItem);
	}
	
	protected final void fireItemsAdded(String collectionName, Collection addedItems) {
		this.changeSupport().fireItemsAdded(collectionName, addedItems);
	}
	
	protected final void fireItemsAdded(CollectionChangeEvent evt) {
		this.changeSupport().fireItemsAdded(evt);
	}
	
	protected final void fireItemRemoved(String collectionName, Object removedItem) {
		this.changeSupport().fireItemRemoved(collectionName, removedItem);
	}

	protected final void fireItemsRemoved(String collectionName, Collection removedItems) {
		this.changeSupport().fireItemsRemoved(collectionName, removedItems);
	}

	protected final void fireItemsRemoved(CollectionChangeEvent evt) {
		this.changeSupport().fireItemsRemoved(evt);
	}

	protected final void fireCollectionChanged(String collectionName) {
		this.changeSupport().fireCollectionChanged(collectionName);
	}

	protected final void fireCollectionChanged(CollectionChangeEvent evt) {
		this.changeSupport().fireCollectionChanged(evt);
	}

	/**
	 * Convenience method.
	 * Add the specified item to the specified bound collection
	 * and fire the appropriate event if necessary.
	 * Return whether the collection changed.
	 */
	protected boolean addItemToCollection(Object item, Collection collection, String collectionName) {
		if (collection.add(item)) {
			this.fireItemAdded(collectionName, item);
			return true;
		}
		return false;
	}

	/**
	 * Convenience method.
	 * Add the specified items to the specified bound collection
	 * and fire the appropriate event if necessary.
	 * Return whether collection changed.
	 */
	protected boolean addItemsToCollection(Collection items, Collection collection, String collectionName) {
		return this.addItemsToCollection(items.iterator(), collection, collectionName);
	}

	/**
	 * Convenience method.
	 * Add the specified items to the specified bound collection
	 * and fire the appropriate event if necessary.
	 * Return whether collection changed.
	 */
	protected boolean addItemsToCollection(Iterator items, Collection collection, String collectionName) {
		Collection addedItems = null;
		while (items.hasNext()) {
			Object item = items.next();
			if (collection.add(item)) {
				if (addedItems == null) {
					addedItems = new ArrayList();
				}
				addedItems.add(item);
			}
		}
		if (addedItems != null) {
			this.fireItemsAdded(collectionName, addedItems);
			return true;
		}
		return false;
	}

	/**
	 * Convenience method.
	 * Remove the specified item from the specified bound collection
	 * and fire the appropriate event if necessary.
	 * Return whether the collection changed.
	 * @see java.util.Collection#remove(Object)
	 */
	protected boolean removeItemFromCollection(Object item, Collection collection, String collectionName) {
		if (collection.remove(item)) {
			this.fireItemRemoved(collectionName, item);
			return true;
		}
		return false;
	}

	/**
	 * Convenience method.
	 * Remove the specified items from the specified bound collection
	 * and fire the appropriate event if necessary.
	 * Return whether the collection changed.
	 * @see java.util.Collection#remove(Object)
	 */
	protected boolean removeItemsFromCollection(Collection items, Collection collection, String collectionName) {
		return this.removeItemsFromCollection(items.iterator(), collection, collectionName);
	}

	/**
	 * Convenience method.
	 * Remove the specified items from the specified bound collection
	 * and fire the appropriate event if necessary.
	 * Return whether the collection changed.
	 * @see java.util.Collection#remove(Object)
	 */
	protected boolean removeItemsFromCollection(Iterator items, Collection collection, String collectionName) {
		Collection removedItems = CollectionTools.collection(items);
		removedItems.retainAll(collection);
		boolean changed = collection.removeAll(removedItems);
		
		if ( ! removedItems.isEmpty()) {
			this.fireItemsRemoved(collectionName, removedItems);
		}
		return changed;
	}
	
	/**
	 * Convenience method.
	 * Clear the entire collection
	 * and fire the appropriate event if necessary.
	 * Return whether the list changed.
	 */
	protected boolean clearCollection(Collection collection, String collectionName) {
		if (collection.isEmpty()) {
			return false;
		}
		collection.clear();
		this.fireCollectionChanged(collectionName);
		return true;
	}
	
	/**
	 * Convenience method.
	 * Synchronize the collection with the specified new collection,
	 * making a minimum number of removes and adds.
	 */
	protected void synchronizeCollection(Collection newCollection, Collection collection, String collectionName) {
		Collection removedItems = new HashBag(collection);
		removedItems.removeAll(newCollection);
		this.removeItemsFromCollection(removedItems, collection, collectionName);

		Collection addedItems = new HashBag(newCollection);
		addedItems.removeAll(collection);
		this.addItemsToCollection(addedItems, collection, collectionName);
	}

	/**
	 * Convenience method.
	 * Synchronize the collection with the specified new collection,
	 * making a minimum number of removes and adds.
	 */
	protected void synchronizeCollection(Iterator newItems, Collection collection, String collectionName) {
		this.synchronizeCollection(CollectionTools.collection(newItems), collection, collectionName);
	}


	// ********** list change support **********
	
	/**
	 * @see Model#addListChangeListener(ListChangeListener)
	 */
	public synchronized void addListChangeListener(ListChangeListener listener) {
		this.changeSupport().addListChangeListener(listener);
	}
	
	/**
	 * @see Model#addListChangeListener(String, ListChangeListener)
	 */
	public synchronized void addListChangeListener(String listName, ListChangeListener listener) {
		this.changeSupport().addListChangeListener(listName, listener);
	}
	
	/**
	 * @see Model#removeListChangeListener(ListChangeListener)
	 */
	public synchronized void removeListChangeListener(ListChangeListener listener) {
		this.changeSupport().removeListChangeListener(listener);
	}
	
	/**
	 * @see Model#removeListChangeListener(String, ListChangeListener)
	 */
	public synchronized void removeListChangeListener(String listName, ListChangeListener listener) {
		this.changeSupport().removeListChangeListener(listName, listener);
	}
	
	protected final void fireItemAdded(String listName, int index, Object addedItem) {
		this.changeSupport().fireItemAdded(listName, index, addedItem);
	}
	
	protected final void fireItemsAdded(String listName, int index, List addedItems) {
		this.changeSupport().fireItemsAdded(listName, index, addedItems);
	}
	
	protected final void fireItemsAdded(ListChangeEvent evt) {
		this.changeSupport().fireItemsAdded(evt);
	}
	
	protected final void fireItemRemoved(String listName, int index, Object removedItem) {
		this.changeSupport().fireItemRemoved(listName, index, removedItem);
	}

	protected final void fireItemsRemoved(String listName, int index, List removedItems) {
		this.changeSupport().fireItemsRemoved(listName, index, removedItems);
	}
	
	protected final void fireItemsRemoved(ListChangeEvent evt) {
		this.changeSupport().fireItemsRemoved(evt);
	}
	
	protected final void fireItemReplaced(String listName, int index, Object newItem, Object replacedItem) {
		this.changeSupport().fireItemReplaced(listName, index, newItem, replacedItem);
	}

	protected final void fireItemsReplaced(String listName, int index, List newItems, List replacedItems) {
		this.changeSupport().fireItemsReplaced(listName, index, newItems, replacedItems);
	}
	
	protected final void fireItemsReplaced(ListChangeEvent evt) {
		this.changeSupport().fireItemsReplaced(evt);
	}
	
	protected final void fireListChanged(String listName) {
		this.changeSupport().fireListChanged(listName);
	}
	
	protected final void fireListChanged(ListChangeEvent evt) {
		this.changeSupport().fireListChanged(evt);
	}
	
	/**
	 * Convenience method.
	 * Add the specified item to the specified bound list
	 * and fire the appropriate event if necessary.
	 */
	protected void addItemToList(int index, Object item, List list, String listName) {
		list.add(index, item);
		this.fireItemAdded(listName, index, item);
	}

	/**
	 * Convenience method.
	 * Add the specified item to the end of the specified bound list
	 * and fire the appropriate event if necessary.
	 */
	protected void addItemToList(Object item, List list, String listName) {
		this.addItemToList(list.size(), item, list, listName);
	}

	/**
	 * Convenience method.
	 * Add the specified items to the specified bound list
	 * and fire the appropriate event if necessary.
	 */
	protected void addItemsToList(int index, List items, List list, String listName) {
		list.addAll(index, items);
		this.fireItemsAdded(listName, index, items);
	}

	/**
	 * Convenience method.
	 * Add the specified items to the end of to the specified bound list
	 * and fire the appropriate event if necessary.
	 */
	protected void addItemsToList(List items, List list, String listName) {
		this.addItemsToList(list.size(), items, list, listName);
	}

	/**
	 * Convenience method.
	 * Remove the specified item from the specified bound list
	 * and fire the appropriate event if necessary.
	 * Return the removed item.
	 */
	protected Object removeItemFromList(int index, List list, String listName) {
		Object item = list.remove(index);
		this.fireItemRemoved(listName, index, item);
		return item;
	}

	/**
	 * Convenience method.
	 * Remove the specified item from the specified bound list
	 * and fire the appropriate event if necessary.
	 * Return the removed item.
	 */
	protected Object removeItemFromList(Object item, List list, String listName) {
		return this.removeItemFromList(list.indexOf(item), list, listName);
	}

	/**
	 * Convenience method.
	 * Remove the specified items from the specified bound list
	 * and fire the appropriate event if necessary.
	 * Return the removed items.
	 */
	protected List removeItemsFromList(int index, int length, List list, String listName) {
		List subList = list.subList(index, index + length);
		List removedItems = new ArrayList(subList);
		subList.clear();
		this.fireItemsRemoved(listName, index, removedItems);
		return removedItems;
	}

	/**
	 * Convenience method.
	 * Set the specified item in the specified bound list
	 * and fire the appropriate event if necessary.
	 * Return the replaced item.
	 */
	protected Object setItemInList(int index, Object item, List list, String listName) {
		Object replacedItem = list.set(index, item);
		this.fireItemReplaced(listName, index, item, replacedItem);
		return replacedItem;
	}

	/**
	 * Convenience method.
	 * Replace the specified item in the specified bound list
	 * and fire the appropriate event if necessary.
	 * Return the replaced item.
	 */
	protected Object replaceItemInList(Object oldItem, Object newItem, List list, String listName) {
		return this.setItemInList(list.indexOf(oldItem), newItem, list, listName);
	}

	/**
	 * Convenience method.
	 * Set the specified items in the specified bound list
	 * and fire the appropriate event if necessary.
	 * Return the replaced items.
	 */
	protected List setItemsInList(int index, List items, List list, String listName) {
		List subList = list.subList(index, index + items.size());
		List replacedItems = new ArrayList(subList);
		for (int i = 0; i < items.size(); i++) {
			subList.set(i, items.get(i));
		}
		this.fireItemsReplaced(listName, index, items, replacedItems);
		return replacedItems;
	}
	
	/**
	 * Convenience method.
	 * Clear the entire list
	 * and fire the appropriate event if necessary.
	 * Return whether the list changed.
	 */
	protected boolean clearList(List list, String listName) {
		if (list.isEmpty()) {
			return false;
		}
		list.clear();
		this.fireListChanged(listName);
		return true;
	}


	// ********** tree change support **********
	
	/**
	 * @see Model#addTreeChangeListener(TreeChangeListener)
	 */
	public synchronized void addTreeChangeListener(TreeChangeListener listener) {
		this.changeSupport().addTreeChangeListener(listener);
	}

	/**
	 * @see Model#addTreeChangeListener(String, TreeChangeListener)
	 */
	public synchronized void addTreeChangeListener(String treeName, TreeChangeListener listener) {
		this.changeSupport().addTreeChangeListener(treeName, listener);
	}
	
	/**
	 * @see Model#removeTreeChangeListener(TreeChangeListener)
	 */
	public synchronized void removeTreeChangeListener(TreeChangeListener listener) {
		this.changeSupport().removeTreeChangeListener(listener);
	}

	/**
	 * @see Model#removeTreeChangeListener(String, TreeChangeListener)
	 */
	public synchronized void removeTreeChangeListener(String treeName, TreeChangeListener listener) {
		this.changeSupport().removeTreeChangeListener(treeName, listener);
	}

	protected final void fireNodeAdded(String treeName, Object[] path) {
		this.changeSupport().fireNodeAdded(treeName, path);
	}
	
	protected final void fireNodeAdded(TreeChangeEvent evt) {
		this.changeSupport().fireNodeAdded(evt);
	}
	
	protected final void fireNodeRemoved(String treeName, Object[] path) {
		this.changeSupport().fireNodeRemoved(treeName, path);
	}

	protected final void fireNodeRemoved(TreeChangeEvent evt) {
		this.changeSupport().fireNodeRemoved(evt);
	}

	protected final void fireTreeStructureChanged(String treeName) {
		this.changeSupport().fireTreeChanged(treeName);
	}
	
	protected final void fireTreeStructureChanged(String treeName, Object[] path) {
		this.changeSupport().fireTreeChanged(treeName, path);
	}
	
	protected final void fireTreeStructureChanged(TreeChangeEvent evt) {
		this.changeSupport().fireTreeChanged(evt);
	}
	

	// ********** queries **********
	
	/**
	 * Return whether there are any state change listeners.
	 */
	public boolean hasAnyStateChangeListeners() {
		return this.changeSupport().hasAnyStateChangeListeners();
	}
	
	/**
	 * Return whether there are no state change listeners.
	 */
	public boolean hasNoStateChangeListeners() {
		return ! this.hasAnyStateChangeListeners();
	}
	
	/**
	 * Return whether there are any property change listeners for a specific property.
	 */
	public boolean hasAnyPropertyChangeListeners(String propertyName) {
		return this.changeSupport().hasAnyPropertyChangeListeners(propertyName);
	}
	
	/**
	 * Return whether there are any property change listeners for a specific property.
	 */
	public boolean hasNoPropertyChangeListeners(String propertyName) {
		return ! this.hasAnyPropertyChangeListeners(propertyName);
	}
	
	/**
	 * Return whether there are any collection change listeners for a specific collection.
	 */
	public boolean hasAnyCollectionChangeListeners(String collectionName) {
		return this.changeSupport().hasAnyCollectionChangeListeners(collectionName);
	}
	
	/**
	 * Return whether there are any collection change listeners for a specific collection.
	 */
	public boolean hasNoCollectionChangeListeners(String collectionName) {
		return ! this.hasAnyCollectionChangeListeners(collectionName);
	}
	
	/**
	 * Return whether there are any list change listeners for a specific list.
	 */
	public boolean hasAnyListChangeListeners(String listName) {
		return this.changeSupport().hasAnyListChangeListeners(listName);
	}
	
	/**
	 * Return whether there are any list change listeners for a specific list.
	 */
	public boolean hasNoListChangeListeners(String listName) {
		return ! this.hasAnyListChangeListeners(listName);
	}
	
	/**
	 * Return whether there are any tree change listeners for a specific tree.
	 */
	public boolean hasAnyTreeChangeListeners(String treeName) {
		return this.changeSupport().hasAnyTreeChangeListeners(treeName);
	}
	
	/**
	 * Return whether there are any tree change listeners for a specific tree.
	 */
	public boolean hasNoTreeChangeListeners(String treeName) {
		return ! this.hasAnyTreeChangeListeners(treeName);
	}
	

	// ********** convenience methods **********
	
	/**
	 * Return whether the values are equal, with the appropriate null checks.
	 * Convenience method for checking whether an attribute value has changed.
	 * 
	 * DO NOT use this to determine whether to fire a change notification,
	 * ChangeSupport already does that.
	 */
	protected final boolean valuesAreEqual(Object value1, Object value2) {
		return this.changeSupport().valuesAreEqual(value1, value2);
	}
	protected final boolean attributeValueHasNotChanged(Object oldValue, Object newValue) {
		return this.valuesAreEqual(oldValue, newValue);
	}
	
	
	/**
	 * Return whether the values are different, with the appropriate null checks.
	 * Convenience method for checking whether an attribute value has changed.
	 * 
	 * DO NOT use this to determine whether to fire a change notification,
	 * ChangeSupport already does that.
	 * 
	 * For example, after firing the change notification, you can use this method
	 * to decide if some other, related, piece of state needs to be synchronized
	 * with the state that just changed.
	 */
	protected final boolean valuesAreDifferent(Object value1, Object value2) {
		return this.changeSupport().valuesAreDifferent(value1, value2);
	}
	protected final boolean attributeValueHasChanged(Object oldValue, Object newValue) {
		return this.valuesAreDifferent(oldValue, newValue);
	}
	

	// ********** standard methods **********

	/**
	 * @see java.lang.Object#clone()
	 * Although cloning models is usually not a Good Idea,
	 * we should at least support it properly.
	 */
	protected Object clone() throws CloneNotSupportedException {
		AbstractModel clone = (AbstractModel) super.clone();
		clone.postClone();
		return clone;
	}

	/**
	 * Perform any post-clone processing necessary to
	 * successfully disconnect the clone from the original.
	 * When this method is called on the clone, the clone
	 * is a "shallow" copy of the original (i.e. the clone
	 * shares all its instance variables with the original).
	 */
	protected void postClone() {
		// clear out change support - models do not share listeners
		this.changeSupport = null;
	// when you override this method, don't forget to include:
	//	super.postClone();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		StringTools.buildSimpleToStringOn(this, sb);
		sb.append(" (");
		this.toString(sb);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * make this public so one model can call a nested model's
	 * #toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		// subclasses should override this to do something a bit more helpful
	}

}
