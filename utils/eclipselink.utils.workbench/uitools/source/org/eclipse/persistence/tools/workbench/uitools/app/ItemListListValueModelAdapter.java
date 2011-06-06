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

import java.util.Arrays;

import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


/**
 * Extend ItemAspectListValueModelAdapter to listen to one or more list
 * aspects of each item in the wrapped list model.
 */
public class ItemListListValueModelAdapter extends ItemAspectListValueModelAdapter {

	/** The names of the subject's lists that we listen to. */
	protected final String[] listNames;

	/** Listener that listens to all the items in the list. */
	protected ListChangeListener itemListListener;


	// ********** constructors **********

	/**
	 * Construct an adapter for the specified item List aspect.
	 */
	public ItemListListValueModelAdapter(ListValueModel listHolder, String listName) {
		this(listHolder, new String[] {listName});
	}

	/**
	 * Construct an adapter for the specified item List aspects.
	 */
	public ItemListListValueModelAdapter(ListValueModel listHolder, String listName1, String listName2) {
		this(listHolder, new String[] {listName1, listName2});
	}

	/**
	 * Construct an adapter for the specified item List aspects.
	 */
	public ItemListListValueModelAdapter(ListValueModel listHolder, String listName1, String listName2, String listName3) {
		this(listHolder, new String[] {listName1, listName2, listName3});
	}

	/**
	 * Construct an adapter for the specified item List aspects.
	 */
	public ItemListListValueModelAdapter(ListValueModel listHolder, String[] listNames) {
		super(listHolder);
		this.listNames = listNames;
	}

	/**
	 * Construct an adapter for the specified item List aspect.
	 */
	public ItemListListValueModelAdapter(CollectionValueModel collectionHolder, String listName) {
		this(collectionHolder, new String[] {listName});
	}

	/**
	 * Construct an adapter for the specified item List aspects.
	 */
	public ItemListListValueModelAdapter(CollectionValueModel collectionHolder, String listName1, String listName2) {
		this(collectionHolder, new String[] {listName1, listName2});
	}

	/**
	 * Construct an adapter for the specified item List aspects.
	 */
	public ItemListListValueModelAdapter(CollectionValueModel collectionHolder, String listName1, String listName2, String listName3) {
		this(collectionHolder, new String[] {listName1, listName2, listName3});
	}

	/**
	 * Construct an adapter for the specified item List aspects.
	 */
	public ItemListListValueModelAdapter(CollectionValueModel collectionHolder, String[] listNames) {
		super(collectionHolder);
		this.listNames = listNames;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.itemListListener = this.buildItemListListener();
	}

	/**
	 * All we really care about is the fact that the List aspect has 
	 * changed.  Do the same thing no matter which event occurs.
	 */
	protected ListChangeListener buildItemListListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ItemListListValueModelAdapter.this.itemAspectChanged(e);
			}
			public void itemsRemoved(ListChangeEvent e) {
				ItemListListValueModelAdapter.this.itemAspectChanged(e);
			}
			public void itemsReplaced(ListChangeEvent e) {
				ItemListListValueModelAdapter.this.itemAspectChanged(e);
			}
			public void listChanged(ListChangeEvent e) {
				ItemListListValueModelAdapter.this.itemAspectChanged(e);
			}
			public String toString() {
				return "item list listener: " + Arrays.asList(ItemListListValueModelAdapter.this.listNames);
			}
		};
	}
	

	// ********** behavior **********

	/**
	 * @see ItemAspectListValueModelAdapter#listenToItem(org.eclipse.persistence.tools.workbench.utility.Model)
	 */
	protected void startListeningToItem(Model item) {
		for (int i = this.listNames.length; i-- > 0; ) {
			item.addListChangeListener(this.listNames[i], this.itemListListener);
		}
	}

	/**
	 * @see ItemAspectListValueModelAdapter#stopListeningToItem(org.eclipse.persistence.tools.workbench.utility.Model)
	 */
	protected void stopListeningToItem(Model item) {
		for (int i = this.listNames.length; i-- > 0; ) {
			item.removeListChangeListener(this.listNames[i], this.itemListListener);
		}
	}

}
