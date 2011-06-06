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

import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


/**
 * This abstract class provides the infrastructure needed to wrap
 * a collection value model, "lazily" listen to it, and convert
 * its change notifications into property value model change
 * notifications.
 * 
 * Subclasses must override:
 * - #buildValue()
 *     to return the current property value, as derived from the
 *     current collection value
 * 
 * Subclasses might want to override:
 * - #setValue(Object)
 *     to manipulate the collection in some appropriate fashion
 * - #itemsAdded(CollectionChangeEvent e)
 * - #itemsRemoved(CollectionChangeEvent e)
 * - #collectionChanged(CollectionChangeEvent e)
 *     to improve performance (by not recalculating the value, if possible)
 */
public abstract class CollectionPropertyValueModelAdapter
	extends AbstractModel
	implements PropertyValueModel
{
	/**
	 * Cache the current value so we can pass an "old value" when
	 * we fire a property change event.
	 * We need this because the value may be calculated and we may
	 * not able to derive the "old value" from the collection
	 * change event fired by the collection value model.
	 */
	protected Object value;

	/** The wrapped collection value model. */
	protected CollectionValueModel collectionHolder;

	/** A listener that allows us to synch with changes to the wrapped collection holder. */
	protected CollectionChangeListener collectionChangeListener;


	// ********** constructor/initialization **********

	/**
	 * Construct a property value model with the specified wrapped
	 * collection value model.
	 */
	protected CollectionPropertyValueModelAdapter(CollectionValueModel collectionHolder) {
		super();
		this.collectionHolder = collectionHolder;
	}

	protected void initialize() {
		super.initialize();
		// our value is null when we are not listening to the collection holder
		this.value = null;
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
				CollectionPropertyValueModelAdapter.this.itemsAdded(e);
			}		
			public void itemsRemoved(CollectionChangeEvent e) {
				CollectionPropertyValueModelAdapter.this.itemsRemoved(e);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				CollectionPropertyValueModelAdapter.this.collectionChanged(e);
			}
			public String toString() {
				return "collection change listener";
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * Return the cached value.
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		return this.value;
	}


	// ********** PropertyValueModel implementation **********

	/**
	 * @see PropertyValueModel#setValue(Object)
	 */
	public void setValue(Object value) {
		throw new UnsupportedOperationException();
	}


	// ********** extend change support **********

	/**
	 * Extend to start listening to the wrapped collection if necessary.
	 * @see Model#addPropertyChangeListener(PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.hasNoListeners()) {
			this.engageModel();
		}
		super.addPropertyChangeListener(listener);
	}
	
	/**
	 * Extend to start listening to the wrapped collection if necessary.
	 * @see Model#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (propertyName == VALUE && this.hasNoListeners()) {
			this.engageModel();
		}
		super.addPropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * Extend to stop listening to the wrapped collection if necessary.
	 * @see Model#removePropertyChangeListener(PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		if (this.hasNoListeners()) {
			this.disengageModel();
		}
	}
	
	/**
	 * Extend to stop listening to the wrapped collection if necessary.
	 * @see Model#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		if (propertyName == VALUE && this.hasNoListeners()) {
			this.disengageModel();
		}
	}


	// ********** queries **********

	/**
	 * Return whether there are any listeners for the aspect.
	 */
	protected boolean hasListeners() {
		return this.hasAnyPropertyChangeListeners(VALUE);
	}

	/**
	 * Return whether there are any listeners for the aspect.
	 */
	protected boolean hasNoListeners() {
		return ! this.hasListeners();
	}


	// ********** behavior **********

	/**
	 * Start listening to the collection holder.
	 */
	protected void engageModel() {
		this.collectionHolder.addCollectionChangeListener(VALUE, this.collectionChangeListener);
		// synch our value *after* we start listening to the collection,
		// since the collection's value might change when a listener is added
		this.value = this.buildValue();
	}

	/**
	 * Build and return the current value, as derived from the
	 * current state of the wrapped collection.
	 */
	protected abstract Object buildValue();

	/**
	 * Stop listening to the collection holder.
	 */
	protected void disengageModel() {
		this.collectionHolder.removeCollectionChangeListener(VALUE, this.collectionChangeListener);
		// clear out our value when we are not listening to the collection
		this.value = null;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.collectionHolder);
	}

	
	// ********** collection change support **********

	/**
	 * Items were added to the wrapped collection holder;
	 * propagate the change notification appropriately.
	 */
	protected void itemsAdded(CollectionChangeEvent e) {
		// by default, simply recalculate the value and fire an event
		this.propertyChanged();
	}

	/**
	 * Items were removed from the wrapped collection holder;
	 * propagate the change notification appropriately.
	 */
	protected void itemsRemoved(CollectionChangeEvent e) {
		// by default, simply recalculate the value and fire an event
		this.propertyChanged();
	}

	/**
	 * The value of the wrapped collection holder has changed;
	 * propagate the change notification appropriately.
	 */
	protected void collectionChanged(CollectionChangeEvent e) {
		// by default, simply recalculate the value and fire an event
		this.propertyChanged();
	}

	/**
	 * The wrapped collection changed in some fashion.
	 * Recalculate the value and notify any listeners.
	 */
	protected void propertyChanged() {
		Object old = this.value;
		this.value = this.buildValue();
		this.firePropertyChanged(VALUE, old, this.value);
	}

}
