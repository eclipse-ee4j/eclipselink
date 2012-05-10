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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;


/**
 * This extension of AspectAdapter provides ListChange support.
 * 
 * The typical subclass will override the following methods:
 * #getValueFromSubject()
 *     at the very minimum, override this method to return a list iterator
 *     on the subject's list aspect; it does not need to be overridden if
 *     #getValue() is overridden and its behavior changed
 * #getItem(int)
 *     override this method to improve performance
 * #sizeFromSubject()
 *     override this method to improve performance; it does not need to be overridden if
 *     #size() is overridden and its behavior changed
 * #addItem(int, Object) and #removeItem(int)
 *     override these methods if the client code needs to *change* the contents of
 *     the subject's list aspect; oftentimes, though, the client code
 *     (e.g. UI) will need only to *get* the value
 * #addItems(int, List) and #removeItems(int, int)
 *     override these methods to improve performance, if necessary
 * #getValue()
 *     override this method only if returning an empty list iterator when the
 *     subject is null is unacceptable
 * #size()
 *     override this method only if returning a zero when the
 *     subject is null is unacceptable
 */
public abstract class ListAspectAdapter
	extends AspectAdapter
	implements ListValueModel
{
	/**
	 * The name of the subject's list that we use for the value.
	 */
	protected String listName;

	/** A listener that listens to the subject's list aspect. */
	protected ListChangeListener listChangeListener;


	// ********** constructors **********

	/**
	 * Construct a ListAspectAdapter for the specified subject
	 * and list.
	 */
	protected ListAspectAdapter(String listName, Model subject) {
		super(subject);
		this.listName = listName;
	}

	/**
	 * Construct a ListAspectAdapter for the specified subject holder
	 * and list.
	 */
	protected ListAspectAdapter(ValueModel subjectHolder, String listName) {
		super(subjectHolder);
		this.listName = listName;
	}

	/**
	 * Construct a ListAspectAdapter for an "unchanging" list in
	 * the specified subject. This is useful for a list aspect that does not
	 * change for a particular subject; but the subject will change, resulting in
	 * a new list.
	 */
	protected ListAspectAdapter(ValueModel subjectHolder) {
		this(subjectHolder, null);
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.listChangeListener = this.buildListChangeListener();
	}

	/**
	 * The subject's list aspect has changed, notify the listeners.
	 */
	protected ListChangeListener buildListChangeListener() {
		// transform the subject's list change events into VALUE list change events
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ListAspectAdapter.this.itemsAdded(e);
			}
			public void itemsRemoved(ListChangeEvent e) {
				ListAspectAdapter.this.itemsRemoved(e);
			}
			public void itemsReplaced(ListChangeEvent e) {
				ListAspectAdapter.this.itemsReplaced(e);
			}
			public void listChanged(ListChangeEvent e) {
				ListAspectAdapter.this.listChanged(e);
			}
			public String toString() {
				return "list change listener: " + ListAspectAdapter.this.listName;
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * Return the value of the subject's list aspect.
	 * This should be a *list iterator* on the list.
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		if (this.subject == null) {
			return NullListIterator.instance();
		}
		return this.getValueFromSubject();
	}

	/**
	 * Return the value of the subject's list aspect.
	 * This should be a *list iterator* on the list.
	 * At this point we can be sure that the subject is not null.
	 * @see #getValue()
	 */
	protected ListIterator getValueFromSubject() {
		throw new UnsupportedOperationException();
	}


	// ********** ListValueModel implementation **********

	/**
	 * Insert the specified item in the subject's list aspect at the specified index.
	 * @see ListValueModel#addItem(int, Object)
	 */
	public void addItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Insert the specified items in the subject's list aspect at the specified index.
	 * @see ListValueModel#addItems(int, java.util.List)
	 */
	public void addItems(int index, List items) {
		for (int i = 0; i < items.size(); i++) {
			this.addItem(index + i, items.get(i));
		}
	}

	/**
	 * Remove the item at the specified index in the subject's list aspect.
	 * @see ListValueModel#removeItem(int)
	 */
	public Object removeItem(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Remove the items at the specified index in the subject's list aspect.
	 * @see ListValueModel#removeItems(int, int)
	 */
	public List removeItems(int index, int length) {
		List removedItems = new ArrayList(length);
		for (int i = 0; i < length; i++) {
			removedItems.add(this.removeItem(index));
		}
		return removedItems;
	}

	/**
	 * Replace the item at the specified index of the subject's list aspect.
	 * @see ListValueModel#replaceItem(int, Object)
	 */
	public Object replaceItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Replace the items at the specified index of the subject's list aspect.
	 * @see ListValueModel#replaceItems(int, java.util.List)
	 */
	public List replaceItems(int index, List items) {
		List replacedItems = new ArrayList(items.size());
		for (int i = 0; i < items.size(); i++) {
			replacedItems.add(this.replaceItem(index + i, items.get(i)));
		}
		return replacedItems;
	}

	/**
	 * Return the item at the specified index of the subject's list aspect.
	 * @see ListValueModel#getItem(int)
	 */
	public Object getItem(int index) {
		return CollectionTools.get((ListIterator) this.getValue(), index);
	}

	/**
	 * Return the size of the subject's list aspect.
	 * @see ListValueModel#size()
	 */
	public int size() {
		return this.subject == null ? 0 : this.sizeFromSubject();
	}

	/**
	 * Return the size of the subject's list aspect.
	 * At this point we can be sure that the subject is not null.
	 * @see #size()
	 */
	protected int sizeFromSubject() {
		return CollectionTools.size((ListIterator) this.getValue());
	}


	// ********** AspectAdapter implementation **********

	/**
	 * @see AspectAdapter#hasListeners()
	 */
	protected boolean hasListeners() {
		return this.hasAnyListChangeListeners(VALUE);
	}

	/**
	 * @see AspectAdapter#fireAspectChange(Object, Object)
	 */
	protected void fireAspectChange(Object oldValue, Object newValue) {
		this.fireListChanged(VALUE);
	}

	/**
	 * @see AspectAdapter#engageNonNullSubject()
	 */
	protected void engageNonNullSubject() {
		if (this.listName != null) {
			((Model) this.subject).addListChangeListener(this.listName, this.listChangeListener);
		}
	}

	/**
	 * @see AspectAdapter#disengageNonNullSubject()
	 */
	protected void disengageNonNullSubject() {
		if (this.listName != null) {
			((Model) this.subject).removeListChangeListener(this.listName, this.listChangeListener);
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.listName);
	}


	// ********** behavior **********

	protected void itemsAdded(ListChangeEvent e) {
		this.fireItemsAdded(e.cloneWithSource(ListAspectAdapter.this, VALUE));
	}

	protected void itemsRemoved(ListChangeEvent e) {
		this.fireItemsRemoved(e.cloneWithSource(ListAspectAdapter.this, VALUE));
	}

	protected void itemsReplaced(ListChangeEvent e) {
		this.fireItemsReplaced(e.cloneWithSource(ListAspectAdapter.this, VALUE));
	}

	protected void listChanged(ListChangeEvent e) {
		this.fireListChanged(VALUE);
	}

}
