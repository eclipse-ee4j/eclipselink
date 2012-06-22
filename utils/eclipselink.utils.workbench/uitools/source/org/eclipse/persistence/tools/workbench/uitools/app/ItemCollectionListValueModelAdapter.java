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

import java.util.Arrays;

import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


/**
 * Extend ItemAspectListValueModelAdapter to listen to one or more collection
 * aspects of each item in the wrapped list model.
 */
public class ItemCollectionListValueModelAdapter extends ItemAspectListValueModelAdapter {

	/** The names of the items' collections that we listen to. */
	protected final String[] collectionNames;

	/** Listener that listens to all the items in the list. */
	protected CollectionChangeListener itemCollectionListener;


	// ********** constructors **********

	/**
	 * Construct an adapter for the specified item Collection.
	 */
	public ItemCollectionListValueModelAdapter(ListValueModel listHolder, String collectionName) {
		this(listHolder, new String[] {collectionName});
	}

	/**
	 * Construct an adapter for the specified item Collections.
	 */
	public ItemCollectionListValueModelAdapter(ListValueModel listHolder, String collectionName1, String collectionName2) {
		this(listHolder, new String[] {collectionName1, collectionName2});
	}

	/**
	 * Construct an adapter for the specified item Collections.
	 */
	public ItemCollectionListValueModelAdapter(ListValueModel listHolder, String collectionName1, String collectionName2, String collectionName3) {
		this(listHolder, new String[] {collectionName1, collectionName2, collectionName3});
	}

	/**
	 * Construct an adapter for the specified item Collections.
	 */
	public ItemCollectionListValueModelAdapter(ListValueModel listHolder, String[] collectionNames) {
		super(listHolder);
		this.collectionNames = collectionNames;
	}

	/**
	 * Construct an adapter for the specified item Collections.
	 */
	public ItemCollectionListValueModelAdapter(CollectionValueModel collectionHolder, String collectionName) {
		this(collectionHolder, new String[] {collectionName});
	}

	/**
	 * Construct an adapter for the specified item Collections.
	 */
	public ItemCollectionListValueModelAdapter(CollectionValueModel collectionHolder, String collectionName1, String collectionName2) {
		this(collectionHolder, new String[] {collectionName1, collectionName2});
	}

	/**
	 * Construct an adapter for the specified item Collections.
	 */
	public ItemCollectionListValueModelAdapter(CollectionValueModel collectionHolder, String collectionName1, String collectionName2, String collectionName3) {
		this(collectionHolder, new String[] {collectionName1, collectionName2, collectionName3});
	}

	/**
	 * Construct an adapter for the specified item Collections.
	 */
	public ItemCollectionListValueModelAdapter(CollectionValueModel collectionHolder, String[] collectionNames) {
		super(collectionHolder);
		this.collectionNames = collectionNames;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.itemCollectionListener = this.buildItemCollectionListener();
	}

	/**
	 * All we really care about is the fact that a Collection aspect has 
	 * changed.  Do the same thing no matter which event occurs.
	 */
	protected CollectionChangeListener buildItemCollectionListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				ItemCollectionListValueModelAdapter.this.itemAspectChanged(e);
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				ItemCollectionListValueModelAdapter.this.itemAspectChanged(e);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				ItemCollectionListValueModelAdapter.this.itemAspectChanged(e);
			}
			public String toString() {
				return "item collection listener: " + Arrays.asList(ItemCollectionListValueModelAdapter.this.collectionNames);
			}
		};
	}


	// ********** behavior **********

	/**
	 * @see ItemAspectListValueModelAdapter#listenToItem(org.eclipse.persistence.tools.workbench.utility.Model)
	 */
	protected void startListeningToItem(Model item) {
		for (int i = this.collectionNames.length; i-- > 0; ) {
			item.addCollectionChangeListener(this.collectionNames[i], this.itemCollectionListener);
		}
	}

	/**
	 * @see ItemAspectListValueModelAdapter#stopListeningToItem(org.eclipse.persistence.tools.workbench.utility.Model)
	 */
	protected void stopListeningToItem(Model item) {
		for (int i = this.collectionNames.length; i-- > 0; ) {
			item.removeCollectionChangeListener(this.collectionNames[i], this.itemCollectionListener);
		}
	}

}
