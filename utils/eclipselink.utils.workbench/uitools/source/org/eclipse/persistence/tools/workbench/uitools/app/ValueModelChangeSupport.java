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

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent;


/**
 * This support class changes the behavior of the standard
 * ChangeSupport in several ways:
 * 	- The source must be a ValueModel.
 * 	- All events fired by the source must specify the VALUE aspect name.
 * 	- Listeners are required to be either VALUE listeners or
 * 		"generic" listeners.
 * 	- VALUE listeners are stored alongside the "generic" listeners,
 * 		improving performance a bit (in terms of both time and space)
 */
public class ValueModelChangeSupport extends ChangeSupport {
	private static final long serialVersionUID = 1L;

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#ChangeSupport(Object)
	 */
	public ValueModelChangeSupport(ValueModel source) {
		super(source);
	}


	// ******************** internal behavior ********************

	private UnsupportedOperationException unsupportedOperationException() {
		return new UnsupportedOperationException("ValueModels only support VALUE changes");
	}

	private void checkAspectName(String aspectName) {
		if (aspectName != ValueModel.VALUE) {
			throw new IllegalArgumentException("ValueModels only support VALUE changes: " + aspectName);
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#addListener(String, Class, Object)
	 */
	protected void addListener(String aspectName, Class listenerClass, Object listener) {
		this.checkAspectName(aspectName);
		// redirect to "generic" listeners collection
		this.addListener(listenerClass, listener);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#removeListener(String, Class, Object)
	 */
	protected void removeListener(String aspectName, Class listenerClass, Object listener) {
		this.checkAspectName(aspectName);
		// redirect to "generic" listeners collection
		this.removeListener(listenerClass, listener);
	}


	// ******************** internal queries ********************

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#hasAnyListeners(Class, String)
	 */
	protected boolean hasAnyListeners(Class listenerClass, String aspectName) {
		this.checkAspectName(aspectName);
		// redirect to "generic" listeners collection
		return this.hasAnyListeners(listenerClass);
	}


	// ******************** state change support ********************

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireStateChanged()
	 */
	public void fireStateChanged() {
		throw this.unsupportedOperationException();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireStateChanged(org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent)
	 */
	public void fireStateChanged(StateChangeEvent event) {
		throw this.unsupportedOperationException();
	}


	// ******************** property change support ********************

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#firePropertyChanged(java.beans.PropertyChangeEvent)
	 */
	public void firePropertyChanged(PropertyChangeEvent event) {
		this.checkAspectName(event.getPropertyName());
		super.firePropertyChanged(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#firePropertyChanged(String, Object, Object)
	 */
	public void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		this.checkAspectName(propertyName);
		super.firePropertyChanged(propertyName, oldValue, newValue);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#firePropertyChanged(String, int, int)
	 */
	public void firePropertyChanged(String propertyName, int oldValue, int newValue) {
		this.checkAspectName(propertyName);
		super.firePropertyChanged(propertyName, oldValue, newValue);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#firePropertyChanged(String, boolean, boolean)
	 */
	public void firePropertyChanged(String propertyName, boolean oldValue, boolean newValue) {
		this.checkAspectName(propertyName);
		super.firePropertyChanged(propertyName, oldValue, newValue);
	}


	// ******************** collection change support ********************

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsAdded(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	public void fireItemsAdded(CollectionChangeEvent event) {
		this.checkAspectName(event.getCollectionName());
		super.fireItemsAdded(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsAdded(String, java.util.Collection)
	 */
	public void fireItemsAdded(String collectionName, Collection addedItems) {
		this.checkAspectName(collectionName);
		super.fireItemsAdded(collectionName, addedItems);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemAdded(String, Object)
	 */
	public void fireItemAdded(String collectionName, Object addedItem) {
		this.checkAspectName(collectionName);
		super.fireItemAdded(collectionName, addedItem);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	public void fireItemsRemoved(CollectionChangeEvent event) {
		this.checkAspectName(event.getCollectionName());
		super.fireItemsRemoved(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsRemoved(String, java.util.Collection)
	 */
	public void fireItemsRemoved(String collectionName, Collection removedItems) {
		this.checkAspectName(collectionName);
		super.fireItemsRemoved(collectionName, removedItems);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemRemoved(String, Object)
	 */
	public void fireItemRemoved(String collectionName, Object removedItem) {
		this.checkAspectName(collectionName);
		super.fireItemRemoved(collectionName, removedItem);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireCollectionChanged(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	public void fireCollectionChanged(CollectionChangeEvent event) {
		this.checkAspectName(event.getCollectionName());
		super.fireCollectionChanged(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireCollectionChanged(String)
	 */
	public void fireCollectionChanged(String collectionName) {
		this.checkAspectName(collectionName);
		super.fireCollectionChanged(collectionName);
	}


	// ******************** list change support ********************

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsAdded(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void fireItemsAdded(ListChangeEvent event) {
		this.checkAspectName(event.getListName());
		super.fireItemsAdded(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsAdded(String, int, java.util.List)
	 */
	public void fireItemsAdded(String listName, int index, List addedItems) {
		this.checkAspectName(listName);
		super.fireItemsAdded(listName, index, addedItems);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemAdded(String, int, Object)
	 */
	public void fireItemAdded(String listName, int index, Object addedItem) {
		this.checkAspectName(listName);
		super.fireItemAdded(listName, index, addedItem);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void fireItemsRemoved(ListChangeEvent event) {
		this.checkAspectName(event.getListName());
		super.fireItemsRemoved(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsRemoved(String, int, java.util.List)
	 */
	public void fireItemsRemoved(String listName, int index, List removedItems) {
		this.checkAspectName(listName);
		super.fireItemsRemoved(listName, index, removedItems);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemRemoved(String, int, Object)
	 */
	public void fireItemRemoved(String listName, int index, Object removedItem) {
		this.checkAspectName(listName);
		super.fireItemRemoved(listName, index, removedItem);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsReplaced(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void fireItemsReplaced(ListChangeEvent event) {
		this.checkAspectName(event.getListName());
		super.fireItemsReplaced(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemsReplaced(String, int, java.util.List, java.util.List)
	 */
	public void fireItemsReplaced(String listName, int index, List newItems, List replacedItems) {
		this.checkAspectName(listName);
		super.fireItemsReplaced(listName, index, newItems, replacedItems);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireItemReplaced(String, int, Object, Object)
	 */
	public void fireItemReplaced(String listName, int index, Object newItem, Object replacedItem) {
		this.checkAspectName(listName);
		super.fireItemReplaced(listName, index, newItem, replacedItem);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireListChanged(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void fireListChanged(ListChangeEvent event) {
		this.checkAspectName(event.getListName());
		super.fireListChanged(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireListChanged(String)
	 */
	public void fireListChanged(String listName) {
		this.checkAspectName(listName);
		super.fireListChanged(listName);
	}


	// ******************** tree change support ********************

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireNodeAdded(org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent)
	 */
	public void fireNodeAdded(TreeChangeEvent event) {
		this.checkAspectName(event.getTreeName());
		super.fireNodeAdded(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireNodeAdded(String, Object[])
	 */
	public void fireNodeAdded(String treeName, Object[] path) {
		this.checkAspectName(treeName);
		super.fireNodeAdded(treeName, path);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireNodeRemoved(org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent)
	 */
	public void fireNodeRemoved(TreeChangeEvent event) {
		this.checkAspectName(event.getTreeName());
		super.fireNodeRemoved(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireNodeRemoved(String, Object[])
	 */
	public void fireNodeRemoved(String treeName, Object[] path) {
		this.checkAspectName(treeName);
		super.fireNodeRemoved(treeName, path);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireTreeChanged(org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent)
	 */
	public void fireTreeChanged(TreeChangeEvent event) {
		this.checkAspectName(event.getTreeName());
		super.fireTreeChanged(event);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport#fireTreeChanged(String, Object[])
	 */
	public void fireTreeChanged(String treeName, Object[] path) {
		this.checkAspectName(treeName);
		super.fireTreeChanged(treeName, path);
	}

}
