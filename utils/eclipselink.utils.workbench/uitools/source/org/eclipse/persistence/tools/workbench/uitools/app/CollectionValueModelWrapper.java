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


import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


/**
 * This abstract class provides the infrastructure needed to wrap
 * another collection value model, "lazily" listen to it, and propagate
 * its change notifications.
 */
public abstract class CollectionValueModelWrapper
	extends AbstractModel
	implements CollectionValueModel
{


	/** The wrapped collection value model. */
	protected CollectionValueModel collectionHolder;

	/** A listener that allows us to synch with changes to the wrapped collection holder. */
	protected CollectionChangeListener collectionChangeListener;


	// ********** constructors **********

	/**
	 * Construct a collection value model with the specified wrapped
	 * collection value model.
	 */
	protected CollectionValueModelWrapper(CollectionValueModel collectionHolder) {
		super();
		this.collectionHolder = collectionHolder;
	}
	

	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.collectionChangeListener = this.buildCollectionChangeListener();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}

	protected CollectionChangeListener buildCollectionChangeListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				CollectionValueModelWrapper.this.itemsAdded(e);
			}		
			public void itemsRemoved(CollectionChangeEvent e) {
				CollectionValueModelWrapper.this.itemsRemoved(e);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				CollectionValueModelWrapper.this.collectionChanged(e);
			}
			public String toString() {
				return "collection change listener";
			}
		};
	}


	// ********** extend change support **********

	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see Model#addCollectionChangeListener(CollectionChangeListener)
	 */
	public synchronized void addCollectionChangeListener(CollectionChangeListener listener) {
		if (this.hasNoCollectionChangeListeners(VALUE)) {
			this.engageModel();
		}
		super.addCollectionChangeListener(listener);
	}
	
	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see Model#addCollectionChangeListener(String, CollectionChangeListener)
	 */
	public synchronized void addCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		if (collectionName == VALUE && this.hasNoCollectionChangeListeners(VALUE)) {
			this.engageModel();
		}
		super.addCollectionChangeListener(collectionName, listener);
	}
	
	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see Model#removeCollectionChangeListener(CollectiontChangeListener)
	 */
	public synchronized void removeCollectionChangeListener(CollectionChangeListener listener) {
		super.removeCollectionChangeListener(listener);
		if (this.hasNoCollectionChangeListeners(VALUE)) {
			this.disengageModel();
		}
	}
	
	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see Model#removeCollectionChangeListener(String, CollectionChangeListener)
	 */
	public synchronized void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
		super.removeCollectionChangeListener(collectionName, listener);
		if (collectionName == VALUE && this.hasNoCollectionChangeListeners(VALUE)) {
			this.disengageModel();
		}
	}


	// ********** CollectionValueModel implementation **********

	/**
	 * wrappers cannot be modified - the underlying model must be modified directly
	 * @see CollectionValueModel#addItem(Object)
	 */
	public void addItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#addItems(java.util.Collection)
	 */
	public void addItems(Collection items) {
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			this.addItem(stream.next());
		}
	}

	/**
	 * wrappers cannot be modified - the underlying model must be modified directly
	 * @see CollectionValueModel#removeItem(Object)
	 */
	public void removeItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#removeItems(java.util.Collection)
	 */
	public void removeItems(Collection items) {
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			this.removeItem(stream.next());
		}
	}


	// ********** behavior **********

	/**
	 * Start listening to the collection holder.
	 */
	protected void engageModel() {
		this.collectionHolder.addCollectionChangeListener(VALUE, this.collectionChangeListener);
	}

	/**
	 * Stop listening to the collection holder.
	 */
	protected void disengageModel() {
		this.collectionHolder.removeCollectionChangeListener(VALUE, this.collectionChangeListener);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.tools.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.collectionHolder);
	}


	// ********** collection change support **********

	/**
	 * Items were added to the wrapped collection holder;
	 * propagate the change notification appropriately.
	 */
	protected abstract void itemsAdded(CollectionChangeEvent e);

	/**
	 * Items were removed from the wrapped collection holder;
	 * propagate the change notification appropriately.
	 */
	protected abstract void itemsRemoved(CollectionChangeEvent e);

	/**
	 * The value of the wrapped collection holder has changed;
	 * propagate the change notification appropriately.
	 */
	protected abstract void collectionChanged(CollectionChangeEvent e);

}
