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

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


/**
 * This abstract class provides the infrastructure needed to wrap
 * another list value model, "lazily" listen to it, and propagate
 * its change notifications.
 */
public abstract class ListValueModelWrapper
	extends AbstractModel
	implements ListValueModel
{

	/** The wrapped list value model. */
	protected ListValueModel listHolder;

	/** A listener that allows us to synch with changes to the wrapped list holder. */
	protected ListChangeListener listChangeListener;


	// ********** constructors **********

	/**
	 * Construct a list value model with the specified wrapped
	 * list value model.
	 */
	protected ListValueModelWrapper(ListValueModel listHolder) {
		super();
		if (listHolder == null) {
			throw new NullPointerException();
		}
		this.listHolder = listHolder;
	}
	

	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.listChangeListener = this.buildListChangeListener();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}

	protected ListChangeListener buildListChangeListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ListValueModelWrapper.this.itemsAdded(e);
			}
			public void itemsRemoved(ListChangeEvent e) {
				ListValueModelWrapper.this.itemsRemoved(e);
			}
			public void itemsReplaced(ListChangeEvent e) {
				ListValueModelWrapper.this.itemsReplaced(e);
			}
			public void listChanged(ListChangeEvent e) {
				ListValueModelWrapper.this.listChanged(e);
			}
			public String toString() {
				return "list change listener";
			}
		};
	}


	// ********** extend change support **********

	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see Model#addListChangeListener(ListChangeListener)
	 */
	public synchronized void addListChangeListener(ListChangeListener listener) {
		if (this.hasNoListChangeListeners(VALUE)) {
			this.engageModel();
		}
		super.addListChangeListener(listener);
	}
	
	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see Model#addListChangeListener(String, ListChangeListener)
	 */
	public synchronized void addListChangeListener(String listName, ListChangeListener listener) {
		if (listName == VALUE && this.hasNoListChangeListeners(VALUE)) {
			this.engageModel();
		}
		super.addListChangeListener(listName, listener);
	}
	
	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see Model#removeListChangeListener(ListChangeListener)
	 */
	public synchronized void removeListChangeListener(ListChangeListener listener) {
		super.removeListChangeListener(listener);
		if (this.hasNoListChangeListeners(VALUE)) {
			this.disengageModel();
		}
	}
	
	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see Model#removeListChangeListener(String, ListChangeListener)
	 */
	public synchronized void removeListChangeListener(String listName, ListChangeListener listener) {
		super.removeListChangeListener(listName, listener);
		if (listName == VALUE && this.hasNoListChangeListeners(VALUE)) {
			this.disengageModel();
		}
	}
	

	// ********** behavior **********

	/**
	 * Start listening to the list holder.
	 */
	protected void engageModel() {
		this.listHolder.addListChangeListener(VALUE, this.listChangeListener);
	}

	/**
	 * Stop listening to the list holder.
	 */
	protected void disengageModel() {
		this.listHolder.removeListChangeListener(VALUE, this.listChangeListener);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.tools.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.listHolder);
	}


	// ********** list change support **********

	/**
	 * Items were added to the wrapped list holder;
	 * propagate the change notification appropriately.
	 */
	protected abstract void itemsAdded(ListChangeEvent e);

	/**
	 * Items were removed from the wrapped list holder;
	 * propagate the change notification appropriately.
	 */
	protected abstract void itemsRemoved(ListChangeEvent e);

	/**
	 * Items were replaced in the wrapped list holder;
	 * propagate the change notification appropriately.
	 */
	protected abstract void itemsReplaced(ListChangeEvent e);

	/**
	 * The value of the wrapped list holder has changed;
	 * propagate the change notification appropriately.
	 */
	protected abstract void listChanged(ListChangeEvent e);

}
