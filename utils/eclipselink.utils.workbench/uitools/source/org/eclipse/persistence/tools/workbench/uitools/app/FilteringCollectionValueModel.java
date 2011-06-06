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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;


/**
 * A <code>FilteringCollectionValueModel</code> wraps another
 * <code>CollectionValueModel</code> and uses a <code>Filter</code>
 * to determine which items in the collection are returned by calls
 * to <code>getValue()</code>.
 * <p>
 * As an alternative to building a <code>Filter</code>, a subclass
 * of <code>FilteringCollectionValueModel</code> can override the
 * <code>accept(Object)</code> method.
 * <p>
 * NB: If the objects in the "filtered" collection can change in such a way
 * that they should be removed from the "filtered" collection, you will
 * need to wrap the original collection in an ItemAspectListValueModelAdapter.
 * For example, if the filter only "accepts" items whose names begin
 * with "X" and the names of the items can change, you will need to
 * wrap the original list of unfiltered items with an
 * ItemPropertyListValueModelAdapter that listens for changes to each
 * item's name and fires the appropriate event whenever an item's name
 * changes. The event will cause this wrapper to re-filter the changed
 * item and add or remove it from the "filtered" collection as appropriate.
 */
public class FilteringCollectionValueModel
	extends CollectionValueModelWrapper
{
	/** This filters the items in the nested collection. */
	private Filter filter;

	/** This filters the items in the nested collection. */
	private Filter localFilter;

	/** Cache the items that were accepted by the filter */
	private Collection filteredItems;


	// ********** constructors **********

	/**
	 * Construct a collection value model with the specified wrapped
	 * collection value model and a filter that simply accepts every object.
	 * Use this constructor if you want to override the
	 * <code>accept(Object)</code> method
	 * instead of building a <code>Filter</code>.
	 */
	public FilteringCollectionValueModel(CollectionValueModel collectionHolder) {
		this(collectionHolder, Filter.NULL_INSTANCE);
	}

	/**
	 * Construct a collection value model with the specified wrapped
	 * collection value model and filter.
	 */
	public FilteringCollectionValueModel(CollectionValueModel collectionHolder, Filter filter) {
		super(collectionHolder);
		this.filter = filter;
	}

	/**
	 * Construct a collection value model with the specified wrapped
	 * list value model and a filter that simply accepts every object.
	 * Use this constructor if you want to override the
	 * <code>accept(Object)</code> method
	 * instead of building a <code>Filter</code>.
	 */
	public FilteringCollectionValueModel(ListValueModel listHolder) {
		this(new ListCollectionValueModelAdapter(listHolder));
	}

	/**
	 * Construct a collection value model with the specified wrapped
	 * list value model and filter.
	 */
	public FilteringCollectionValueModel(ListValueModel listHolder, Filter filter) {
		this(new ListCollectionValueModelAdapter(listHolder), filter);
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.localFilter = this.buildLocalFilter();
		this.filteredItems = new ArrayList();
	}

	/**
	 * Implement the filter by calling back to the collection
	 * value model. This allows us to keep the method
	 * #accept(Object) protected.
	 */
	protected Filter buildLocalFilter() {
		return new Filter() {
			public boolean accept(Object o) {
				return FilteringCollectionValueModel.this.accept(o);
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		return this.filteredItems.iterator();
	}


	// ********** CollectionValueModel implementation **********

	/**
	 * @see CollectionValueModel#size()
	 */
	public int size() {
		return this.filteredItems.size();
	}


	// ********** CollectionValueModelWrapper overrides/implementation **********

	/**
	 * @see CollectionValueModelWrapper#engageModel()
	 */
	protected void engageModel() {
		super.engageModel();
		// synch our cache *after* we start listening to the nested collection,
		// since its value might change when a listener is added
		this.synchFilteredItems();
	}

	/**
	 * @see CollectionValueModelWrapper#disengageModel()
	 */
	protected void disengageModel() {
		super.disengageModel();
		// clear out the cache when we are not listening to the nested collection
		this.filteredItems.clear();
	}

	/**
	 * @see CollectionValueModelWrapper#itemsAdded(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	protected void itemsAdded(CollectionChangeEvent e) {
		// filter the values before propagating the change event
		this.addItemsToCollection(this.filter(e.items()), this.filteredItems, VALUE);
	}

	/**
	 * @see CollectionValueModelWrapper#itemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	protected void itemsRemoved(CollectionChangeEvent e) {
		// do NOT filter the values, because they may no longer be
		// "accepted" and that might be why they were removed in the first place;
		// anyway, any extraneous items are harmless
		this.removeItemsFromCollection(e.items(), this.filteredItems, VALUE);
	}

	/**
	 * @see CollectionValueModelWrapper#collectionChanged(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	protected void collectionChanged(CollectionChangeEvent e) {
		this.synchFilteredItems();
		this.fireCollectionChanged(VALUE);
	}


	// ********** queries **********

	/**
	 * Return whether the <code>FilteringCollectionValueModel</code> should
	 * include the specified value in the iterator returned from a call to the
	 * <code>getValue()</code> method; the value came
	 * from the nested collection value model.
	 * <p>
	 * This method can be overridden by a subclass as an
	 * alternative to building a <code>Filter</code>.
	 */
	protected boolean accept(Object value) {
		return this.filter.accept(value);
	}

	/**
	 * Return an iterator that filters the specified iterator.
	 */
	protected Iterator filter(Iterator items) {
		return new FilteringIterator(items, this.localFilter);
	}


	// ********** behavior **********

	/**
	 * Synchronize our cache with the wrapped collection.
	 */
	protected void synchFilteredItems() {
		this.filteredItems.clear();
		CollectionTools.addAll(this.filteredItems, this.filter((Iterator) this.collectionHolder.getValue()));
	}

}
