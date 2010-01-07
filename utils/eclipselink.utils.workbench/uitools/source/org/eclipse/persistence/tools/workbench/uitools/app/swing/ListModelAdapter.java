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
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This javax.swing.ListModel can be used to keep a ListDataListener
 * (e.g. a JList) in synch with a ListValueModel (or a CollectionValueModel).
 * 
 * An instance of this ListModel *must* be supplied with a value model,
 * which is a ListValueModel on the bound list or a CollectionValueModel
 * on the bound collection. This is required - the list (or collection)
 * itself can be null, but the value model that holds it cannot.
 */
public class ListModelAdapter extends AbstractListModel {

	/** A value model on the underlying model list. */
	protected ListValueModel listHolder;

	/**
	 * Cache the size of the list for "dramatic" changes.
	 * @see #listChanged(ListChangeEvent)
	 */
	protected int listSize;

	/** A listener that allows us to forward changes made to the underlying model list. */
	protected ListChangeListener listChangeListener;


	// ********** constructors **********

	/**
	 * Default constructor - initialize stuff.
	 */
	private ListModelAdapter() {
		super();
		this.initialize();
	}

	/**
	 * Constructor - the list holder is required.
	 */
	public ListModelAdapter(ListValueModel listHolder) {
		this();
		this.setModel(listHolder);
	}

	/**
	 * Constructor - the collection holder is required.
	 */
	public ListModelAdapter(CollectionValueModel collectionHolder) {
		this();
		this.setModel(collectionHolder);
	}


	// ********** initialization **********

	protected void initialize() {
		this.listSize = 0;
		this.listChangeListener = this.buildListChangeListener();
	}

	protected ListChangeListener buildListChangeListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ListModelAdapter.this.itemsAdded(e);
			}
			public void itemsRemoved(ListChangeEvent e) {
				ListModelAdapter.this.itemsRemoved(e);
			}
			public void itemsReplaced(ListChangeEvent e) {
				ListModelAdapter.this.itemsReplaced(e);
			}
			public void listChanged(ListChangeEvent e) {
				ListModelAdapter.this.listChanged();
			}
			public String toString() {
				return "list listener";
			}
		};
	}


	// ********** ListModel implementation **********

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return this.listHolder.size();
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return this.listHolder.getItem(index);
	}

	/**
	 * Extend to start listening to the underlying model list if necessary.
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l) {
		if (this.hasNoListDataListeners()) {
			this.engageModel();
			this.listSize = this.listHolder.size();
		}
		super.addListDataListener(l);
	}

	/**
	 * Extend to stop listening to the underlying model list if appropriate.
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		super.removeListDataListener(l);
		if (this.hasNoListDataListeners()) {
			this.disengageModel();
			this.listSize = 0;
		}
	}


	// ********** public API **********

	/**
	 * Return the underlying list model.
	 */
	public ListValueModel getModel() {
		return this.listHolder;
	}
	
	/**
	 * Set the underlying list model.
	 */
	public void setModel(ListValueModel listHolder) {
		if (listHolder == null) {
			throw new NullPointerException();
		}
		boolean hasListeners = this.hasListDataListeners();
		if (hasListeners) {
			this.disengageModel();
		}
		this.listHolder = listHolder;
		if (hasListeners) {
			this.engageModel();
			this.listChanged();
		}
	}

	/**
	 * Set the underlying collection model.
	 */
	public void setModel(CollectionValueModel collectionHolder) {
		this.setModel(new CollectionListValueModelAdapter(collectionHolder));
	}


	// ********** queries **********

	/**
	 * Return whether this model has no listeners.
	 */
	protected boolean hasNoListDataListeners() {
		return this.getListDataListeners().length == 0;
	}

	/**
	 * Return whether this model has any listeners.
	 */
	protected boolean hasListDataListeners() {
		return ! this.hasNoListDataListeners();
	}


	// ********** behavior **********

	protected void engageModel() {
		this.listHolder.addListChangeListener(ValueModel.VALUE, this.listChangeListener);
	}

	protected void disengageModel() {
		this.listHolder.removeListChangeListener(ValueModel.VALUE, this.listChangeListener);
	}



	// ********** list change support **********

	/**
	 * Items were added to the underlying model list.
	 * Notify listeners of the changes.
	 */
	protected void itemsAdded(ListChangeEvent e) {
		int start = e.getIndex();
		int end = start + e.size() - 1;
		this.fireIntervalAdded(this, start, end);
		this.listSize += e.size();
	}

	/**
	 * Items were removed from the underlying model list.
	 * Notify listeners of the changes.
	 */
	protected void itemsRemoved(ListChangeEvent e) {
		int start = e.getIndex();
		int end = start + e.size() - 1;
		this.fireIntervalRemoved(this, start, end);
		this.listSize -= e.size();
	}

	/**
	 * Items were replaced in the underlying model list.
	 * Notify listeners of the changes.
	 */
	protected void itemsReplaced(ListChangeEvent e) {
		int start = e.getIndex();
		int end = start + e.size() - 1;
		this.fireContentsChanged(this, start, end);
	}

	/**
	 * The underlying model list has changed "dramatically".
	 * Notify listeners of the changes.
	 */
	protected void listChanged() {
		if (this.listSize != 0) {
			this.fireIntervalRemoved(this, 0, this.listSize - 1);
		}
		this.listSize = this.listHolder.size();
		if (this.listSize != 0) {
			this.fireIntervalAdded(this, 0, this.listSize - 1);
		} else {
			// this can happen when the "context" of the list changes;
			// even though there are no items in the list,
			// listeners might want to know that the list has "changed"
			this.fireContentsChanged(this, 0, -1);
		}
	}


	// ********** Object overrides **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.listHolder);
	}

}
