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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * Straightforward implementation of ChangeNotifier interface:
 * Just forward the change notification directly to the listener.
 */
public final class DefaultChangeNotifier
	implements ChangeNotifier, Serializable
{
	// singleton
	private static ChangeNotifier INSTANCE;

	private static final long serialVersionUID = 1L;


	/**
	 * Return the singleton.
	 */
	public synchronized static ChangeNotifier instance() {
		if (INSTANCE == null) {
			INSTANCE = new DefaultChangeNotifier();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private DefaultChangeNotifier() {
		super();
	}

	/**
	 * @see ChangeNotifier#stateChanged(StateChangeListener, StateChangeEvent)
	 */
	public void stateChanged(StateChangeListener listener, StateChangeEvent event) {
		listener.stateChanged(event);
	}

	/**
	 * @see ChangeNotifier#propertyChange(java.beans.PropertyChangeListener, java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeListener listener, PropertyChangeEvent event) {
		listener.propertyChange(event);
	}

	/**
	 * @see ChangeNotifier#itemsAdded(CollectionChangeListener, CollectionChangeEvent)
	 */
	public void itemsAdded(CollectionChangeListener listener, CollectionChangeEvent event) {
		listener.itemsAdded(event);
	}

	/**
	 * @see ChangeNotifier#itemsRemoved(CollectionChangeListener, CollectionChangeEvent)
	 */
	public void itemsRemoved(CollectionChangeListener listener, CollectionChangeEvent event) {
		listener.itemsRemoved(event);
	}

	/**
	 * @see ChangeNotifier#collectionChanged(CollectionChangeListener, CollectionChangeEvent)
	 */
	public void collectionChanged(CollectionChangeListener listener, CollectionChangeEvent event) {
		listener.collectionChanged(event);
	}

	/**
	 * @see ChangeNotifier#itemsAdded(ListChangeListener, ListChangeEvent)
	 */
	public void itemsAdded(ListChangeListener listener, ListChangeEvent event) {
		listener.itemsAdded(event);
	}

	/**
	 * @see ChangeNotifier#itemsRemoved(ListChangeListener, ListChangeEvent)
	 */
	public void itemsRemoved(ListChangeListener listener, ListChangeEvent event) {
		listener.itemsRemoved(event);
	}

	/**
	 * @see ChangeNotifier#itemsReplaced(ListChangeListener, ListChangeEvent)
	 */
	public void itemsReplaced(ListChangeListener listener, ListChangeEvent event) {
		listener.itemsReplaced(event);
	}

	/**
	 * @see ChangeNotifier#listChanged(ListChangeListener, ListChangeEvent)
	 */
	public void listChanged(ListChangeListener listener, ListChangeEvent event) {
		listener.listChanged(event);
	}

	/**
	 * @see ChangeNotifier#nodeAdded(TreeChangeListener, TreeChangeEvent)
	 */
	public void nodeAdded(TreeChangeListener listener, TreeChangeEvent event) {
		listener.nodeAdded(event);
	}

	/**
	 * @see ChangeNotifier#nodeRemoved(TreeChangeListener, TreeChangeEvent)
	 */
	public void nodeRemoved(TreeChangeListener listener, TreeChangeEvent event) {
		listener.nodeRemoved(event);
	}

	/**
	 * @see ChangeNotifier#treeChanged(TreeChangeListener, TreeChangeEvent)
	 */
	public void treeChanged(TreeChangeListener listener, TreeChangeEvent event) {
		listener.treeChanged(event);
	}

	/**
	 * Serializable singleton support
	 */
	private Object readResolve() {
		return instance();
	}

}
