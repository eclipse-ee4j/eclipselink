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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.eclipse.persistence.tools.workbench.utility.Model;


/**
 * Extend ItemAspectListValueModelAdapter to listen to one or more
 * properties of each item in the wrapped list model.
 */
public class ItemPropertyListValueModelAdapter extends ItemAspectListValueModelAdapter {

	/** The names of the items' properties that we listen to. */
	protected final String[] propertyNames;

	/** Listener that listens to all the items in the list. */
	protected PropertyChangeListener itemPropertyListener;


	// ********** constructors **********

	/**
	 * Construct an adapter for the specified item property.
	 */
	public ItemPropertyListValueModelAdapter(ListValueModel listHolder, String propertyName) {
		this(listHolder, new String[] {propertyName});
	}

	/**
	 * Construct an adapter for the specified item properties.
	 */
	public ItemPropertyListValueModelAdapter(ListValueModel listHolder, String propertyName1, String propertyName2) {
		this(listHolder, new String[] {propertyName1, propertyName2});
	}

	/**
	 * Construct an adapter for the specified item properties.
	 */
	public ItemPropertyListValueModelAdapter(ListValueModel listHolder, String propertyName1, String propertyName2, String propertyName3) {
		this(listHolder, new String[] {propertyName1, propertyName2, propertyName3});
	}

	/**
	 * Construct an adapter for the specified item properties.
	 */
	public ItemPropertyListValueModelAdapter(ListValueModel listHolder, String[] propertyNames) {
		super(listHolder);
		this.propertyNames = propertyNames;
	}

	/**
	 * Construct an adapter for the specified item property.
	 */
	public ItemPropertyListValueModelAdapter(CollectionValueModel collectionHolder, String propertyName) {
		this(collectionHolder, new String[] {propertyName});
	}

	/**
	 * Construct an adapter for the specified item properties.
	 */
	public ItemPropertyListValueModelAdapter(CollectionValueModel collectionHolder, String propertyName1, String propertyName2) {
		this(collectionHolder, new String[] {propertyName1, propertyName2});
	}

	/**
	 * Construct an adapter for the specified item properties.
	 */
	public ItemPropertyListValueModelAdapter(CollectionValueModel collectionHolder, String propertyName1, String propertyName2, String propertyName3) {
		this(collectionHolder, new String[] {propertyName1, propertyName2, propertyName3});
	}

	/**
	 * Construct an adapter for the specified item properties.
	 */
	public ItemPropertyListValueModelAdapter(CollectionValueModel collectionHolder, String[] propertyNames) {
		super(collectionHolder);
		this.propertyNames = propertyNames;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.itemPropertyListener = this.buildItemPropertyListener();
	}

	protected PropertyChangeListener buildItemPropertyListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ItemPropertyListValueModelAdapter.this.itemAspectChanged(e);
			}
			public String toString() {
				return "item property listener: " + Arrays.asList(ItemPropertyListValueModelAdapter.this.propertyNames);
			}
		};
	}
	

	// ********** behavior **********

	protected void startListeningToItem(Model item) {
		for (int i = this.propertyNames.length; i-- > 0; ) {
			item.addPropertyChangeListener(this.propertyNames[i], this.itemPropertyListener);
		}
	}

	protected void stopListeningToItem(Model item) {
		for (int i = this.propertyNames.length; i-- > 0; ) {
			item.removePropertyChangeListener(this.propertyNames[i], this.itemPropertyListener);
		}
	}

}
