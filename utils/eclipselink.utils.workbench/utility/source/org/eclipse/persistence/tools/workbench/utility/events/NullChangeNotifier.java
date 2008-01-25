/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * Null implementation of ChangeNotifier interface: Do nothing.
 */
public final class NullChangeNotifier
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
			INSTANCE = new NullChangeNotifier();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private NullChangeNotifier() {
		super();
	}

	public void stateChanged(StateChangeListener listener, StateChangeEvent event) {
		// do nothing
	}

	public void propertyChange(PropertyChangeListener listener, PropertyChangeEvent event) {
		// do nothing
	}

	public void itemsAdded(CollectionChangeListener listener, CollectionChangeEvent event) {
		// do nothing
	}

	public void itemsRemoved(CollectionChangeListener listener, CollectionChangeEvent event) {
		// do nothing
	}

	public void collectionChanged(CollectionChangeListener listener, CollectionChangeEvent event) {
		// do nothing
	}

	public void itemsAdded(ListChangeListener listener, ListChangeEvent event) {
		// do nothing
	}

	public void itemsRemoved(ListChangeListener listener, ListChangeEvent event) {
		// do nothing
	}

	public void itemsReplaced(ListChangeListener listener, ListChangeEvent event) {
		// do nothing
	}

	public void listChanged(ListChangeListener listener, ListChangeEvent event) {
		// do nothing
	}

	public void nodeAdded(TreeChangeListener listener, TreeChangeEvent event) {
		// do nothing
	}

	public void nodeRemoved(TreeChangeListener listener, TreeChangeEvent event) {
		// do nothing
	}

	public void treeChanged(TreeChangeListener listener, TreeChangeEvent event) {
		// do nothing
	}

	/**
	 * Serializable singleton support
	 */
	private Object readResolve() {
		return instance();
	}

}
