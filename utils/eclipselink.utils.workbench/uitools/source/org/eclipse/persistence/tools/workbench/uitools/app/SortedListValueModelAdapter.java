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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Range;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;


/**
 * An adapter that allows us to make a CollectionValueModel
 * (or ListValueModel) behave like a read-only ListValueModel
 * that keeps its contents sorted and notifies listeners appropriately.
 * 
 * The comparator can be changed at any time; allowing the same
 * adapter to be used with different sort criteria (e.g. when the user
 * wants to sort a list of files first by name, then by date, then by size).
 * 
 * NB: Since we only listen to the wrapped collection when we have
 * listeners ourselves and we can only stay in synch with the wrapped
 * collection while we are listening to it, results to various methods
 * (e.g. #size(), getItem(int)) will be unpredictable whenever
 * we do not have any listeners. This should not be too painful since,
 * most likely, client objects will also be listeners.
 */
public class SortedListValueModelAdapter
	extends CollectionListValueModelAdapter
{
	/**
	 * A comparator used for sorting the elements;
	 * if it is null, we use "natural ordering".
	 */
	protected Comparator comparator;


	// ********** constructors **********

	/**
	 * Wrap the specified collection value model and sort its contents
	 * using the specified comparator.
	 */
	public SortedListValueModelAdapter(CollectionValueModel collectionHolder, Comparator comparator) {
		super(collectionHolder);
		this.comparator = comparator;
	}

	/**
	 * Wrap the specified collection value model and sort its contents
	 * based on the elements' "natural ordering".
	 */
	public SortedListValueModelAdapter(CollectionValueModel collectionHolder) {
		this(collectionHolder, null);
	}

	/**
	 * Wrap the specified list value model and sort its contents
	 * using the specified comparator.
	 */
	public SortedListValueModelAdapter(ListValueModel listHolder, Comparator comparator) {
		this(new ListCollectionValueModelAdapter(listHolder), comparator);
	}

	/**
	 * Wrap the specified list value model and sort its contents
	 * based on the elements' "natural ordering".
	 */
	public SortedListValueModelAdapter(ListValueModel listHolder) {
		this(listHolder, null);
	}


	// ********** accessors **********

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
		this.sortList();
	}


	// ********** behavior **********

	/**
	 * Sort the internal list before
	 * sending out change notification.
	 */
	protected void postBuildList() {
		super.postBuildList();
		Collections.sort(this.list, this.comparator);
	}

	/**
	 * the list will need to be sorted after the item is added
	 */
	protected void itemsAdded(CollectionChangeEvent e) {
		// first add the items and notify our listeners...
		super.itemsAdded(e);
		// ...then sort the list
		this.sortList();
	}

	/**
	 * sort the list and notify our listeners, if necessary;
	 */
	protected void sortList() {
		// save the unsorted state of the sorted list so we can minimize the number of "replaced" items
		ArrayList unsortedList = (ArrayList) this.list.clone();
		Collections.sort(this.list, this.comparator);
		Range diffRange = CollectionTools.identityDiffRange(unsortedList, this.list);
		if (diffRange.size > 0) {
			List unsortedItems = unsortedList.subList(diffRange.start, diffRange.end + 1);
			List sortedItems = this.list.subList(diffRange.start, diffRange.end + 1);
			this.fireItemsReplaced(VALUE, diffRange.start, sortedItems, unsortedItems);
		}
	}

}
