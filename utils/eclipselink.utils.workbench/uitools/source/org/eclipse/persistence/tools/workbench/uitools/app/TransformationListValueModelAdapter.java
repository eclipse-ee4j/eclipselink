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
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;


/**
 * An adapter that allows us to transform a ListValueModel
 * (or CollectionValueModel) into a read-only ListValueModel
 * whose items are tranformations of the items in the wrapped
 * ListValueModel. It will keep its contents in synch with
 * the contents of the wrapped ListValueModel and notifies its
 * listeners of any changes.
 * 
 * To use, supply a <code>Transformer</code> or subclass
 * <code>TransformationListValueModelAdapter</code>
 * and override the <code>transformItem(Object)</code> method.
 * 
 * NB: Since we only listen to the wrapped list when we have
 * listeners ourselves and we can only stay in synch with the wrapped
 * list while we are listening to it, results to various methods
 * (e.g. #size(), getItem(int)) will be unpredictable whenever
 * we do not have any listeners. This should not be too painful since,
 * most likely, client objects will also be listeners.
 */
public class TransformationListValueModelAdapter extends ListValueModelWrapper {

	/** This transforms the items, unless the subclass overrides #transformItem(Object). */
	protected final Transformer transformer;

	/** The list of transformed items. */
	protected List transformedList;


	// ********** constructors **********

	/**
	 * Constructor - the list holder is required.
	 */
	public TransformationListValueModelAdapter(ListValueModel listHolder, Transformer transformer) {
		super(listHolder);
		this.transformer = transformer;
	}

	/**
	 * Constructor - the list holder is required.
	 */
	public TransformationListValueModelAdapter(ListValueModel listHolder) {
		this(listHolder, Transformer.NULL_INSTANCE);
	}

	/**
	 * Constructor - the collection holder is required.
	 */
	public TransformationListValueModelAdapter(CollectionValueModel collectionHolder, Transformer transformer) {
		this(new CollectionListValueModelAdapter(collectionHolder), transformer);
	}

	/**
	 * Constructor - the collection holder is required.
	 */
	public TransformationListValueModelAdapter(CollectionValueModel collectionHolder) {
		this(collectionHolder, Transformer.NULL_INSTANCE);
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.transformedList = new ArrayList();
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		// try to prevent backdoor modification of the list
		return new ReadOnlyListIterator(this.transformedList);
	}


	// ********** ListValueModel implementation **********

	/**
	 * @see ListValueModel#addItem(int, java.lang.Object)
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
	 * @see ListValueModel#replaceItem(int, java.lang.Object)
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
		return this.transformedList.get(index);
	}

	/**
	 * @see ListValueModel#size()
	 */
	public int size() {
		return this.transformedList.size();
	}


	// ********** behavior **********

	protected void engageModel() {
		super.engageModel();
		// synch the transformed list *after* we start listening to the list holder,
		// since its value might change when a listener is added
		this.transformedList.addAll(this.transformItems(this.listHolder));
	}

	protected void disengageModel() {
		super.disengageModel();
		// clear out the list when we are not listening to the collection holder
		this.transformedList.clear();
	}

	/**
	 * Transform the items associated with the specified event.
	 */
	protected List transformItems(ListChangeEvent e) {
		return this.transformItems(e.items(), e.size());
	}

	/**
	 * Transform the items in the specified list value model.
	 */
	protected List transformItems(ListValueModel lvm) {
		return this.transformItems((ListIterator) lvm.getValue(), lvm.size());
	}

	/**
	 * Transform the replaced items associated with the specified event.
	 */
	protected List transformReplacedItems(ListChangeEvent e) {
		return this.transformItems(e.replacedItems(), e.size());
	}

	/**
	 * Transform the specified items.
	 */
	protected List transformItems(ListIterator items, int size) {
		List result = new ArrayList(size);
		while (items.hasNext()) {
			result.add(this.transformItem(items.next()));
		}
		return result;
	}

	/**
	 * Transform the specified item.
	 */
	protected Object transformItem(Object item) {
		return this.transformer.transform(item);
	}


	// ********** list change support **********

	/**
	 * Items were added to the wrapped list holder.
	 * Transform them, add them to our transformation list,
	 * and notify our listeners.
	 */
	protected void itemsAdded(ListChangeEvent e) {
		this.addItemsToList(e.getIndex(), this.transformItems(e), this.transformedList, VALUE);
	}

	/**
	 * Items were removed from the wrapped list holder.
	 * Remove the corresponding items from our transformation list
	 * and notify our listeners.
	 */
	protected void itemsRemoved(ListChangeEvent e) {
		this.removeItemsFromList(e.getIndex(), e.size(), this.transformedList, VALUE);
	}

	/**
	 * Items were replaced in the wrapped list holder.
	 * Replace the corresponding items in our transformation list
	 * and notify our listeners.
	 */
	protected void itemsReplaced(ListChangeEvent e) {
		this.setItemsInList(e.getIndex(), this.transformItems(e), this.transformedList, VALUE);
	}

	/**
	 * The wrapped list holder has changed in some dramatic fashion.
	 * Reconfigure our listeners and forward the event.
	 */
	protected void listChanged(ListChangeEvent e) {
		this.transformedList.clear();
		this.transformedList.addAll(this.transformItems(this.listHolder));
		this.fireListChanged(VALUE);
	}

}
